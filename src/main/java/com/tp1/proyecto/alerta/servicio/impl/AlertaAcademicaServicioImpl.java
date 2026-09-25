package com.tp1.proyecto.alerta.servicio.impl;

import com.tp1.proyecto.academico.entidad.DiaSemana;
import com.tp1.proyecto.academico.entidad.HorarioSemanal;
import com.tp1.proyecto.academico.entidad.Matricula;
import com.tp1.proyecto.academico.entidad.PeriodoEvaluacion;
import com.tp1.proyecto.academico.entidad.DocenteCursoSeccion;
import com.tp1.proyecto.academico.repositorio.HorarioSemanalRepositorio;
import com.tp1.proyecto.academico.repositorio.MatriculaRepositorio;
import com.tp1.proyecto.academico.repositorio.PeriodoEvaluacionRepositorio;
import com.tp1.proyecto.alerta.dto.AlertaAcademicaRespuestaDto;
import com.tp1.proyecto.alerta.entidad.AlertaAcademica;
import com.tp1.proyecto.alerta.repositorio.AlertaAcademicaRepositorio;
import com.tp1.proyecto.comun.enumeracion.EstadoRegistro;
import com.tp1.proyecto.docente.entidad.Docente;
import com.tp1.proyecto.evaluacion.entidad.Evaluacion;
import com.tp1.proyecto.evaluacion.repositorio.AsistenciaSesionRepositorio;
import com.tp1.proyecto.evaluacion.repositorio.DetalleNotaEvaluacionRepositorio;
import com.tp1.proyecto.evaluacion.repositorio.EvaluacionRepositorio;
import com.tp1.proyecto.seguridad.servicio.UsuarioAutenticado;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AlertaAcademicaServicioImpl implements com.tp1.proyecto.alerta.servicio.AlertaAcademicaServicio {
    private static final ZoneId ZONA_LIMA = ZoneId.of("America/Lima");
    private static final String PENDIENTE = "PENDIENTE";
    private static final String ATENDIDA = "ATENDIDA";
    private static final String ASISTENCIA = "ASISTENCIA_PENDIENTE";
    private static final String NOTAS = "NOTAS_PENDIENTES";

    private final AlertaAcademicaRepositorio alertaRepositorio;
    private final HorarioSemanalRepositorio horarioRepositorio;
    private final PeriodoEvaluacionRepositorio periodoEvaluacionRepositorio;
    private final MatriculaRepositorio matriculaRepositorio;
    private final AsistenciaSesionRepositorio asistenciaRepositorio;
    private final EvaluacionRepositorio evaluacionRepositorio;
    private final DetalleNotaEvaluacionRepositorio detalleRepositorio;

    public AlertaAcademicaServicioImpl(
        AlertaAcademicaRepositorio alertaRepositorio,
        HorarioSemanalRepositorio horarioRepositorio,
        PeriodoEvaluacionRepositorio periodoEvaluacionRepositorio,
        MatriculaRepositorio matriculaRepositorio,
        AsistenciaSesionRepositorio asistenciaRepositorio,
        EvaluacionRepositorio evaluacionRepositorio,
        DetalleNotaEvaluacionRepositorio detalleRepositorio
    ) {
        this.alertaRepositorio = alertaRepositorio;
        this.horarioRepositorio = horarioRepositorio;
        this.periodoEvaluacionRepositorio = periodoEvaluacionRepositorio;
        this.matriculaRepositorio = matriculaRepositorio;
        this.asistenciaRepositorio = asistenciaRepositorio;
        this.evaluacionRepositorio = evaluacionRepositorio;
        this.detalleRepositorio = detalleRepositorio;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertaAcademicaRespuestaDto> listar(String estado, UsuarioAutenticado actor) {
        List<AlertaAcademica> alertas = alertaRepositorio.findAllByOrderByFechaLimiteDesc().stream()
            .filter(a -> estado == null || estado.isBlank() || "TODAS".equalsIgnoreCase(estado)
                || a.getEstadoAlerta().equalsIgnoreCase(estado))
            .filter(a -> a.getEstado() == EstadoRegistro.ACTIVO)
            .filter(a -> puedeVer(a, actor))
            .toList();
        return alertas.stream().map(this::mapear).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long contarPendientes(UsuarioAutenticado actor) {
        return alertaRepositorio.findByEstadoAlertaOrderByFechaLimiteDesc(PENDIENTE).stream()
            .filter(a -> a.getEstado() == EstadoRegistro.ACTIVO)
            .filter(a -> puedeVer(a, actor))
            .count();
    }

    @Override
    @Scheduled(cron = "0 */5 * * * *", zone = "America/Lima")
    @Transactional
    public void detectarPendientes() {
        LocalDateTime ahora = LocalDateTime.now(ZONA_LIMA);
        LocalDate hoy = ahora.toLocalDate();

        List<HorarioSemanal> horariosActivos = horarioRepositorio.findByEstado(EstadoRegistro.ACTIVO);
        for (HorarioSemanal horario : horariosActivos) {
            if (!esInicioSesion(horario, horariosActivos)) {
                alertaRepositorio.findByClaveOrigen("ASISTENCIA:" + horario.getId() + ":" + hoy)
                    .ifPresent(alerta -> {
                        alerta.setEstado(EstadoRegistro.INACTIVO);
                        alertaRepositorio.save(alerta);
                    });
                continue;
            }
            DocenteCursoSeccion asignacion = horario.getAsignacion();
            if (asignacion.getEstado() != EstadoRegistro.ACTIVO
                || asignacion.getPeriodoAcademico().getEstado() != EstadoRegistro.ACTIVO) continue;
            Long periodoId = asignacion.getPeriodoAcademico().getId();
            LocalDate desde = max(hoy.minusDays(7), asignacion.getPeriodoAcademico().getFechaInicio());
            LocalDate hasta = min(hoy, asignacion.getPeriodoAcademico().getFechaFin());
            for (LocalDate fecha = desde; !fecha.isAfter(hasta); fecha = fecha.plusDays(1)) {
                if (fecha.getDayOfWeek() == DayOfWeek.SATURDAY || fecha.getDayOfWeek() == DayOfWeek.SUNDAY) continue;
                if (diaSemana(fecha.getDayOfWeek()) != horario.getDiaSemana()) continue;
                LocalDate fechaClase = fecha;
                LocalDateTime limite = LocalDateTime.of(fechaClase, finSesion(horario, horariosActivos));
                if (ahora.isBefore(limite)) continue;
                PeriodoEvaluacion periodo = periodoEvaluacionRepositorio.findByPeriodoAcademicoId(periodoId).stream()
                    .filter(p -> p.getEstado() == EstadoRegistro.ACTIVO)
                    .filter(p -> !fechaClase.isBefore(p.getFechaInicio()) && !fechaClase.isAfter(p.getFechaFin()))
                    .findFirst().orElse(null);
                if (periodo != null) revisarAsistencia(horario, periodo, fechaClase, limite);
            }
        }

        revisarNotas(hoy);
        resolverAlertasCompletadas();
    }

    private void revisarAsistencia(HorarioSemanal horario, PeriodoEvaluacion periodo, LocalDate fecha, LocalDateTime limite) {
        DocenteCursoSeccion asignacion = horario.getAsignacion();
        List<Matricula> matriculas = matriculaRepositorio.findBySeccionIdAndPeriodoAcademicoIdAndEstado(
            asignacion.getSeccion().getId(), asignacion.getPeriodoAcademico().getId(), EstadoRegistro.ACTIVO
        );
        if (matriculas.isEmpty()) return;
        Set<Long> registradas = new HashSet<>(asistenciaRepositorio
            .findByHorarioSemanalIdAndAsignacionIdAndPeriodoEvaluacionIdAndFechaClaseAndEstado(
                horario.getId(), asignacion.getId(), periodo.getId(), fecha, EstadoRegistro.ACTIVO
            ).stream().map(a -> a.getMatricula().getId()).toList());
        int pendientes = (int) matriculas.stream().filter(m -> !registradas.contains(m.getId())).count();
        String clave = "ASISTENCIA:" + horario.getId() + ":" + fecha;
        guardarAlerta(clave, ASISTENCIA, asignacion, horario, null, fecha, limite, pendientes);
    }

    private boolean esInicioSesion(HorarioSemanal horario, List<HorarioSemanal> horarios) {
        return horarios.stream()
            .filter(otro -> otro.getAsignacion().getId().equals(horario.getAsignacion().getId()))
            .filter(otro -> otro.getDiaSemana() == horario.getDiaSemana())
            .noneMatch(otro -> otro.getBloque().getHoraFin().equals(horario.getBloque().getHoraInicio()));
    }

    private LocalTime finSesion(HorarioSemanal inicio, List<HorarioSemanal> horarios) {
        LocalTime fin = inicio.getBloque().getHoraFin();
        boolean avanzo;
        do {
            LocalTime finActual = fin;
            HorarioSemanal siguiente = horarios.stream()
                .filter(otro -> otro.getAsignacion().getId().equals(inicio.getAsignacion().getId()))
                .filter(otro -> otro.getDiaSemana() == inicio.getDiaSemana())
                .filter(otro -> otro.getBloque().getHoraInicio().equals(finActual))
                .findFirst().orElse(null);
            avanzo = siguiente != null;
            if (siguiente != null) fin = siguiente.getBloque().getHoraFin();
        } while (avanzo);
        return fin;
    }

    private void revisarNotas(LocalDate hoy) {
        for (Evaluacion evaluacion : evaluacionRepositorio.findByDocenteCursoSeccionEstadoAndEstadoAndFechaEvaluacionIsNotNull(
            EstadoRegistro.ACTIVO, EstadoRegistro.ACTIVO
        )) {
            DocenteCursoSeccion asignacion = evaluacion.getDocenteCursoSeccion();
            LocalDate fecha = evaluacion.getFechaEvaluacion();
            if (asignacion.getPeriodoAcademico().getEstado() == EstadoRegistro.ACTIVO
                && !fecha.isBefore(asignacion.getPeriodoAcademico().getFechaInicio())
                && !fecha.isAfter(asignacion.getPeriodoAcademico().getFechaFin())
                && fecha.isBefore(hoy)) {
                revisarEvaluacion(evaluacion);
            }
        }
    }

    private void revisarEvaluacion(Evaluacion evaluacion) {
        LocalDate fechaEvaluacion = evaluacion.getFechaEvaluacion();
        DocenteCursoSeccion asignacion = evaluacion.getDocenteCursoSeccion();
        List<Matricula> matriculas = matriculaRepositorio.findBySeccionIdAndPeriodoAcademicoIdAndEstado(
            asignacion.getSeccion().getId(), asignacion.getPeriodoAcademico().getId(), EstadoRegistro.ACTIVO
        );
        if (matriculas.isEmpty()) return;
        Set<Long> idsMatricula = new HashSet<>(matriculas.stream().map(Matricula::getId).toList());
        Set<Long> registradas = new HashSet<>(detalleRepositorio.findByEvaluacionIdAndEstado(
            evaluacion.getId(), EstadoRegistro.ACTIVO
        ).stream().map(d -> d.getMatricula().getId()).filter(idsMatricula::contains).toList());
        int pendientes = (int) matriculas.stream().filter(m -> !registradas.contains(m.getId())).count();
        guardarAlerta("NOTAS:" + evaluacion.getId(), NOTAS, asignacion, null, evaluacion,
            fechaEvaluacion, fechaEvaluacion.plusDays(1).atStartOfDay(), pendientes);
    }

    private void guardarAlerta(
        String clave, String tipo, DocenteCursoSeccion asignacion, HorarioSemanal horario,
        Evaluacion evaluacion, LocalDate fecha, LocalDateTime limite, int pendientes
    ) {
        AlertaAcademica alerta = alertaRepositorio.findByClaveOrigen(clave).orElseGet(AlertaAcademica::new);
        alerta.setClaveOrigen(clave);
        alerta.setTipo(tipo);
        alerta.setAsignacion(asignacion);
        alerta.setHorario(horario);
        alerta.setEvaluacion(evaluacion);
        alerta.setFechaReferencia(fecha);
        alerta.setFechaLimite(limite);
        alerta.setCantidadPendiente(pendientes);
        alerta.setEstadoAlerta(pendientes == 0 ? ATENDIDA : PENDIENTE);
        alerta.setEstado(EstadoRegistro.ACTIVO);
        alertaRepositorio.save(alerta);
    }

    private void resolverAlertasCompletadas() {
        List<HorarioSemanal> horariosActivos = horarioRepositorio.findByEstado(EstadoRegistro.ACTIVO);
        for (AlertaAcademica alerta : alertaRepositorio.findByEstadoAlertaOrderByFechaLimiteDesc(PENDIENTE)) {
            if (alerta.getAsignacion().getEstado() != EstadoRegistro.ACTIVO
                || alerta.getAsignacion().getPeriodoAcademico().getEstado() != EstadoRegistro.ACTIVO) {
                alerta.setEstado(EstadoRegistro.INACTIVO);
                alertaRepositorio.save(alerta);
                continue;
            }
            if (ASISTENCIA.equals(alerta.getTipo()) && alerta.getHorario() != null) {
                if (!esInicioSesion(alerta.getHorario(), horariosActivos)) {
                    alerta.setEstado(EstadoRegistro.INACTIVO);
                    alertaRepositorio.save(alerta);
                    continue;
                }
                if (alerta.getHorario().getEstado() != EstadoRegistro.ACTIVO) {
                    alerta.setEstado(EstadoRegistro.INACTIVO);
                    alertaRepositorio.save(alerta);
                    continue;
                }
                PeriodoEvaluacion periodo = periodoEvaluacionRepositorio.findByPeriodoAcademicoId(
                    alerta.getAsignacion().getPeriodoAcademico().getId()
                ).stream().filter(p -> !alerta.getFechaReferencia().isBefore(p.getFechaInicio())
                    && !alerta.getFechaReferencia().isAfter(p.getFechaFin())).findFirst().orElse(null);
                if (periodo != null) revisarAsistencia(alerta.getHorario(), periodo,
                    alerta.getFechaReferencia(), LocalDateTime.of(alerta.getFechaReferencia(), finSesion(alerta.getHorario(), horariosActivos)));
            } else if (NOTAS.equals(alerta.getTipo()) && alerta.getEvaluacion() != null) {
                if (alerta.getEvaluacion().getEstado() != EstadoRegistro.ACTIVO
                    || alerta.getEvaluacion().getFechaEvaluacion() == null
                    || !alerta.getEvaluacion().getFechaEvaluacion().isBefore(LocalDate.now(ZONA_LIMA))) {
                    alerta.setEstado(EstadoRegistro.INACTIVO);
                    alertaRepositorio.save(alerta);
                    continue;
                }
                revisarEvaluacion(alerta.getEvaluacion());
            }
        }
    }

    private boolean puedeVer(AlertaAcademica alerta, UsuarioAutenticado actor) {
        boolean directivo = actor.getAuthorities().stream().map(GrantedAuthority::getAuthority)
            .anyMatch(r -> r.equals("ROLE_ADMIN") || r.equals("ROLE_DIRECTOR_ACADEMICO"));
        if (directivo) return true;
        return alerta.getAsignacion().getDocente().getUsuario() != null
            && alerta.getAsignacion().getDocente().getUsuario().getId().equals(actor.getUsuario().getId());
    }

    private AlertaAcademicaRespuestaDto mapear(AlertaAcademica alerta) {
        DocenteCursoSeccion asignacion = alerta.getAsignacion();
        AlertaAcademicaRespuestaDto dto = new AlertaAcademicaRespuestaDto();
        dto.setId(alerta.getId());
        dto.setTipo(alerta.getTipo());
        dto.setTitulo(ASISTENCIA.equals(alerta.getTipo()) ? "Asistencia de clase pendiente" : "Notas de evaluación pendientes");
        dto.setCurso(asignacion.getCurso().getNombre());
        dto.setSeccion(asignacion.getSeccion().getNombre());
        dto.setGrado(asignacion.getSeccion().getGrado().getNombre());
        Docente docente = asignacion.getDocente();
        dto.setDocente(docente.getNombres() + " " + docente.getApellidos());
        dto.setFechaReferencia(alerta.getFechaReferencia());
        dto.setFechaLimite(alerta.getFechaLimite());
        dto.setCantidadPendiente(alerta.getCantidadPendiente());
        dto.setEstado(alerta.getEstadoAlerta());
        dto.setAsignacionId(asignacion.getId());
        dto.setHorarioId(alerta.getHorario() != null ? alerta.getHorario().getId() : null);
        dto.setEvaluacionId(alerta.getEvaluacion() != null ? alerta.getEvaluacion().getId() : null);
        dto.setPeriodoEvaluacionId(alerta.getEvaluacion() != null ? alerta.getEvaluacion().getPeriodoEvaluacion().getId() : null);
        return dto;
    }

    private DiaSemana diaSemana(DayOfWeek dia) {
        return switch (dia) {
            case MONDAY -> DiaSemana.LUNES;
            case TUESDAY -> DiaSemana.MARTES;
            case WEDNESDAY -> DiaSemana.MIERCOLES;
            case THURSDAY -> DiaSemana.JUEVES;
            case FRIDAY -> DiaSemana.VIERNES;
            case SATURDAY -> DiaSemana.SABADO;
            case SUNDAY -> DiaSemana.DOMINGO;
        };
    }

    private LocalDate max(LocalDate a, LocalDate b) { return a.isAfter(b) ? a : b; }
    private LocalDate min(LocalDate a, LocalDate b) { return a.isBefore(b) ? a : b; }
}
