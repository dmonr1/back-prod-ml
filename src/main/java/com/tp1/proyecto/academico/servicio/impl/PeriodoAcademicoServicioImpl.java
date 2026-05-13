package com.tp1.proyecto.academico.servicio.impl;

import com.tp1.proyecto.academico.dto.PeriodoAcademicoConPeriodosRespuestaDto;
import com.tp1.proyecto.academico.dto.PeriodoAcademicoConPeriodosSolicitudDto;
import com.tp1.proyecto.academico.dto.PeriodoAcademicoRespuestaDto;
import com.tp1.proyecto.academico.dto.PeriodoAcademicoSolicitudDto;
import com.tp1.proyecto.academico.dto.PeriodoEvaluacionInicialSolicitudDto;
import com.tp1.proyecto.academico.dto.PeriodoEvaluacionRespuestaDto;
import com.tp1.proyecto.academico.entidad.PeriodoAcademico;
import com.tp1.proyecto.academico.entidad.PeriodoEvaluacion;
import com.tp1.proyecto.academico.repositorio.PeriodoAcademicoRepositorio;
import com.tp1.proyecto.academico.repositorio.PeriodoEvaluacionRepositorio;
import com.tp1.proyecto.academico.servicio.PeriodoAcademicoServicio;
import com.tp1.proyecto.excepcion.ReglaNegocioException;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PeriodoAcademicoServicioImpl implements PeriodoAcademicoServicio {

    private final PeriodoAcademicoRepositorio periodoAcademicoRepositorio;
    private final PeriodoEvaluacionRepositorio periodoEvaluacionRepositorio;

    public PeriodoAcademicoServicioImpl(
        PeriodoAcademicoRepositorio periodoAcademicoRepositorio,
        PeriodoEvaluacionRepositorio periodoEvaluacionRepositorio
    ) {
        this.periodoAcademicoRepositorio = periodoAcademicoRepositorio;
        this.periodoEvaluacionRepositorio = periodoEvaluacionRepositorio;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PeriodoAcademicoRespuestaDto> listar() {
        return periodoAcademicoRepositorio.findAll()
            .stream()
            .sorted(Comparator.comparing(PeriodoAcademico::getAnio, Comparator.nullsLast(Integer::compareTo)).reversed())
            .map(this::mapearRespuesta)
            .toList();
    }

    @Override
    public PeriodoAcademicoRespuestaDto crear(PeriodoAcademicoSolicitudDto solicitud) {
        validarPeriodoAcademico(solicitud);

        PeriodoAcademico periodoAcademico = construirPeriodoAcademico(solicitud);

        return mapearRespuesta(periodoAcademicoRepositorio.save(periodoAcademico));
    }

    @Override
    public PeriodoAcademicoConPeriodosRespuestaDto crearConPeriodosEvaluacion(PeriodoAcademicoConPeriodosSolicitudDto solicitud) {
        validarPeriodoAcademico(solicitud);
        validarPeriodosEvaluacion(solicitud);

        PeriodoAcademico periodoAcademico = periodoAcademicoRepositorio.save(construirPeriodoAcademico(solicitud));
        List<PeriodoEvaluacion> periodosEvaluacion = solicitud.getPeriodosEvaluacion()
            .stream()
            .map(periodo -> construirPeriodoEvaluacion(periodoAcademico, periodo))
            .toList();

        List<PeriodoEvaluacionRespuestaDto> periodosGuardados = periodoEvaluacionRepositorio.saveAll(periodosEvaluacion)
            .stream()
            .sorted(Comparator.comparing(PeriodoEvaluacion::getNumero))
            .map(this::mapearPeriodoEvaluacion)
            .toList();

        PeriodoAcademicoConPeriodosRespuestaDto respuesta = new PeriodoAcademicoConPeriodosRespuestaDto();
        respuesta.setPeriodoAcademico(mapearRespuesta(periodoAcademico));
        respuesta.setPeriodosEvaluacion(periodosGuardados);
        return respuesta;
    }

    private void validarPeriodoAcademico(PeriodoAcademicoSolicitudDto solicitud) {
        if (solicitud.getFechaFin().isBefore(solicitud.getFechaInicio())) {
            throw new ReglaNegocioException("La fecha de fin no puede ser menor que la fecha de inicio");
        }

        periodoAcademicoRepositorio.findByAnio(solicitud.getAnio())
            .ifPresent(periodoExistente -> {
                throw new ReglaNegocioException("Ya existe un periodo academico para ese anio");
            });
    }

    private PeriodoAcademico construirPeriodoAcademico(PeriodoAcademicoSolicitudDto solicitud) {
        PeriodoAcademico periodoAcademico = new PeriodoAcademico();
        periodoAcademico.setNombre(normalizarTexto(solicitud.getNombre()));
        periodoAcademico.setAnio(solicitud.getAnio());
        periodoAcademico.setFechaInicio(solicitud.getFechaInicio());
        periodoAcademico.setFechaFin(solicitud.getFechaFin());
        periodoAcademico.setTipoPeriodoEvaluacion(solicitud.getTipoPeriodoEvaluacion());
        return periodoAcademico;
    }

    private void validarPeriodosEvaluacion(PeriodoAcademicoConPeriodosSolicitudDto solicitud) {
        List<PeriodoEvaluacionInicialSolicitudDto> periodos = solicitud.getPeriodosEvaluacion();

        for (PeriodoEvaluacionInicialSolicitudDto periodo : periodos) {
            if (periodo.getFechaFin().isBefore(periodo.getFechaInicio())) {
                throw new ReglaNegocioException("La fecha de fin del periodo de evaluacion no puede ser menor que su fecha de inicio");
            }

            if (periodo.getFechaInicio().isBefore(solicitud.getFechaInicio()) || periodo.getFechaFin().isAfter(solicitud.getFechaFin())) {
                throw new ReglaNegocioException("Los periodos de evaluacion deben estar dentro del periodo academico");
            }
        }
    }

    private PeriodoEvaluacion construirPeriodoEvaluacion(
        PeriodoAcademico periodoAcademico,
        PeriodoEvaluacionInicialSolicitudDto solicitud
    ) {
        PeriodoEvaluacion periodoEvaluacion = new PeriodoEvaluacion();
        periodoEvaluacion.setPeriodoAcademico(periodoAcademico);
        periodoEvaluacion.setNombre(normalizarTexto(solicitud.getNombre()));
        periodoEvaluacion.setNumero(solicitud.getNumero());
        periodoEvaluacion.setFechaInicio(solicitud.getFechaInicio());
        periodoEvaluacion.setFechaFin(solicitud.getFechaFin());
        return periodoEvaluacion;
    }

    private PeriodoAcademicoRespuestaDto mapearRespuesta(PeriodoAcademico periodoAcademico) {
        PeriodoAcademicoRespuestaDto dto = new PeriodoAcademicoRespuestaDto();
        dto.setId(periodoAcademico.getId());
        dto.setNombre(periodoAcademico.getNombre());
        dto.setAnio(periodoAcademico.getAnio());
        dto.setFechaInicio(periodoAcademico.getFechaInicio());
        dto.setFechaFin(periodoAcademico.getFechaFin());
        dto.setTipoPeriodoEvaluacion(periodoAcademico.getTipoPeriodoEvaluacion());
        dto.setEstado(periodoAcademico.getEstado() != null ? periodoAcademico.getEstado().name() : null);
        return dto;
    }

    private PeriodoEvaluacionRespuestaDto mapearPeriodoEvaluacion(PeriodoEvaluacion periodoEvaluacion) {
        PeriodoEvaluacionRespuestaDto dto = new PeriodoEvaluacionRespuestaDto();
        dto.setId(periodoEvaluacion.getId());
        dto.setNombre(periodoEvaluacion.getNombre());
        dto.setNumero(periodoEvaluacion.getNumero());
        dto.setFechaInicio(periodoEvaluacion.getFechaInicio());
        dto.setFechaFin(periodoEvaluacion.getFechaFin());
        dto.setEstado(periodoEvaluacion.getEstado() != null ? periodoEvaluacion.getEstado().name() : null);
        dto.setPeriodoAcademicoId(periodoEvaluacion.getPeriodoAcademico().getId());
        dto.setPeriodoAcademicoNombre(periodoEvaluacion.getPeriodoAcademico().getNombre());
        dto.setAnioAcademico(periodoEvaluacion.getPeriodoAcademico().getAnio());
        return dto;
    }

    private String normalizarTexto(String texto) {
        return texto == null ? null : texto.trim().toUpperCase();
    }
}
