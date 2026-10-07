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
import com.tp1.proyecto.evaluacion.entidad.AsistenciaSesion;
import com.tp1.proyecto.evaluacion.entidad.Evaluacion;
import com.tp1.proyecto.evaluacion.evento.AsistenciaSesionRegistradaEvent;
import com.tp1.proyecto.evaluacion.repositorio.AsistenciaSesionRepositorio;
import com.tp1.proyecto.evaluacion.repositorio.DetalleNotaEvaluacionRepositorio;
import com.tp1.proyecto.evaluacion.repositorio.EvaluacionRepositorio;
import com.tp1.proyecto.seguridad.servicio.UsuarioAutenticado;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

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
    @Transactional
    public List<AlertaAcademicaRespuestaDto> listar(String estado, UsuarioAutenticado actor) {
        sincronizarAlertasPendientes();
        List<AlertaAcademica> alertas = alertaRepositorio.findAllByOrderByFechaLimiteDesc().stream()
            .filter(a -> estado == null || estado.isBlank() || "TODAS".equalsIgnoreCase(estado)
                || a.getEstadoAlerta().equalsIgnoreCase(estado))
            .filter(a -> a.getEstado() == EstadoRegistro.ACTIVO)
            .filter(a -> puedeVer(a, actor))
            .toList();
        return alertas.stream().map(this::mapear).toList();
    }

    @Override
    @Transactional
    public long contarPendientes(UsuarioAutenticado actor) {
        sincronizarAlertasPendientes();
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
        Map<Long, List<PeriodoEvaluacion>> periodosPorAnio = new HashMap<>();
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
                List<PeriodoEvaluacion> periodos = periodosPorAnio.computeIfAbsent(
                    periodoId, periodoEvaluacionRepositorio::findByPeriodoAcademicoId
                );
                PeriodoEvaluacion periodo = periodos.stream()
                    .filter(p -> p.getEstado() == EstadoRegistro.ACTIVO)
                    .filter(p -> !fechaClase.isBefore(p.getFechaInicio()) && !fechaClase.isAfter(p.getFechaFin()))
                    .findFirst().orElse(null);
                if (periodo != null) revisarAsistencia(horario, periodo, fechaClase, limite, horariosActivos);
            }
        }

        revisarNotas(hoy);
        resolverAlertasCompletadas();
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void alRegistrarAsistencia(AsistenciaSesionRegistradaEvent event) {
        if (event.asignacionId() == null || event.horarioSemanalId() == null || event.fechaClase() == null) return;

        HorarioSemanal horario = horarioRepositorio.findByIdAndEstado(
            event.horarioSemanalId(), EstadoRegistro.ACTIVO
        ).orElse(null);
        if (horario == null || !horario.getAsignacion().getId().equals(event.asignacionId())) return;

        PeriodoEvaluacion periodo = periodoEvaluacionRepositorio.findById(event.periodoEvaluacionId())
            .filter(item -> item.getEstado() == EstadoRegistro.ACTIVO)
            .filter(item -> !event.fechaClase().isBefore(item.getFechaInicio())
                && !event.fechaClase().isAfter(item.getFechaFin()))
            .orElse(null);
        if (periodo == null) return;

        List<HorarioSemanal> horariosActivos = horarioRepositorio.findByAsignacionPeriodoAcademicoIdAndEstado(
            horario.getAsignacion().getPeriodoAcademico().getId(), EstadoRegistro.ACTIVO
        ).stream().filter(item -> item.getAsignacion().getId().equals(event.asignacionId())).toList();
        if (!esInicioSesion(horario, horariosActivos)) return;

        LocalDateTime limite = LocalDateTime.of(event.fechaClase(), finSesion(horario, horariosActivos));
        revisarAsistencia(horario, periodo, event.fechaClase(), limite, horariosActivos);
    }

    private void revisarAsistencia(
        HorarioSemanal horario, PeriodoEvaluacion periodo, LocalDate fecha,
        LocalDateTime limite, List<HorarioSemanal> horariosActivos
    ) {
        DocenteCursoSeccion asignacion = horario.getAsignacion();
        List<Matricula> matriculas = matriculaRepositorio.findBySeccionIdAndPeriodoAcademicoIdAndEstado(
            asignacion.getSeccion().getId(), asignacion.getPeriodoAcademico().getId(), EstadoRegistro.ACTIVO
        );
        if (matriculas.isEmpty()) return;
        Set<Long> bloquesSesion = bloquesContinuos(horario, horariosActivos).stream()
            .map(HorarioSemanal::getId).collect(java.util.stream.Collectors.toSet());
        Set<Long> registradas = new HashSet<>(asistenciaRepositorio
            .findByAsignacionIdAndPeriodoEvaluacionIdAndFechaClaseAndEstado(
                asignacion.getId(), periodo.getId(), fecha, EstadoRegistro.ACTIVO
            ).stream()
            .filter(a -> a.getHorarioSemanal() == null || bloquesSesion.contains(a.getHorarioSemanal().getId()))
            .map(a -> a.getMatricula().getId()).toList());
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

    private List<HorarioSemanal> bloquesContinuos(HorarioSemanal inicio, List<HorarioSemanal> horarios) {
        List<HorarioSemanal> bloques = new java.util.ArrayList<>();
        bloques.add(inicio);
        HorarioSemanal actual = inicio;
        while (true) {
            LocalTime finActual = actual.getBloque().getHoraFin();
            HorarioSemanal siguiente = horarios.stream()
                .filter(otro -> otro.getAsignacion().getId().equals(inicio.getAsignacion().getId()))
                .filter(otro -> otro.getDiaSemana() == inicio.getDiaSemana())
                .filter(otro -> otro.getBloque().getHoraInicio().equals(finActual))
                .findFirst().orElse(null);
            if (siguiente == null) return bloques;
            bloques.add(siguiente);
            actual = siguiente;
        }
    }

    private void revisarNotas(LocalDate hoy) {
        List<Evaluacion> evaluaciones = evaluacionRepositorio
            .findByDocenteCursoSeccionEstadoAndEstadoAndFechaEvaluacionIsNotNull(
                EstadoRegistro.ACTIVO, EstadoRegistro.ACTIVO
            ).stream()
            .filter(evaluacion -> evaluacion.getDocenteCursoSeccion().getPeriodoAcademico().getEstado()
                == EstadoRegistro.ACTIVO)
            .filter(evaluacion -> !evaluacion.getFechaEvaluacion()
                .isBefore(evaluacion.getDocenteCursoSeccion().getPeriodoAcademico().getFechaInicio()))
            .filter(evaluacion -> !evaluacion.getFechaEvaluacion()
                .isAfter(evaluacion.getDocenteCursoSeccion().getPeriodoAcademico().getFechaFin()))
            .filter(evaluacion -> evaluacion.getFechaEvaluacion().isBefore(hoy))
            .toList();
        if (evaluaciones.isEmpty()) return;

        Map<Long, Map<Long, Set<Long>>> matriculasPorPeriodoYSeccion = new HashMap<>();
        for (Long periodoAcademicoId : evaluaciones.stream()
            .map(e -> e.getDocenteCursoSeccion().getPeriodoAcademico().getId()).distinct().toList()) {
            Map<Long, Set<Long>> porSeccion = new HashMap<>();
            for (Matricula matricula : matriculaRepositorio.findByPeriodoAcademicoIdAndEstado(
                periodoAcademicoId, EstadoRegistro.ACTIVO
            )) {
                porSeccion.computeIfAbsent(matricula.getSeccion().getId(), ignored -> new HashSet<>())
                    .add(matricula.getId());
            }
            matriculasPorPeriodoYSeccion.put(periodoAcademicoId, porSeccion);
        }

        List<Long> evaluacionIds = evaluaciones.stream().map(Evaluacion::getId).toList();
        Map<Long, Set<Long>> matriculasConNotaPorEvaluacion = new HashMap<>();
        for (var detalle : detalleRepositorio.findByEvaluacionIdInAndEstado(evaluacionIds, EstadoRegistro.ACTIVO)) {
            matriculasConNotaPorEvaluacion.computeIfAbsent(detalle.getEvaluacion().getId(), ignored -> new HashSet<>())
                .add(detalle.getMatricula().getId());
        }

        List<String> claves = evaluacionIds.stream().map(id -> "NOTAS:" + id).toList();
        Map<String, AlertaAcademica> alertasPorClave = alertaRepositorio.findByClaveOrigenIn(claves).stream()
            .collect(Collectors.toMap(AlertaAcademica::getClaveOrigen, Function.identity()));
        List<AlertaAcademica> cambios = new ArrayList<>();
        for (Evaluacion evaluacion : evaluaciones) {
            DocenteCursoSeccion asignacion = evaluacion.getDocenteCursoSeccion();
            Long periodoId = asignacion.getPeriodoAcademico().getId();
            Set<Long> matriculas = matriculasPorPeriodoYSeccion
                .getOrDefault(periodoId, Map.of())
                .getOrDefault(asignacion.getSeccion().getId(), Set.of());
            if (matriculas.isEmpty()) continue;

            Set<Long> registradas = new HashSet<>(matriculasConNotaPorEvaluacion
                .getOrDefault(evaluacion.getId(), Set.of()));
            registradas.retainAll(matriculas);
            int cantidadPendiente = matriculas.size() - registradas.size();
            String clave = "NOTAS:" + evaluacion.getId();
            AlertaAcademica alerta = alertasPorClave.get(clave);
            if (cantidadPendiente == 0 && alerta == null) continue;
            if (alerta == null) alerta = new AlertaAcademica();
            alerta.setClaveOrigen(clave);
            alerta.setTipo(NOTAS);
            alerta.setAsignacion(asignacion);
            alerta.setHorario(null);
            alerta.setEvaluacion(evaluacion);
            alerta.setFechaReferencia(evaluacion.getFechaEvaluacion());
            alerta.setFechaLimite(evaluacion.getFechaEvaluacion().plusDays(1).atStartOfDay());
            alerta.setCantidadPendiente(cantidadPendiente);
            alerta.setEstadoAlerta(cantidadPendiente == 0 ? ATENDIDA : PENDIENTE);
            alerta.setEstado(EstadoRegistro.ACTIVO);
            cambios.add(alerta);
        }
        if (!cambios.isEmpty()) alertaRepositorio.saveAll(cambios);
    }

    private void guardarAlerta(
        String clave, String tipo, DocenteCursoSeccion asignacion, HorarioSemanal horario,
        Evaluacion evaluacion, LocalDate fecha, LocalDateTime limite, int pendientes
    ) {
        AlertaAcademica existente = alertaRepositorio.findByClaveOrigen(clave).orElse(null);
        if (pendientes == 0 && existente == null) return;
        AlertaAcademica alerta = existente != null ? existente : new AlertaAcademica();
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

    /** Recalcula las alertas persistidas antes de mostrarlas para no conservar pendientes obsoletas. */
    private void sincronizarAlertasPendientes() {
        List<AlertaAcademica> pendientes = alertaRepositorio.findByEstadoAlertaOrderByFechaLimiteDesc(PENDIENTE)
            .stream().filter(alerta -> alerta.getEstado() == EstadoRegistro.ACTIVO)
            .toList();
        if (pendientes.isEmpty()) return;

        boolean hayNotasPendientes = pendientes.stream().anyMatch(alerta -> NOTAS.equals(alerta.getTipo()));
        if (hayNotasPendientes) revisarNotas(LocalDate.now(ZONA_LIMA));

        List<AlertaAcademica> asistenciasPendientes = pendientes.stream()
            .filter(alerta -> ASISTENCIA.equals(alerta.getTipo())).toList();
        List<HorarioSemanal> horariosActivos = asistenciasPendientes.isEmpty()
            ? List.of() : horarioRepositorio.findByEstado(EstadoRegistro.ACTIVO);
        Map<Long, List<PeriodoEvaluacion>> periodosPorAnio = new HashMap<>();
        Map<Long, Set<Long>> matriculasPorAsignacion = new HashMap<>();
        Map<Long, List<AsistenciaSesion>> asistenciasPorAsignacion = new HashMap<>();
        Map<Long, LocalDate[]> rangosPorAsignacion = new HashMap<>();
        for (AlertaAcademica alerta : asistenciasPendientes) {
            Long asignacionId = alerta.getAsignacion().getId();
            LocalDate[] rango = rangosPorAsignacion.computeIfAbsent(asignacionId,
                ignored -> new LocalDate[] {alerta.getFechaReferencia(), alerta.getFechaReferencia()});
            if (alerta.getFechaReferencia().isBefore(rango[0])) rango[0] = alerta.getFechaReferencia();
            if (alerta.getFechaReferencia().isAfter(rango[1])) rango[1] = alerta.getFechaReferencia();
        }
        List<AlertaAcademica> actualizadas = new java.util.ArrayList<>();
        for (AlertaAcademica alerta : asistenciasPendientes) {
            if (alerta.getAsignacion().getEstado() != EstadoRegistro.ACTIVO
                || alerta.getAsignacion().getPeriodoAcademico().getEstado() != EstadoRegistro.ACTIVO
                || alerta.getHorario() == null
                || alerta.getHorario().getEstado() != EstadoRegistro.ACTIVO
                || !esInicioSesion(alerta.getHorario(), horariosActivos)) {
                alerta.setEstado(EstadoRegistro.INACTIVO);
                actualizadas.add(alerta);
                continue;
            }
            Long periodoAcademicoId = alerta.getAsignacion().getPeriodoAcademico().getId();
            List<PeriodoEvaluacion> periodos = periodosPorAnio.computeIfAbsent(periodoAcademicoId,
                periodoEvaluacionRepositorio::findByPeriodoAcademicoId);
            PeriodoEvaluacion periodo = periodos.stream().filter(p -> p.getEstado() == EstadoRegistro.ACTIVO)
                .filter(p -> !alerta.getFechaReferencia().isBefore(p.getFechaInicio())
                    && !alerta.getFechaReferencia().isAfter(p.getFechaFin()))
                .findFirst().orElse(null);
            if (periodo == null) continue;

            Long asignacionId = alerta.getAsignacion().getId();
            Set<Long> idsMatricula = matriculasPorAsignacion.computeIfAbsent(asignacionId, ignored ->
                new HashSet<>(matriculaRepositorio.findBySeccionIdAndPeriodoAcademicoIdAndEstado(
                    alerta.getAsignacion().getSeccion().getId(), periodoAcademicoId, EstadoRegistro.ACTIVO
                ).stream().map(Matricula::getId).toList()));
            if (idsMatricula.isEmpty()) continue;

            LocalDate[] rango = rangosPorAsignacion.get(asignacionId);
            List<AsistenciaSesion> registros = asistenciasPorAsignacion.computeIfAbsent(asignacionId, ignored ->
                asistenciaRepositorio.findByAsignacionIdAndFechaClaseBetweenAndEstado(
                    asignacionId, rango[0], rango[1], EstadoRegistro.ACTIVO
                ));
            Set<Long> idsBloques = bloquesContinuos(alerta.getHorario(), horariosActivos).stream()
                .map(HorarioSemanal::getId).collect(java.util.stream.Collectors.toSet());
            Set<Long> registradas = new HashSet<>();
            for (AsistenciaSesion registro : registros) {
                if (registro.getFechaClase().equals(alerta.getFechaReferencia())
                    && registro.getPeriodoEvaluacion() != null
                    && registro.getPeriodoEvaluacion().getId().equals(periodo.getId())
                    && (registro.getHorarioSemanal() == null || idsBloques.contains(registro.getHorarioSemanal().getId()))) {
                    registradas.add(registro.getMatricula().getId());
                }
            }
            int cantidad = (int) idsMatricula.stream().filter(id -> !registradas.contains(id)).count();
            alerta.setCantidadPendiente(cantidad);
            alerta.setEstadoAlerta(cantidad == 0 ? ATENDIDA : PENDIENTE);
            actualizadas.add(alerta);
        }
        if (!actualizadas.isEmpty()) alertaRepositorio.saveAll(actualizadas);

        for (AlertaAcademica alerta : pendientes) {
            if (!NOTAS.equals(alerta.getTipo())) continue;
            Evaluacion evaluacion = alerta.getEvaluacion();
            if (alerta.getAsignacion().getEstado() != EstadoRegistro.ACTIVO
                || alerta.getAsignacion().getPeriodoAcademico().getEstado() != EstadoRegistro.ACTIVO
                || evaluacion == null
                || evaluacion.getEstado() != EstadoRegistro.ACTIVO
                || evaluacion.getFechaEvaluacion() == null
                || !evaluacion.getFechaEvaluacion().isBefore(LocalDate.now(ZONA_LIMA))) {
                alerta.setEstado(EstadoRegistro.INACTIVO);
                alertaRepositorio.save(alerta);
                continue;
            }
        }
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
            } else if (NOTAS.equals(alerta.getTipo()) && alerta.getEvaluacion() != null) {
                if (alerta.getEvaluacion().getEstado() != EstadoRegistro.ACTIVO
                    || alerta.getEvaluacion().getFechaEvaluacion() == null
                    || !alerta.getEvaluacion().getFechaEvaluacion().isBefore(LocalDate.now(ZONA_LIMA))) {
                    alerta.setEstado(EstadoRegistro.INACTIVO);
                    alertaRepositorio.save(alerta);
                    continue;
                }
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
