package com.tp1.proyecto.evaluacion.servicio.impl;

import com.tp1.proyecto.academico.entidad.DocenteCursoSeccion;
import com.tp1.proyecto.academico.entidad.DiaSemana;
import com.tp1.proyecto.academico.entidad.HorarioSemanal;
import com.tp1.proyecto.academico.entidad.Matricula;
import com.tp1.proyecto.academico.entidad.PeriodoEvaluacion;
import com.tp1.proyecto.academico.repositorio.DocenteCursoSeccionRepositorio;
import com.tp1.proyecto.academico.repositorio.HorarioSemanalRepositorio;
import com.tp1.proyecto.academico.repositorio.MatriculaRepositorio;
import com.tp1.proyecto.academico.repositorio.PeriodoEvaluacionRepositorio;
import com.tp1.proyecto.academico.repositorio.TutoriaRepositorio;
import com.tp1.proyecto.comun.enumeracion.EstadoRegistro;
import com.tp1.proyecto.docente.entidad.Docente;
import com.tp1.proyecto.docente.repositorio.DocenteRepositorio;
import com.tp1.proyecto.evaluacion.dto.AsistenciaSesionItemSolicitudDto;
import com.tp1.proyecto.evaluacion.dto.AsistenciaSesionRespuestaDto;
import com.tp1.proyecto.evaluacion.dto.EstadoAsistenciaSesionResumenDto;
import com.tp1.proyecto.evaluacion.dto.RegistroAsistenciaSesionSolicitudDto;
import com.tp1.proyecto.evaluacion.entidad.AsistenciaSesion;
import com.tp1.proyecto.evaluacion.evento.AsistenciaSesionRegistradaEvent;
import com.tp1.proyecto.evaluacion.repositorio.AsistenciaSesionRepositorio;
import com.tp1.proyecto.evaluacion.servicio.AsistenciaSesionServicio;
import com.tp1.proyecto.excepcion.RecursoNoEncontradoException;
import com.tp1.proyecto.excepcion.ReglaNegocioException;
import com.tp1.proyecto.seguridad.servicio.UsuarioAutenticado;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AsistenciaSesionServicioImpl implements AsistenciaSesionServicio {

    private final AsistenciaSesionRepositorio asistenciaSesionRepositorio;
    private final DocenteCursoSeccionRepositorio asignacionRepositorio;
    private final PeriodoEvaluacionRepositorio periodoEvaluacionRepositorio;
    private final MatriculaRepositorio matriculaRepositorio;
    private final DocenteRepositorio docenteRepositorio;
    private final HorarioSemanalRepositorio horarioSemanalRepositorio;
    private final ApplicationEventPublisher eventPublisher;
    private final TutoriaRepositorio tutoriaRepositorio;
    private final JdbcTemplate jdbcTemplate;

    public AsistenciaSesionServicioImpl(
        AsistenciaSesionRepositorio asistenciaSesionRepositorio,
        DocenteCursoSeccionRepositorio asignacionRepositorio,
        PeriodoEvaluacionRepositorio periodoEvaluacionRepositorio,
        MatriculaRepositorio matriculaRepositorio,
        DocenteRepositorio docenteRepositorio,
        HorarioSemanalRepositorio horarioSemanalRepositorio,
        ApplicationEventPublisher eventPublisher,
        TutoriaRepositorio tutoriaRepositorio,
        JdbcTemplate jdbcTemplate
    ) {
        this.asistenciaSesionRepositorio = asistenciaSesionRepositorio;
        this.asignacionRepositorio = asignacionRepositorio;
        this.periodoEvaluacionRepositorio = periodoEvaluacionRepositorio;
        this.matriculaRepositorio = matriculaRepositorio;
        this.docenteRepositorio = docenteRepositorio;
        this.horarioSemanalRepositorio = horarioSemanalRepositorio;
        this.eventPublisher = eventPublisher;
        this.tutoriaRepositorio = tutoriaRepositorio;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AsistenciaSesionRespuestaDto> listar(
        Long asignacionId,
        Long periodoEvaluacionId,
        LocalDate fecha,
        Long horarioSemanalId,
        UsuarioAutenticado actor
    ) {
        DocenteCursoSeccion asignacion = obtenerAsignacion(asignacionId);
        validarAcceso(asignacion, actor);
        validarPeriodo(asignacion, periodoEvaluacionId, fecha);
        if (horarioSemanalId != null) {
            HorarioSemanal horario = validarHorario(asignacion, horarioSemanalId, fecha);
            Set<Long> bloquesSesion = bloquesContinuos(horario,
                horarioSemanalRepositorio.findByAsignacionPeriodoAcademicoIdAndEstado(
                    asignacion.getPeriodoAcademico().getId(), EstadoRegistro.ACTIVO
                ).stream().filter(item -> item.getAsignacion().getId().equals(asignacionId)).toList()
            ).stream().map(HorarioSemanal::getId).collect(Collectors.toSet());
            return asistenciaSesionRepositorio.findByAsignacionIdAndPeriodoEvaluacionIdAndFechaClaseAndEstado(
                asignacionId, periodoEvaluacionId, fecha, EstadoRegistro.ACTIVO
            ).stream()
                .filter(item -> item.getHorarioSemanal() == null || bloquesSesion.contains(item.getHorarioSemanal().getId()))
                .map(this::mapear)
                .toList();
        }
        return asistenciaSesionRepositorio.findByAsignacionIdAndPeriodoEvaluacionIdAndFechaClaseAndEstado(
                asignacionId,
                periodoEvaluacionId,
                fecha,
                EstadoRegistro.ACTIVO
            )
            .stream()
            .map(this::mapear)
            .toList();
    }

    @Override
    public List<AsistenciaSesionRespuestaDto> registrar(
        RegistroAsistenciaSesionSolicitudDto solicitud,
        UsuarioAutenticado actor
    ) {
        if (solicitud.getFechaClase() == null || !solicitud.getFechaClase().equals(LocalDate.now(ZoneId.of("America/Lima")))) {
            throw new ReglaNegocioException("La asistencia regular solo puede registrarse el día programado. Para corregir una fecha pasada, usa la opción de edición autorizada.");
        }
        DocenteCursoSeccion asignacion = obtenerAsignacion(solicitud.getDocenteCursoSeccionId());
        validarAcceso(asignacion, actor);
        PeriodoEvaluacion periodo = validarPeriodo(
            asignacion,
            solicitud.getPeriodoEvaluacionId(),
            solicitud.getFechaClase()
        );
        HorarioSemanal horario = solicitud.getHorarioSemanalId() == null
            ? null
            : validarHorario(asignacion, solicitud.getHorarioSemanalId(), solicitud.getFechaClase());

        Long periodoAcademicoId = asignacion.getPeriodoAcademico().getId();
        Long seccionId = asignacion.getSeccion().getId();
        Set<Long> matriculasIncluidas = new HashSet<>();
        List<AsistenciaSesionRespuestaDto> respuestas = new ArrayList<>();

        for (AsistenciaSesionItemSolicitudDto item : solicitud.getAsistencias()) {
            if (!matriculasIncluidas.add(item.getMatriculaId())) {
                throw new ReglaNegocioException("La lista contiene una matrícula duplicada.");
            }

            Matricula matricula = matriculaRepositorio.findById(item.getMatriculaId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                    "Matrícula no encontrada con id: " + item.getMatriculaId()
                ));

            if (!matricula.getSeccion().getId().equals(seccionId)
                || !matricula.getPeriodoAcademico().getId().equals(periodoAcademicoId)
                || matricula.getEstado() != EstadoRegistro.ACTIVO) {
                throw new ReglaNegocioException("La matrícula no pertenece a la sección activa de esta asignación.");
            }

            AsistenciaSesion registro = horario == null
                ? asistenciaSesionRepositorio.findByHorarioSemanalIsNullAndAsignacionIdAndPeriodoEvaluacionIdAndFechaClaseAndMatriculaId(
                    asignacion.getId(), periodo.getId(), solicitud.getFechaClase(), matricula.getId()).orElseGet(AsistenciaSesion::new)
                : asistenciaSesionRepositorio.findByHorarioSemanalIdAndAsignacionIdAndPeriodoEvaluacionIdAndFechaClaseAndMatriculaId(
                    horario.getId(), asignacion.getId(), periodo.getId(), solicitud.getFechaClase(), matricula.getId())
                    .orElseGet(() -> asistenciaSesionRepositorio.findByHorarioSemanalIsNullAndAsignacionIdAndPeriodoEvaluacionIdAndFechaClaseAndMatriculaId(
                        asignacion.getId(), periodo.getId(), solicitud.getFechaClase(), matricula.getId()).orElseGet(AsistenciaSesion::new));

            if (registro.getId() != null) {
                throw new ReglaNegocioException("La asistencia ya fue registrada. Usa la acción de edición para corregirla.");
            }

            registro.setAsignacion(asignacion);
            registro.setHorarioSemanal(horario);
            registro.setPeriodoEvaluacion(periodo);
            registro.setMatricula(matricula);
            registro.setFechaClase(solicitud.getFechaClase());
            registro.setEstadoAsistencia(item.getEstado());
            registro.setObservacion(item.getObservacion());
            registro.setUsuarioRegistro(actor.getUsuario());
            registro.setEstado(EstadoRegistro.ACTIVO);
            respuestas.add(mapear(asistenciaSesionRepositorio.save(registro)));
        }

        asistenciaSesionRepositorio.flush();
        eventPublisher.publishEvent(new AsistenciaSesionRegistradaEvent(periodo.getId(), List.copyOf(matriculasIncluidas)));

        return respuestas;
    }

    @Override
    public List<AsistenciaSesionRespuestaDto> editar(
        RegistroAsistenciaSesionSolicitudDto solicitud,
        UsuarioAutenticado actor
    ) {
        DocenteCursoSeccion asignacion = obtenerAsignacion(solicitud.getDocenteCursoSeccionId());
        validarPermisoEdicionHistorica(asignacion, actor);
        if (solicitud.getMotivoEdicion() == null || solicitud.getMotivoEdicion().isBlank()) {
            throw new ReglaNegocioException("Indica el motivo de la corrección de asistencia.");
        }
        PeriodoEvaluacion periodo = validarPeriodo(asignacion, solicitud.getPeriodoEvaluacionId(), solicitud.getFechaClase());
        HorarioSemanal horario = solicitud.getHorarioSemanalId() == null
            ? null
            : validarHorario(asignacion, solicitud.getHorarioSemanalId(), solicitud.getFechaClase());
        Set<Long> matriculasIncluidas = new HashSet<>();
        List<AsistenciaSesionRespuestaDto> respuestas = new ArrayList<>();

        for (AsistenciaSesionItemSolicitudDto item : solicitud.getAsistencias()) {
            if (!matriculasIncluidas.add(item.getMatriculaId())) {
                throw new ReglaNegocioException("La lista contiene una matrícula duplicada.");
            }
            List<AsistenciaSesion> registrosExistentes = new ArrayList<>();
            if (horario == null) {
                asistenciaSesionRepositorio.findByHorarioSemanalIsNullAndAsignacionIdAndPeriodoEvaluacionIdAndFechaClaseAndMatriculaId(
                    asignacion.getId(), periodo.getId(), solicitud.getFechaClase(), item.getMatriculaId()
                ).ifPresent(registrosExistentes::add);
            } else {
                List<HorarioSemanal> horariosAsignacion = horarioSemanalRepositorio
                    .findByAsignacionPeriodoAcademicoIdAndEstado(asignacion.getPeriodoAcademico().getId(), EstadoRegistro.ACTIVO)
                    .stream().filter(h -> h.getAsignacion().getId().equals(asignacion.getId())).toList();
                for (HorarioSemanal bloque : bloquesContinuos(horario, horariosAsignacion)) {
                    asistenciaSesionRepositorio.findByHorarioSemanalIdAndAsignacionIdAndPeriodoEvaluacionIdAndFechaClaseAndMatriculaId(
                        bloque.getId(), asignacion.getId(), periodo.getId(), solicitud.getFechaClase(), item.getMatriculaId()
                    ).ifPresent(registrosExistentes::add);
                }
                if (registrosExistentes.isEmpty()) {
                    asistenciaSesionRepositorio.findByHorarioSemanalIsNullAndAsignacionIdAndPeriodoEvaluacionIdAndFechaClaseAndMatriculaId(
                        asignacion.getId(), periodo.getId(), solicitud.getFechaClase(), item.getMatriculaId()
                    ).ifPresent(registrosExistentes::add);
                }
            }
            boolean nuevo = registrosExistentes.isEmpty();
            if (nuevo) {
                Matricula matricula = matriculaRepositorio.findById(item.getMatriculaId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Matrícula no encontrada con id: " + item.getMatriculaId()));
                if (!matricula.getSeccion().getId().equals(asignacion.getSeccion().getId())
                    || !matricula.getPeriodoAcademico().getId().equals(asignacion.getPeriodoAcademico().getId())
                    || matricula.getEstado() != EstadoRegistro.ACTIVO) {
                    throw new ReglaNegocioException("La matrícula no pertenece a la sección activa de esta asignación.");
                }
            }

            if (nuevo) {
                AsistenciaSesion registro = new AsistenciaSesion();
                registro.setAsignacion(asignacion);
                registro.setHorarioSemanal(horario);
                registro.setPeriodoEvaluacion(periodo);
                registro.setMatricula(matriculaRepositorio.findById(item.getMatriculaId()).orElseThrow());
                registro.setFechaClase(solicitud.getFechaClase());
                registro.setEstadoAsistencia(item.getEstado());
                registro.setObservacion(item.getObservacion());
                registro.setUsuarioRegistro(actor.getUsuario());
                registro.setEstado(EstadoRegistro.ACTIVO);
                registrosExistentes.add(asistenciaSesionRepositorio.save(registro));
            }
            for (AsistenciaSesion registro : registrosExistentes) {
                String estadoAnterior = nuevo ? null : registro.getEstadoAsistencia().name();
                String observacionAnterior = registro.getObservacion();
                if (!nuevo && registro.getEstadoAsistencia() == item.getEstado()
                    && java.util.Objects.equals(observacionAnterior, item.getObservacion())) {
                    continue;
                }
                registro.setEstadoAsistencia(item.getEstado());
                registro.setObservacion(item.getObservacion());
                registro.setUsuarioRegistro(actor.getUsuario());
                AsistenciaSesion guardado = asistenciaSesionRepositorio.save(registro);
                jdbcTemplate.update("""
                    INSERT INTO db_tp1.historial_ediciones_asistencia
                    (asistencia_sesion_id, estado_anterior, estado_nuevo, observacion_anterior,
                     observacion_nueva, motivo, usuario_editor_id, fecha_edicion)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                    """,
                    guardado.getId(), estadoAnterior, item.getEstado().name(), observacionAnterior,
                    item.getObservacion(), solicitud.getMotivoEdicion().trim(),
                    actor.getUsuario().getId(), LocalDateTime.now(ZoneId.of("America/Lima"))
                );
                if (respuestas.stream().noneMatch(r -> r.getMatriculaId().equals(item.getMatriculaId()))) {
                    respuestas.add(mapear(guardado));
                }
            }
            if (respuestas.stream().noneMatch(r -> r.getMatriculaId().equals(item.getMatriculaId()))) {
                respuestas.add(mapear(registrosExistentes.get(0)));
            }
        }
        asistenciaSesionRepositorio.flush();
        eventPublisher.publishEvent(new AsistenciaSesionRegistradaEvent(periodo.getId(), List.copyOf(matriculasIncluidas)));
        return respuestas;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EstadoAsistenciaSesionResumenDto> resumir(
        Collection<Long> asignacionIds,
        LocalDate desde,
        LocalDate hasta,
        UsuarioAutenticado actor
    ) {
        if (desde == null || hasta == null || hasta.isBefore(desde)) {
            throw new ReglaNegocioException("El rango de fechas para consultar asistencia no es válido.");
        }
        List<EstadoAsistenciaSesionResumenDto> resultado = new ArrayList<>();
        for (Long asignacionId : asignacionIds.stream().distinct().toList()) {
            DocenteCursoSeccion asignacion = obtenerAsignacion(asignacionId);
            validarAcceso(asignacion, actor);
            List<HorarioSemanal> horarios = horarioSemanalRepositorio
                .findByAsignacionPeriodoAcademicoIdAndEstado(asignacion.getPeriodoAcademico().getId(), EstadoRegistro.ACTIVO)
                .stream()
                .filter(item -> item.getAsignacion().getId().equals(asignacionId))
                .toList();
            List<AsistenciaSesion> registros = asistenciaSesionRepositorio
                .findByAsignacionIdAndFechaClaseBetweenAndEstado(asignacionId, desde, hasta, EstadoRegistro.ACTIVO);
            Map<String, Set<Long>> alumnosPorSesion = new HashMap<>();
            for (HorarioSemanal inicio : horarios) {
                if (tieneBloqueAnterior(inicio, horarios)) continue;
                Set<Long> idsBloques = bloquesContinuos(inicio, horarios).stream()
                    .map(HorarioSemanal::getId).collect(Collectors.toSet());
                for (AsistenciaSesion registro : registros) {
                    if (registro.getHorarioSemanal() != null && idsBloques.contains(registro.getHorarioSemanal().getId())) {
                        String clave = inicio.getId() + ":" + registro.getFechaClase();
                        alumnosPorSesion.computeIfAbsent(clave, ignored -> new HashSet<>())
                            .add(registro.getMatricula().getId());
                    }
                }
            }
            int total = (int) matriculaRepositorio.findBySeccionIdAndPeriodoAcademicoIdAndEstado(
                asignacion.getSeccion().getId(), asignacion.getPeriodoAcademico().getId(), EstadoRegistro.ACTIVO
            ).size();
            for (Map.Entry<String, Set<Long>> entrada : alumnosPorSesion.entrySet()) {
                String[] clave = entrada.getKey().split(":");
                EstadoAsistenciaSesionResumenDto dto = new EstadoAsistenciaSesionResumenDto();
                dto.setAsignacionId(asignacionId);
                dto.setHorarioSemanalId(Long.valueOf(clave[0]));
                dto.setFechaClase(LocalDate.parse(clave[1]));
                dto.setRegistradas(entrada.getValue().size());
                dto.setTotal(total);
                resultado.add(dto);
            }
        }
        return resultado;
    }

    private boolean tieneBloqueAnterior(HorarioSemanal horario, List<HorarioSemanal> horarios) {
        return horarios.stream()
            .filter(otro -> otro.getDiaSemana() == horario.getDiaSemana())
            .anyMatch(otro -> otro.getBloque().getHoraFin().equals(horario.getBloque().getHoraInicio()));
    }

    private List<HorarioSemanal> bloquesContinuos(HorarioSemanal inicio, List<HorarioSemanal> horarios) {
        List<HorarioSemanal> bloques = new ArrayList<>();
        bloques.add(inicio);
        HorarioSemanal actual = inicio;
        while (true) {
            HorarioSemanal actualBloque = actual;
            HorarioSemanal siguiente = horarios.stream()
                .filter(otro -> otro.getDiaSemana() == inicio.getDiaSemana())
                .filter(otro -> otro.getBloque().getHoraInicio().equals(actualBloque.getBloque().getHoraFin()))
                .findFirst().orElse(null);
            if (siguiente == null) break;
            bloques.add(siguiente);
            actual = siguiente;
        }
        return bloques;
    }

    private HorarioSemanal validarHorario(DocenteCursoSeccion asignacion, Long horarioId, LocalDate fecha) {
        HorarioSemanal horario = horarioSemanalRepositorio.findByIdAndEstado(horarioId, EstadoRegistro.ACTIVO)
            .orElseThrow(() -> new RecursoNoEncontradoException("La sesión seleccionada ya no está programada."));
        if (!horario.getAsignacion().getId().equals(asignacion.getId())) {
            throw new ReglaNegocioException("La sesión seleccionada no corresponde a esta asignación.");
        }
        if (fecha == null || horario.getDiaSemana() != diaSemana(fecha.getDayOfWeek())) {
            throw new ReglaNegocioException("La fecha seleccionada no coincide con el día programado de la clase.");
        }
        if (fecha.getDayOfWeek() == DayOfWeek.SATURDAY || fecha.getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new ReglaNegocioException("El registro de asistencia solo está habilitado para clases de lunes a viernes.");
        }
        boolean tieneBloqueAnterior = horarioSemanalRepositorio.findByAsignacionPeriodoAcademicoIdAndEstado(
            asignacion.getPeriodoAcademico().getId(), EstadoRegistro.ACTIVO
        ).stream()
            .filter(anterior -> anterior.getAsignacion().getId().equals(asignacion.getId()))
            .filter(anterior -> anterior.getDiaSemana() == horario.getDiaSemana())
            .anyMatch(anterior -> anterior.getBloque().getHoraFin().equals(horario.getBloque().getHoraInicio()));
        if (tieneBloqueAnterior) {
            throw new ReglaNegocioException("Esta clase forma parte de una sesión continua. Registra la asistencia desde su primer bloque horario.");
        }
        return horario;
    }

    private DiaSemana diaSemana(DayOfWeek dia) {
        switch (dia) {
            case MONDAY: return DiaSemana.LUNES;
            case TUESDAY: return DiaSemana.MARTES;
            case WEDNESDAY: return DiaSemana.MIERCOLES;
            case THURSDAY: return DiaSemana.JUEVES;
            case FRIDAY: return DiaSemana.VIERNES;
            case SATURDAY: return DiaSemana.SABADO;
            case SUNDAY: return DiaSemana.DOMINGO;
            default: throw new IllegalArgumentException("Día de semana no válido.");
        }
    }

    private DocenteCursoSeccion obtenerAsignacion(Long asignacionId) {
        DocenteCursoSeccion asignacion = asignacionRepositorio.findById(asignacionId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Asignación académica no encontrada."));
        if (asignacion.getEstado() != EstadoRegistro.ACTIVO) {
            throw new ReglaNegocioException("La asignación seleccionada no está activa.");
        }
        return asignacion;
    }

    private void validarAcceso(DocenteCursoSeccion asignacion, UsuarioAutenticado actor) {
        boolean esAdministrador = actor.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch("ROLE_ADMIN"::equals);
        if (esAdministrador) {
            return;
        }

        Docente docente = docenteRepositorio.findByUsuarioId(actor.getUsuario().getId())
            .orElseThrow(() -> new ReglaNegocioException("El usuario no está vinculado a un docente."));
        boolean esTutorDeSeccion = actor.getRoles().contains("DOCENTE_TUTOR")
            && tutoriaRepositorio.existsByDocenteIdAndSeccionIdAndPeriodoAcademicoIdAndEstado(
                docente.getId(), asignacion.getSeccion().getId(), asignacion.getPeriodoAcademico().getId(), EstadoRegistro.ACTIVO
            );
        if (!docente.getId().equals(asignacion.getDocente().getId()) && !esTutorDeSeccion) {
            throw new ReglaNegocioException("Solo puedes registrar asistencia en tus asignaciones.");
        }
    }

    private void validarPermisoEdicionHistorica(DocenteCursoSeccion asignacion, UsuarioAutenticado actor) {
        boolean esAdministradorODirector = actor.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch("ROLE_ADMIN"::equals);
        if (esAdministradorODirector) return;
        if (!actor.getRoles().contains("DOCENTE_TUTOR")) {
            throw new ReglaNegocioException("La edición de asistencias pasadas está reservada al tutor de sección, dirección académica o administrador.");
        }
        Docente tutor = docenteRepositorio.findByUsuarioId(actor.getUsuario().getId())
            .orElseThrow(() -> new ReglaNegocioException("El usuario no está vinculado a un docente tutor."));
        if (!tutoriaRepositorio.existsByDocenteIdAndSeccionIdAndPeriodoAcademicoIdAndEstado(
            tutor.getId(), asignacion.getSeccion().getId(), asignacion.getPeriodoAcademico().getId(), EstadoRegistro.ACTIVO
        )) {
            throw new ReglaNegocioException("Solo el tutor asignado a esta sección puede corregir la asistencia histórica.");
        }
    }

    private PeriodoEvaluacion validarPeriodo(
        DocenteCursoSeccion asignacion,
        Long periodoEvaluacionId,
        LocalDate fecha
    ) {
        PeriodoEvaluacion periodo = periodoEvaluacionRepositorio.findById(periodoEvaluacionId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Período de evaluación no encontrado."));
        if (!periodo.getPeriodoAcademico().getId().equals(asignacion.getPeriodoAcademico().getId())) {
            throw new ReglaNegocioException("El período de evaluación no corresponde a la asignación.");
        }
        if (fecha == null || fecha.isBefore(periodo.getFechaInicio()) || fecha.isAfter(periodo.getFechaFin())) {
            throw new ReglaNegocioException("La fecha de clase debe estar dentro del período de evaluación.");
        }
        return periodo;
    }

    private AsistenciaSesionRespuestaDto mapear(AsistenciaSesion entidad) {
        AsistenciaSesionRespuestaDto dto = new AsistenciaSesionRespuestaDto();
        dto.setId(entidad.getId());
        dto.setMatriculaId(entidad.getMatricula().getId());
        dto.setPeriodoEvaluacionId(entidad.getPeriodoEvaluacion().getId());
        dto.setHorarioSemanalId(entidad.getHorarioSemanal() != null ? entidad.getHorarioSemanal().getId() : null);
        dto.setFechaClase(entidad.getFechaClase());
        dto.setEstado(entidad.getEstadoAsistencia());
        dto.setObservacion(entidad.getObservacion());
        return dto;
    }
}
