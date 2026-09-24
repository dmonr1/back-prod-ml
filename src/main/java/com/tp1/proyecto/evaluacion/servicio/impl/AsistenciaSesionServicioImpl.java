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
import com.tp1.proyecto.comun.enumeracion.EstadoRegistro;
import com.tp1.proyecto.docente.entidad.Docente;
import com.tp1.proyecto.docente.repositorio.DocenteRepositorio;
import com.tp1.proyecto.evaluacion.dto.AsistenciaSesionItemSolicitudDto;
import com.tp1.proyecto.evaluacion.dto.AsistenciaSesionRespuestaDto;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.context.ApplicationEventPublisher;
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

    public AsistenciaSesionServicioImpl(
        AsistenciaSesionRepositorio asistenciaSesionRepositorio,
        DocenteCursoSeccionRepositorio asignacionRepositorio,
        PeriodoEvaluacionRepositorio periodoEvaluacionRepositorio,
        MatriculaRepositorio matriculaRepositorio,
        DocenteRepositorio docenteRepositorio,
        HorarioSemanalRepositorio horarioSemanalRepositorio,
        ApplicationEventPublisher eventPublisher
    ) {
        this.asistenciaSesionRepositorio = asistenciaSesionRepositorio;
        this.asignacionRepositorio = asignacionRepositorio;
        this.periodoEvaluacionRepositorio = periodoEvaluacionRepositorio;
        this.matriculaRepositorio = matriculaRepositorio;
        this.docenteRepositorio = docenteRepositorio;
        this.horarioSemanalRepositorio = horarioSemanalRepositorio;
        this.eventPublisher = eventPublisher;
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
            validarHorario(asignacion, horarioSemanalId, fecha);
            return asistenciaSesionRepositorio.findByAsignacionIdAndPeriodoEvaluacionIdAndFechaClaseAndEstado(
                asignacionId, periodoEvaluacionId, fecha, EstadoRegistro.ACTIVO
            ).stream()
                .filter(item -> item.getHorarioSemanal() == null || item.getHorarioSemanal().getId().equals(horarioSemanalId))
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

    private HorarioSemanal validarHorario(DocenteCursoSeccion asignacion, Long horarioId, LocalDate fecha) {
        HorarioSemanal horario = horarioSemanalRepositorio.findByIdAndEstado(horarioId, EstadoRegistro.ACTIVO)
            .orElseThrow(() -> new RecursoNoEncontradoException("La sesión seleccionada ya no está programada."));
        if (!horario.getAsignacion().getId().equals(asignacion.getId())) {
            throw new ReglaNegocioException("La sesión seleccionada no corresponde a esta asignación.");
        }
        if (fecha == null || horario.getDiaSemana() != diaSemana(fecha.getDayOfWeek())) {
            throw new ReglaNegocioException("La fecha seleccionada no coincide con el día programado de la clase.");
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
        if (!docente.getId().equals(asignacion.getDocente().getId())) {
            throw new ReglaNegocioException("Solo puedes registrar asistencia en tus asignaciones.");
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
