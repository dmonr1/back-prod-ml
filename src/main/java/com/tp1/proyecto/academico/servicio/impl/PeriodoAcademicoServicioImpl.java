package com.tp1.proyecto.academico.servicio.impl;

import com.tp1.proyecto.academico.dto.PeriodoAcademicoRespuestaDto;
import com.tp1.proyecto.academico.entidad.PeriodoAcademico;
import com.tp1.proyecto.academico.repositorio.PeriodoAcademicoRepositorio;
import com.tp1.proyecto.academico.servicio.PeriodoAcademicoServicio;
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
}
