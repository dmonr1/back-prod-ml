package com.tp1.proyecto.evaluacion.servicio.impl;

import com.tp1.proyecto.academico.entidad.Matricula;
import com.tp1.proyecto.academico.entidad.DocenteCursoSeccion;
import com.tp1.proyecto.academico.repositorio.PeriodoEvaluacionRepositorio;
import com.tp1.proyecto.academico.repositorio.MatriculaRepositorio;
import com.tp1.proyecto.academico.repositorio.DocenteCursoSeccionRepositorio;
import com.tp1.proyecto.comun.enumeracion.EstadoRegistro;
import com.tp1.proyecto.evaluacion.dto.AsistenciaPeriodoEvaluacionRespuestaDto;
import com.tp1.proyecto.evaluacion.dto.AsistenciaPeriodoEvaluacionSolicitudDto;
import com.tp1.proyecto.evaluacion.dto.ConfiguracionAsistenciaPeriodoRespuestaDto;
import com.tp1.proyecto.evaluacion.dto.ConfiguracionAsistenciaPeriodoSolicitudDto;
import com.tp1.proyecto.evaluacion.dto.RegistroAsistenciasPeriodoEvaluacionSolicitudDto;
import com.tp1.proyecto.evaluacion.entidad.AsistenciaPeriodoEvaluacion;
import com.tp1.proyecto.evaluacion.entidad.ConfiguracionAsistenciaPeriodo;
import com.tp1.proyecto.evaluacion.repositorio.AsistenciaPeriodoEvaluacionRepositorio;
import com.tp1.proyecto.evaluacion.repositorio.ConfiguracionAsistenciaPeriodoRepositorio;
import com.tp1.proyecto.evaluacion.servicio.AsistenciaPeriodoEvaluacionServicio;
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
public class AsistenciaPeriodoEvaluacionServicioImpl implements AsistenciaPeriodoEvaluacionServicio {

    private final AsistenciaPeriodoEvaluacionRepositorio asistenciaPeriodoEvaluacionRepositorio;
    private final MatriculaRepositorio matriculaRepositorio;
    private final PeriodoEvaluacionRepositorio periodoEvaluacionRepositorio;
    private final DocenteCursoSeccionRepositorio docenteCursoSeccionRepositorio;
    private final ConfiguracionAsistenciaPeriodoRepositorio configuracionAsistenciaPeriodoRepositorio;
    private final PrediccionRiesgoServicio prediccionRiesgoServicio;

    public AsistenciaPeriodoEvaluacionServicioImpl(
        AsistenciaPeriodoEvaluacionRepositorio asistenciaPeriodoEvaluacionRepositorio,
        MatriculaRepositorio matriculaRepositorio,
        PeriodoEvaluacionRepositorio periodoEvaluacionRepositorio,
        DocenteCursoSeccionRepositorio docenteCursoSeccionRepositorio,
        ConfiguracionAsistenciaPeriodoRepositorio configuracionAsistenciaPeriodoRepositorio,
        PrediccionRiesgoServicio prediccionRiesgoServicio
    ) {
        this.asistenciaPeriodoEvaluacionRepositorio = asistenciaPeriodoEvaluacionRepositorio;
        this.matriculaRepositorio = matriculaRepositorio;
        this.periodoEvaluacionRepositorio = periodoEvaluacionRepositorio;
        this.docenteCursoSeccionRepositorio = docenteCursoSeccionRepositorio;
        this.configuracionAsistenciaPeriodoRepositorio = configuracionAsistenciaPeriodoRepositorio;
        this.prediccionRiesgoServicio = prediccionRiesgoServicio;
    }

    @Override
    public ConfiguracionAsistenciaPeriodoRespuestaDto guardarConfiguracion(
        Long periodoEvaluacionId,
        ConfiguracionAsistenciaPeriodoSolicitudDto solicitud
    ) {
        var periodoEvaluacion = periodoEvaluacionRepositorio.findById(periodoEvaluacionId)
            .orElseThrow(() -> new RecursoNoEncontradoException("PeriodoEvaluacion no encontrado con id: " + periodoEvaluacionId));

        DocenteCursoSeccion docenteCursoSeccion = docenteCursoSeccionRepositorio.findById(solicitud.getDocenteCursoSeccionId())
            .orElseThrow(() -> new RecursoNoEncontradoException("Asignacion docente-curso-seccion no encontrada con id: " + solicitud.getDocenteCursoSeccionId()));

        ConfiguracionAsistenciaPeriodo configuracion = configuracionAsistenciaPeriodoRepositorio
            .findByDocenteCursoSeccionIdAndPeriodoEvaluacionId(docenteCursoSeccion.getId(), periodoEvaluacionId)
            .orElseGet(ConfiguracionAsistenciaPeriodo::new);

        configuracion.setDocenteCursoSeccion(docenteCursoSeccion);
        configuracion.setPeriodoEvaluacion(periodoEvaluacion);
        configuracion.setClasesProgramadas(solicitud.getClasesProgramadas());
        configuracion.setEstado(EstadoRegistro.ACTIVO);

        ConfiguracionAsistenciaPeriodo guardada = configuracionAsistenciaPeriodoRepositorio.save(configuracion);
        return mapearConfiguracion(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public ConfiguracionAsistenciaPeriodoRespuestaDto obtenerConfiguracion(
        Long docenteCursoSeccionId,
        Long periodoEvaluacionId
    ) {
        return configuracionAsistenciaPeriodoRepositorio
            .findByDocenteCursoSeccionIdAndPeriodoEvaluacionId(docenteCursoSeccionId, periodoEvaluacionId)
            .map(this::mapearConfiguracion)
            .orElse(null);
    }

    @Override
    public List<AsistenciaPeriodoEvaluacionRespuestaDto> registrarAsistencias(Long periodoEvaluacionId, RegistroAsistenciasPeriodoEvaluacionSolicitudDto solicitud) {
        var periodoEvaluacion = periodoEvaluacionRepositorio.findById(periodoEvaluacionId)
            .orElseThrow(() -> new RecursoNoEncontradoException("PeriodoEvaluacion no encontrado con id: " + periodoEvaluacionId));

        List<AsistenciaPeriodoEvaluacionRespuestaDto> respuestas = new ArrayList<>();
        for (AsistenciaPeriodoEvaluacionSolicitudDto item : solicitud.getAsistencias()) {
            Matricula matricula = matriculaRepositorio.findById(item.getMatriculaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Matricula no encontrada con id: " + item.getMatriculaId()));

            ConfiguracionAsistenciaPeriodo configuracion = configuracionAsistenciaPeriodoRepositorio
                .findByDocenteCursoSeccionIdAndPeriodoEvaluacionId(
                    solicitud.getDocenteCursoSeccionId(),
                    periodoEvaluacionId
                )
                .orElseThrow(() -> new ReglaNegocioException("Primero configura las clases programadas para esta asignacion y periodo."));

            if (item.getClasesAsistidas() > configuracion.getClasesProgramadas()) {
                throw new ReglaNegocioException("Las clases asistidas no pueden ser mayores que las programadas");
            }

            AsistenciaPeriodoEvaluacion asistencia = asistenciaPeriodoEvaluacionRepositorio
                .findByMatriculaIdAndPeriodoEvaluacionId(item.getMatriculaId(), periodoEvaluacionId)
                .orElseGet(AsistenciaPeriodoEvaluacion::new);

            asistencia.setMatricula(matricula);
            asistencia.setPeriodoEvaluacion(periodoEvaluacion);
            asistencia.setClasesProgramadas(configuracion.getClasesProgramadas());
            asistencia.setClasesAsistidas(item.getClasesAsistidas());
            asistencia.setObservacion(item.getObservacion());
            asistencia.setEstado(EstadoRegistro.ACTIVO);

            AsistenciaPeriodoEvaluacion guardada = asistenciaPeriodoEvaluacionRepositorio.save(asistencia);
            prediccionRiesgoServicio.generarPrediccionGlobalPorMatricula(matricula.getId(), periodoEvaluacionId);
            respuestas.add(mapear(guardada));
        }

        return respuestas;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AsistenciaPeriodoEvaluacionRespuestaDto> listarPorSeccionYPeriodoEvaluacion(Long seccionId, Long periodoAcademicoId, Long periodoEvaluacionId) {
        periodoEvaluacionRepositorio.findById(periodoEvaluacionId)
            .orElseThrow(() -> new RecursoNoEncontradoException("PeriodoEvaluacion no encontrado con id: " + periodoEvaluacionId));

        Map<Long, AsistenciaPeriodoEvaluacion> asistenciaPorMatricula = new LinkedHashMap<>();
        for (Matricula matricula : matriculaRepositorio.findBySeccionIdAndPeriodoAcademicoId(seccionId, periodoAcademicoId)) {
            asistenciaPeriodoEvaluacionRepositorio.findByMatriculaIdAndPeriodoEvaluacionId(matricula.getId(), periodoEvaluacionId)
                .ifPresent(asistencia -> asistenciaPorMatricula.put(matricula.getId(), asistencia));
        }

        return asistenciaPorMatricula.values().stream().map(this::mapear).toList();
    }

    private AsistenciaPeriodoEvaluacionRespuestaDto mapear(AsistenciaPeriodoEvaluacion entidad) {
        AsistenciaPeriodoEvaluacionRespuestaDto dto = new AsistenciaPeriodoEvaluacionRespuestaDto();
        dto.setId(entidad.getId());
        dto.setMatriculaId(entidad.getMatricula().getId());
        dto.setAlumnoId(entidad.getMatricula().getAlumno().getId());
        dto.setCodigoAlumno(entidad.getMatricula().getAlumno().getCodigo());
        dto.setAlumnoNombreCompleto(
            entidad.getMatricula().getAlumno().getNombres() + " " + entidad.getMatricula().getAlumno().getApellidos()
        );
        dto.setPeriodoEvaluacionId(entidad.getPeriodoEvaluacion().getId());
        dto.setClasesProgramadas(entidad.getClasesProgramadas());
        dto.setClasesAsistidas(entidad.getClasesAsistidas());
        dto.setPorcentajeAsistencia(calcularPorcentaje(entidad.getClasesProgramadas(), entidad.getClasesAsistidas()));
        dto.setObservacion(entidad.getObservacion());
        return dto;
    }

    private ConfiguracionAsistenciaPeriodoRespuestaDto mapearConfiguracion(ConfiguracionAsistenciaPeriodo entidad) {
        ConfiguracionAsistenciaPeriodoRespuestaDto dto = new ConfiguracionAsistenciaPeriodoRespuestaDto();
        dto.setId(entidad.getId());
        dto.setDocenteCursoSeccionId(entidad.getDocenteCursoSeccion().getId());
        dto.setPeriodoEvaluacionId(entidad.getPeriodoEvaluacion().getId());
        dto.setClasesProgramadas(entidad.getClasesProgramadas());
        return dto;
    }

    private Double calcularPorcentaje(Integer clasesProgramadas, Integer clasesAsistidas) {
        if (clasesProgramadas == null || clasesProgramadas == 0) {
            return 0.0;
        }
        return BigDecimal.valueOf(clasesAsistidas)
            .multiply(BigDecimal.valueOf(100))
            .divide(BigDecimal.valueOf(clasesProgramadas), 2, RoundingMode.HALF_UP)
            .doubleValue();
    }
}
