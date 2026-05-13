package com.tp1.proyecto.academico.servicio.impl;

import com.tp1.proyecto.academico.dto.PeriodoEvaluacionRespuestaDto;
import com.tp1.proyecto.academico.dto.PeriodoEvaluacionSolicitudDto;
import com.tp1.proyecto.academico.entidad.PeriodoEvaluacion;
import com.tp1.proyecto.academico.entidad.PeriodoAcademico;
import com.tp1.proyecto.academico.repositorio.PeriodoEvaluacionRepositorio;
import com.tp1.proyecto.academico.repositorio.PeriodoAcademicoRepositorio;
import com.tp1.proyecto.academico.servicio.PeriodoEvaluacionServicio;
import com.tp1.proyecto.excepcion.RecursoNoEncontradoException;
import com.tp1.proyecto.excepcion.ReglaNegocioException;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PeriodoEvaluacionServicioImpl implements PeriodoEvaluacionServicio {

    private final PeriodoEvaluacionRepositorio periodoEvaluacionRepositorio;
    private final PeriodoAcademicoRepositorio periodoAcademicoRepositorio;

    public PeriodoEvaluacionServicioImpl(
        PeriodoEvaluacionRepositorio periodoEvaluacionRepositorio,
        PeriodoAcademicoRepositorio periodoAcademicoRepositorio
    ) {
        this.periodoEvaluacionRepositorio = periodoEvaluacionRepositorio;
        this.periodoAcademicoRepositorio = periodoAcademicoRepositorio;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PeriodoEvaluacionRespuestaDto> listar() {
        return periodoEvaluacionRepositorio.findAll()
            .stream()
            .sorted(Comparator
                .comparing((PeriodoEvaluacion periodoEvaluacion) -> periodoEvaluacion.getPeriodoAcademico().getAnio(), Comparator.nullsLast(Integer::compareTo))
                .reversed()
                .thenComparing(PeriodoEvaluacion::getNumero, Comparator.nullsLast(Short::compareTo)))
            .map(this::mapearRespuesta)
            .toList();
    }

    @Override
    public PeriodoEvaluacionRespuestaDto crear(PeriodoEvaluacionSolicitudDto solicitud) {
        PeriodoAcademico periodoAcademico = periodoAcademicoRepositorio.findById(solicitud.getPeriodoAcademicoId())
            .orElseThrow(() -> new RecursoNoEncontradoException("Periodo academico no encontrado con id: " + solicitud.getPeriodoAcademicoId()));

        if (solicitud.getFechaFin().isBefore(solicitud.getFechaInicio())) {
            throw new ReglaNegocioException("La fecha de fin no puede ser menor que la fecha de inicio");
        }

        if (solicitud.getFechaInicio().isBefore(periodoAcademico.getFechaInicio())
            || solicitud.getFechaFin().isAfter(periodoAcademico.getFechaFin())) {
            throw new ReglaNegocioException("Las fechas del periodoEvaluacion deben estar dentro del periodo academico seleccionado");
        }

        boolean duplicadoNombre = periodoEvaluacionRepositorio.findByPeriodoAcademicoId(periodoAcademico.getId())
            .stream()
            .anyMatch(periodoEvaluacion -> normalizarTexto(solicitud.getNombre()).equalsIgnoreCase(periodoEvaluacion.getNombre()));

        if (duplicadoNombre) {
            throw new ReglaNegocioException("Ya existe un periodoEvaluacion con ese nombre para el periodo seleccionado");
        }

        boolean duplicadoNumero = periodoEvaluacionRepositorio.findByPeriodoAcademicoId(periodoAcademico.getId())
            .stream()
            .anyMatch(periodoEvaluacion -> solicitud.getNumero().equals(periodoEvaluacion.getNumero()));

        if (duplicadoNumero) {
            throw new ReglaNegocioException("Ya existe un periodoEvaluacion con ese numero para el periodo seleccionado");
        }

        PeriodoEvaluacion periodoEvaluacion = new PeriodoEvaluacion();
        periodoEvaluacion.setPeriodoAcademico(periodoAcademico);
        periodoEvaluacion.setNombre(normalizarTexto(solicitud.getNombre()));
        periodoEvaluacion.setNumero(solicitud.getNumero());
        periodoEvaluacion.setFechaInicio(solicitud.getFechaInicio());
        periodoEvaluacion.setFechaFin(solicitud.getFechaFin());

        return mapearRespuesta(periodoEvaluacionRepositorio.save(periodoEvaluacion));
    }

    private PeriodoEvaluacionRespuestaDto mapearRespuesta(PeriodoEvaluacion periodoEvaluacion) {
        PeriodoEvaluacionRespuestaDto dto = new PeriodoEvaluacionRespuestaDto();
        dto.setId(periodoEvaluacion.getId());
        dto.setNombre(periodoEvaluacion.getNombre());
        dto.setNumero(periodoEvaluacion.getNumero());
        dto.setFechaInicio(periodoEvaluacion.getFechaInicio());
        dto.setFechaFin(periodoEvaluacion.getFechaFin());
        dto.setEstado(periodoEvaluacion.getEstado() != null ? periodoEvaluacion.getEstado().name() : null);
        if (periodoEvaluacion.getPeriodoAcademico() != null) {
            dto.setPeriodoAcademicoId(periodoEvaluacion.getPeriodoAcademico().getId());
            dto.setPeriodoAcademicoNombre(periodoEvaluacion.getPeriodoAcademico().getNombre());
            dto.setAnioAcademico(periodoEvaluacion.getPeriodoAcademico().getAnio());
        }
        return dto;
    }

    private String normalizarTexto(String texto) {
        return texto == null ? null : texto.trim().toUpperCase();
    }
}
