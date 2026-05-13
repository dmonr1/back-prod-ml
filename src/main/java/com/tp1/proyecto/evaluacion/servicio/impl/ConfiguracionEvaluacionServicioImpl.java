package com.tp1.proyecto.evaluacion.servicio.impl;

import com.tp1.proyecto.academico.entidad.PeriodoEvaluacion;
import com.tp1.proyecto.academico.entidad.Curso;
import com.tp1.proyecto.academico.entidad.Grado;
import com.tp1.proyecto.academico.entidad.PeriodoAcademico;
import com.tp1.proyecto.academico.repositorio.PeriodoEvaluacionRepositorio;
import com.tp1.proyecto.academico.repositorio.CursoRepositorio;
import com.tp1.proyecto.academico.repositorio.GradoRepositorio;
import com.tp1.proyecto.academico.repositorio.PeriodoAcademicoRepositorio;
import com.tp1.proyecto.evaluacion.dto.ConfiguracionEvaluacionRespuestaDto;
import com.tp1.proyecto.evaluacion.dto.ConfiguracionEvaluacionSolicitudDto;
import com.tp1.proyecto.evaluacion.entidad.ConfiguracionEvaluacion;
import com.tp1.proyecto.evaluacion.entidad.TipoEvaluacion;
import com.tp1.proyecto.evaluacion.repositorio.ConfiguracionEvaluacionRepositorio;
import com.tp1.proyecto.evaluacion.repositorio.TipoEvaluacionRepositorio;
import com.tp1.proyecto.evaluacion.servicio.ConfiguracionEvaluacionServicio;
import com.tp1.proyecto.excepcion.RecursoNoEncontradoException;
import com.tp1.proyecto.excepcion.ReglaNegocioException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ConfiguracionEvaluacionServicioImpl implements ConfiguracionEvaluacionServicio {

    private final ConfiguracionEvaluacionRepositorio configuracionEvaluacionRepositorio;
    private final PeriodoAcademicoRepositorio periodoAcademicoRepositorio;
    private final PeriodoEvaluacionRepositorio periodoEvaluacionRepositorio;
    private final CursoRepositorio cursoRepositorio;
    private final GradoRepositorio gradoRepositorio;
    private final TipoEvaluacionRepositorio tipoEvaluacionRepositorio;

    public ConfiguracionEvaluacionServicioImpl(
        ConfiguracionEvaluacionRepositorio configuracionEvaluacionRepositorio,
        PeriodoAcademicoRepositorio periodoAcademicoRepositorio,
        PeriodoEvaluacionRepositorio periodoEvaluacionRepositorio,
        CursoRepositorio cursoRepositorio,
        GradoRepositorio gradoRepositorio,
        TipoEvaluacionRepositorio tipoEvaluacionRepositorio
    ) {
        this.configuracionEvaluacionRepositorio = configuracionEvaluacionRepositorio;
        this.periodoAcademicoRepositorio = periodoAcademicoRepositorio;
        this.periodoEvaluacionRepositorio = periodoEvaluacionRepositorio;
        this.cursoRepositorio = cursoRepositorio;
        this.gradoRepositorio = gradoRepositorio;
        this.tipoEvaluacionRepositorio = tipoEvaluacionRepositorio;
    }

    @Override
    public ConfiguracionEvaluacionRespuestaDto crear(ConfiguracionEvaluacionSolicitudDto solicitud) {
        PeriodoAcademico periodoAcademico = periodoAcademicoRepositorio.findById(solicitud.getPeriodoAcademicoId())
            .orElseThrow(() -> new RecursoNoEncontradoException("Periodo academico no encontrado con id: " + solicitud.getPeriodoAcademicoId()));

        PeriodoEvaluacion periodoEvaluacion = periodoEvaluacionRepositorio.findById(solicitud.getPeriodoEvaluacionId())
            .orElseThrow(() -> new RecursoNoEncontradoException("PeriodoEvaluacion no encontrado con id: " + solicitud.getPeriodoEvaluacionId()));

        Curso curso = cursoRepositorio.findById(solicitud.getCursoId())
            .orElseThrow(() -> new RecursoNoEncontradoException("Curso no encontrado con id: " + solicitud.getCursoId()));

        TipoEvaluacion tipoEvaluacion = tipoEvaluacionRepositorio.findById(solicitud.getTipoEvaluacionId())
            .orElseThrow(() -> new RecursoNoEncontradoException("Tipo de evaluacion no encontrado con id: " + solicitud.getTipoEvaluacionId()));

        if (!periodoEvaluacion.getPeriodoAcademico().getId().equals(periodoAcademico.getId())) {
            throw new ReglaNegocioException("El periodoEvaluacion no pertenece al periodo academico seleccionado");
        }

        Grado grado = null;
        if (solicitud.getGradoId() != null) {
            grado = gradoRepositorio.findById(solicitud.getGradoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Grado no encontrado con id: " + solicitud.getGradoId()));
        }

        ConfiguracionEvaluacion configuracion = new ConfiguracionEvaluacion();
        configuracion.setPeriodoAcademico(periodoAcademico);
        configuracion.setPeriodoEvaluacion(periodoEvaluacion);
        configuracion.setCurso(curso);
        configuracion.setGrado(grado);
        configuracion.setTipoEvaluacion(tipoEvaluacion);
        configuracion.setCantidadEvaluaciones(solicitud.getCantidadEvaluaciones());
        configuracion.setCalcularEnPromedio(solicitud.getCalcularEnPromedio());

        return mapear(configuracionEvaluacionRepositorio.save(configuracion));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConfiguracionEvaluacionRespuestaDto> listarPorPeriodoEvaluacionYCurso(Long periodoEvaluacionId, Long cursoId) {
        return configuracionEvaluacionRepositorio.findByPeriodoEvaluacionIdAndCursoIdOrderByTipoEvaluacionOrdenAsc(periodoEvaluacionId, cursoId)
            .stream()
            .map(this::mapear)
            .toList();
    }

    private ConfiguracionEvaluacionRespuestaDto mapear(ConfiguracionEvaluacion entidad) {
        ConfiguracionEvaluacionRespuestaDto dto = new ConfiguracionEvaluacionRespuestaDto();
        dto.setId(entidad.getId());
        dto.setPeriodoAcademicoId(entidad.getPeriodoAcademico().getId());
        dto.setPeriodoEvaluacionId(entidad.getPeriodoEvaluacion().getId());
        dto.setNombrePeriodoEvaluacion(entidad.getPeriodoEvaluacion().getNombre());
        dto.setCursoId(entidad.getCurso().getId());
        dto.setNombreCurso(entidad.getCurso().getNombre());
        if (entidad.getGrado() != null) {
            dto.setGradoId(entidad.getGrado().getId());
            dto.setNombreGrado(entidad.getGrado().getNombre());
        }
        dto.setTipoEvaluacionId(entidad.getTipoEvaluacion().getId());
        dto.setNombreTipoEvaluacion(entidad.getTipoEvaluacion().getNombre());
        dto.setCantidadEvaluaciones(entidad.getCantidadEvaluaciones());
        dto.setCalcularEnPromedio(entidad.getCalcularEnPromedio());
        dto.setEstado(entidad.getEstado().name());
        return dto;
    }
}
