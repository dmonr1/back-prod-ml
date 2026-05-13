package com.tp1.proyecto.evaluacion.servicio.impl;

import com.tp1.proyecto.academico.entidad.DocenteCursoSeccion;
import com.tp1.proyecto.academico.entidad.Matricula;
import com.tp1.proyecto.academico.repositorio.PeriodoEvaluacionRepositorio;
import com.tp1.proyecto.academico.repositorio.DocenteCursoSeccionRepositorio;
import com.tp1.proyecto.academico.repositorio.MatriculaRepositorio;
import com.tp1.proyecto.comun.enumeracion.EstadoRegistro;
import com.tp1.proyecto.evaluacion.dto.DetalleNotaEvaluacionRespuestaDto;
import com.tp1.proyecto.evaluacion.dto.DetalleNotaEvaluacionSolicitudDto;
import com.tp1.proyecto.evaluacion.dto.EvaluacionRespuestaDto;
import com.tp1.proyecto.evaluacion.dto.EvaluacionSolicitudDto;
import com.tp1.proyecto.evaluacion.dto.RegistroNotasEvaluacionSolicitudDto;
import com.tp1.proyecto.evaluacion.entidad.ConfiguracionEvaluacion;
import com.tp1.proyecto.evaluacion.entidad.DetalleNotaEvaluacion;
import com.tp1.proyecto.evaluacion.entidad.Evaluacion;
import com.tp1.proyecto.evaluacion.entidad.NotaCursoPeriodoEvaluacion;
import com.tp1.proyecto.evaluacion.entidad.TipoEvaluacion;
import com.tp1.proyecto.evaluacion.repositorio.ConfiguracionEvaluacionRepositorio;
import com.tp1.proyecto.evaluacion.repositorio.DetalleNotaEvaluacionRepositorio;
import com.tp1.proyecto.evaluacion.repositorio.EvaluacionRepositorio;
import com.tp1.proyecto.evaluacion.repositorio.NotaCursoPeriodoEvaluacionRepositorio;
import com.tp1.proyecto.evaluacion.repositorio.TipoEvaluacionRepositorio;
import com.tp1.proyecto.evaluacion.servicio.EvaluacionServicio;
import com.tp1.proyecto.excepcion.RecursoNoEncontradoException;
import com.tp1.proyecto.excepcion.ReglaNegocioException;
import com.tp1.proyecto.prediccion.servicio.PrediccionRiesgoServicio;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EvaluacionServicioImpl implements EvaluacionServicio {

    private final EvaluacionRepositorio evaluacionRepositorio;
    private final ConfiguracionEvaluacionRepositorio configuracionEvaluacionRepositorio;
    private final DocenteCursoSeccionRepositorio docenteCursoSeccionRepositorio;
    private final PeriodoEvaluacionRepositorio periodoEvaluacionRepositorio;
    private final TipoEvaluacionRepositorio tipoEvaluacionRepositorio;
    private final DetalleNotaEvaluacionRepositorio detalleNotaEvaluacionRepositorio;
    private final MatriculaRepositorio matriculaRepositorio;
    private final NotaCursoPeriodoEvaluacionRepositorio notaCursoPeriodoEvaluacionRepositorio;
    private final PrediccionRiesgoServicio prediccionRiesgoServicio;

    public EvaluacionServicioImpl(
        EvaluacionRepositorio evaluacionRepositorio,
        ConfiguracionEvaluacionRepositorio configuracionEvaluacionRepositorio,
        DocenteCursoSeccionRepositorio docenteCursoSeccionRepositorio,
        PeriodoEvaluacionRepositorio periodoEvaluacionRepositorio,
        TipoEvaluacionRepositorio tipoEvaluacionRepositorio,
        DetalleNotaEvaluacionRepositorio detalleNotaEvaluacionRepositorio,
        MatriculaRepositorio matriculaRepositorio,
        NotaCursoPeriodoEvaluacionRepositorio notaCursoPeriodoEvaluacionRepositorio,
        PrediccionRiesgoServicio prediccionRiesgoServicio
    ) {
        this.evaluacionRepositorio = evaluacionRepositorio;
        this.configuracionEvaluacionRepositorio = configuracionEvaluacionRepositorio;
        this.docenteCursoSeccionRepositorio = docenteCursoSeccionRepositorio;
        this.periodoEvaluacionRepositorio = periodoEvaluacionRepositorio;
        this.tipoEvaluacionRepositorio = tipoEvaluacionRepositorio;
        this.detalleNotaEvaluacionRepositorio = detalleNotaEvaluacionRepositorio;
        this.matriculaRepositorio = matriculaRepositorio;
        this.notaCursoPeriodoEvaluacionRepositorio = notaCursoPeriodoEvaluacionRepositorio;
        this.prediccionRiesgoServicio = prediccionRiesgoServicio;
    }

    @Override
    public EvaluacionRespuestaDto crear(EvaluacionSolicitudDto solicitud) {
        ConfiguracionEvaluacion configuracion = configuracionEvaluacionRepositorio.findById(solicitud.getConfiguracionEvaluacionId())
            .orElseThrow(() -> new RecursoNoEncontradoException("Configuracion de evaluacion no encontrada con id: " + solicitud.getConfiguracionEvaluacionId()));

        DocenteCursoSeccion docenteCursoSeccion = docenteCursoSeccionRepositorio.findById(solicitud.getDocenteCursoSeccionId())
            .orElseThrow(() -> new RecursoNoEncontradoException("Asignacion docente-curso-seccion no encontrada con id: " + solicitud.getDocenteCursoSeccionId()));

        var periodoEvaluacion = periodoEvaluacionRepositorio.findById(solicitud.getPeriodoEvaluacionId())
            .orElseThrow(() -> new RecursoNoEncontradoException("PeriodoEvaluacion no encontrado con id: " + solicitud.getPeriodoEvaluacionId()));

        TipoEvaluacion tipoEvaluacion = tipoEvaluacionRepositorio.findById(solicitud.getTipoEvaluacionId())
            .orElseThrow(() -> new RecursoNoEncontradoException("Tipo de evaluacion no encontrado con id: " + solicitud.getTipoEvaluacionId()));

        validarCoherenciaConfiguracion(configuracion, docenteCursoSeccion, periodoEvaluacion.getId(), tipoEvaluacion.getId());

        Evaluacion evaluacion = new Evaluacion();
        evaluacion.setConfiguracionEvaluacion(configuracion);
        evaluacion.setDocenteCursoSeccion(docenteCursoSeccion);
        evaluacion.setPeriodoEvaluacion(periodoEvaluacion);
        evaluacion.setTipoEvaluacion(tipoEvaluacion);
        evaluacion.setNumeroEvaluacion(solicitud.getNumeroEvaluacion());
        evaluacion.setNombre(solicitud.getNombre().trim());
        evaluacion.setFechaEvaluacion(solicitud.getFechaEvaluacion());

        return mapearEvaluacion(evaluacionRepositorio.save(evaluacion));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EvaluacionRespuestaDto> listarPorAsignacionYPeriodoEvaluacion(Long docenteCursoSeccionId, Long periodoEvaluacionId) {
        return evaluacionRepositorio
            .findByDocenteCursoSeccionIdAndPeriodoEvaluacionIdOrderByTipoEvaluacionOrdenAscNumeroEvaluacionAsc(docenteCursoSeccionId, periodoEvaluacionId)
            .stream()
            .map(this::mapearEvaluacion)
            .toList();
    }

    @Override
    public List<DetalleNotaEvaluacionRespuestaDto> registrarNotas(Long evaluacionId, RegistroNotasEvaluacionSolicitudDto solicitud) {
        Evaluacion evaluacion = evaluacionRepositorio.findById(evaluacionId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Evaluacion no encontrada con id: " + evaluacionId));

        Map<Long, Matricula> matriculasPorId = new LinkedHashMap<>();
        for (Matricula matricula : matriculaRepositorio.findBySeccionIdAndPeriodoAcademicoId(
            evaluacion.getDocenteCursoSeccion().getSeccion().getId(),
            evaluacion.getDocenteCursoSeccion().getPeriodoAcademico().getId()
        )) {
            matriculasPorId.put(matricula.getId(), matricula);
        }

        List<DetalleNotaEvaluacionRespuestaDto> respuestas = new ArrayList<>();
        for (DetalleNotaEvaluacionSolicitudDto item : solicitud.getNotas()) {
            Matricula matricula = matriculasPorId.get(item.getMatriculaId());
            if (matricula == null) {
                throw new ReglaNegocioException("La matricula " + item.getMatriculaId() + " no pertenece a la seccion de la evaluacion");
            }

            DetalleNotaEvaluacion detalle = detalleNotaEvaluacionRepositorio
                .findByEvaluacionIdAndMatriculaId(evaluacionId, item.getMatriculaId())
                .orElseGet(DetalleNotaEvaluacion::new);

            detalle.setEvaluacion(evaluacion);
            detalle.setMatricula(matricula);
            detalle.setNota(item.getNota());
            detalle.setObservacion(item.getObservacion());
            detalle.setEstado(EstadoRegistro.ACTIVO);

            DetalleNotaEvaluacion guardado = detalleNotaEvaluacionRepositorio.save(detalle);
            recalcularNotaCursoPeriodoEvaluacion(evaluacion, matricula);
            prediccionRiesgoServicio.generarPrediccionGlobalPorMatricula(matricula.getId(), evaluacion.getPeriodoEvaluacion().getId());
            respuestas.add(mapearDetalle(guardado));
        }

        return respuestas;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DetalleNotaEvaluacionRespuestaDto> listarNotasPorEvaluacion(Long evaluacionId) {
        evaluacionRepositorio.findById(evaluacionId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Evaluacion no encontrada con id: " + evaluacionId));

        return detalleNotaEvaluacionRepositorio.findByEvaluacionId(evaluacionId)
            .stream()
            .map(this::mapearDetalle)
            .toList();
    }

    private void validarCoherenciaConfiguracion(
        ConfiguracionEvaluacion configuracion,
        DocenteCursoSeccion docenteCursoSeccion,
        Long periodoEvaluacionId,
        Long tipoEvaluacionId
    ) {
        if (!configuracion.getCurso().getId().equals(docenteCursoSeccion.getCurso().getId())) {
            throw new ReglaNegocioException("La configuracion no corresponde al curso asignado");
        }
        if (!configuracion.getPeriodoEvaluacion().getId().equals(periodoEvaluacionId)) {
            throw new ReglaNegocioException("La configuracion no corresponde al periodoEvaluacion seleccionado");
        }
        if (!configuracion.getTipoEvaluacion().getId().equals(tipoEvaluacionId)) {
            throw new ReglaNegocioException("El tipo de evaluacion no coincide con la configuracion");
        }
    }

    private void recalcularNotaCursoPeriodoEvaluacion(Evaluacion evaluacion, Matricula matricula) {
        List<Evaluacion> evaluacionesRelacionadas = evaluacionRepositorio
            .findByDocenteCursoSeccionIdAndPeriodoEvaluacionIdOrderByTipoEvaluacionOrdenAscNumeroEvaluacionAsc(
                evaluacion.getDocenteCursoSeccion().getId(),
                evaluacion.getPeriodoEvaluacion().getId()
            );

        List<BigDecimal> notasConsideradas = new ArrayList<>();
        for (Evaluacion item : evaluacionesRelacionadas) {
            if (!Boolean.TRUE.equals(item.getConfiguracionEvaluacion().getCalcularEnPromedio())) {
                continue;
            }

            detalleNotaEvaluacionRepositorio.findByEvaluacionIdAndMatriculaId(item.getId(), matricula.getId())
                .ifPresent(detalle -> notasConsideradas.add(detalle.getNota()));
        }

        if (notasConsideradas.isEmpty()) {
            return;
        }

        BigDecimal suma = notasConsideradas.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal promedio = suma.divide(BigDecimal.valueOf(notasConsideradas.size()), 2, RoundingMode.HALF_UP);

        NotaCursoPeriodoEvaluacion consolidado = notaCursoPeriodoEvaluacionRepositorio
            .findByMatriculaIdAndCursoIdAndPeriodoEvaluacionId(
                matricula.getId(),
                evaluacion.getDocenteCursoSeccion().getCurso().getId(),
                evaluacion.getPeriodoEvaluacion().getId()
            )
            .orElseGet(NotaCursoPeriodoEvaluacion::new);

        consolidado.setMatricula(matricula);
        consolidado.setCurso(evaluacion.getDocenteCursoSeccion().getCurso());
        consolidado.setPeriodoEvaluacion(evaluacion.getPeriodoEvaluacion());
        consolidado.setPromedioCurso(promedio);
        consolidado.setCantidadEvaluacionesRegistradas(notasConsideradas.size());
        consolidado.setObservacion("Promedio recalculado desde evaluaciones parciales");
        consolidado.setEstado(EstadoRegistro.ACTIVO);

        notaCursoPeriodoEvaluacionRepositorio.save(consolidado);
    }

    private EvaluacionRespuestaDto mapearEvaluacion(Evaluacion entidad) {
        EvaluacionRespuestaDto dto = new EvaluacionRespuestaDto();
        dto.setId(entidad.getId());
        dto.setConfiguracionEvaluacionId(entidad.getConfiguracionEvaluacion().getId());
        dto.setDocenteCursoSeccionId(entidad.getDocenteCursoSeccion().getId());
        dto.setPeriodoEvaluacionId(entidad.getPeriodoEvaluacion().getId());
        dto.setNombrePeriodoEvaluacion(entidad.getPeriodoEvaluacion().getNombre());
        dto.setTipoEvaluacionId(entidad.getTipoEvaluacion().getId());
        dto.setTipoEvaluacion(entidad.getTipoEvaluacion().getNombre());
        dto.setNumeroEvaluacion(entidad.getNumeroEvaluacion());
        dto.setNombre(entidad.getNombre());
        dto.setFechaEvaluacion(entidad.getFechaEvaluacion());
        dto.setCurso(entidad.getDocenteCursoSeccion().getCurso().getNombre());
        dto.setSeccion(entidad.getDocenteCursoSeccion().getSeccion().getNombre());
        dto.setGrado(entidad.getDocenteCursoSeccion().getSeccion().getGrado().getNombre());
        dto.setNivel(entidad.getDocenteCursoSeccion().getSeccion().getGrado().getNivel().getNombre());
        dto.setEstado(entidad.getEstado().name());
        return dto;
    }

    private DetalleNotaEvaluacionRespuestaDto mapearDetalle(DetalleNotaEvaluacion entidad) {
        DetalleNotaEvaluacionRespuestaDto dto = new DetalleNotaEvaluacionRespuestaDto();
        dto.setId(entidad.getId());
        dto.setMatriculaId(entidad.getMatricula().getId());
        dto.setAlumnoId(entidad.getMatricula().getAlumno().getId());
        dto.setCodigoAlumno(entidad.getMatricula().getAlumno().getCodigo());
        dto.setAlumnoNombreCompleto(
            entidad.getMatricula().getAlumno().getNombres() + " " + entidad.getMatricula().getAlumno().getApellidos()
        );
        dto.setNota(entidad.getNota());
        dto.setObservacion(entidad.getObservacion());
        return dto;
    }
}
