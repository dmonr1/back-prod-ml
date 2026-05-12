package com.tp1.proyecto.academico.servicio.impl;

import com.tp1.proyecto.academico.dto.PeriodoAcademicoRespuestaDto;
import com.tp1.proyecto.academico.dto.PeriodoAcademicoSolicitudDto;
import com.tp1.proyecto.academico.entidad.PeriodoAcademico;
import com.tp1.proyecto.academico.repositorio.PeriodoAcademicoRepositorio;
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

    public PeriodoAcademicoServicioImpl(PeriodoAcademicoRepositorio periodoAcademicoRepositorio) {
        this.periodoAcademicoRepositorio = periodoAcademicoRepositorio;
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
        if (solicitud.getFechaFin().isBefore(solicitud.getFechaInicio())) {
            throw new ReglaNegocioException("La fecha de fin no puede ser menor que la fecha de inicio");
        }

        periodoAcademicoRepositorio.findByAnio(solicitud.getAnio())
            .ifPresent(periodoExistente -> {
                throw new ReglaNegocioException("Ya existe un periodo academico para ese anio");
            });

        PeriodoAcademico periodoAcademico = new PeriodoAcademico();
        periodoAcademico.setNombre(normalizarTexto(solicitud.getNombre()));
        periodoAcademico.setAnio(solicitud.getAnio());
        periodoAcademico.setFechaInicio(solicitud.getFechaInicio());
        periodoAcademico.setFechaFin(solicitud.getFechaFin());

        return mapearRespuesta(periodoAcademicoRepositorio.save(periodoAcademico));
    }

    private PeriodoAcademicoRespuestaDto mapearRespuesta(PeriodoAcademico periodoAcademico) {
        PeriodoAcademicoRespuestaDto dto = new PeriodoAcademicoRespuestaDto();
        dto.setId(periodoAcademico.getId());
        dto.setNombre(periodoAcademico.getNombre());
        dto.setAnio(periodoAcademico.getAnio());
        dto.setFechaInicio(periodoAcademico.getFechaInicio());
        dto.setFechaFin(periodoAcademico.getFechaFin());
        dto.setEstado(periodoAcademico.getEstado() != null ? periodoAcademico.getEstado().name() : null);
        return dto;
    }

    private String normalizarTexto(String texto) {
        return texto == null ? null : texto.trim().toUpperCase();
    }
}
