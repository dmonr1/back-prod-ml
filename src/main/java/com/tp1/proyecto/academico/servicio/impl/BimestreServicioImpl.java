package com.tp1.proyecto.academico.servicio.impl;

import com.tp1.proyecto.academico.dto.BimestreRespuestaDto;
import com.tp1.proyecto.academico.dto.BimestreSolicitudDto;
import com.tp1.proyecto.academico.entidad.Bimestre;
import com.tp1.proyecto.academico.entidad.PeriodoAcademico;
import com.tp1.proyecto.academico.repositorio.BimestreRepositorio;
import com.tp1.proyecto.academico.repositorio.PeriodoAcademicoRepositorio;
import com.tp1.proyecto.academico.servicio.BimestreServicio;
import com.tp1.proyecto.excepcion.RecursoNoEncontradoException;
import com.tp1.proyecto.excepcion.ReglaNegocioException;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BimestreServicioImpl implements BimestreServicio {

    private final BimestreRepositorio bimestreRepositorio;
    private final PeriodoAcademicoRepositorio periodoAcademicoRepositorio;

    public BimestreServicioImpl(
        BimestreRepositorio bimestreRepositorio,
        PeriodoAcademicoRepositorio periodoAcademicoRepositorio
    ) {
        this.bimestreRepositorio = bimestreRepositorio;
        this.periodoAcademicoRepositorio = periodoAcademicoRepositorio;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BimestreRespuestaDto> listar() {
        return bimestreRepositorio.findAll()
            .stream()
            .sorted(Comparator
                .comparing((Bimestre bimestre) -> bimestre.getPeriodoAcademico().getAnio(), Comparator.nullsLast(Integer::compareTo))
                .reversed()
                .thenComparing(Bimestre::getNumero, Comparator.nullsLast(Short::compareTo)))
            .map(this::mapearRespuesta)
            .toList();
    }

    @Override
    public BimestreRespuestaDto crear(BimestreSolicitudDto solicitud) {
        PeriodoAcademico periodoAcademico = periodoAcademicoRepositorio.findById(solicitud.getPeriodoAcademicoId())
            .orElseThrow(() -> new RecursoNoEncontradoException("Periodo academico no encontrado con id: " + solicitud.getPeriodoAcademicoId()));

        if (solicitud.getFechaFin().isBefore(solicitud.getFechaInicio())) {
            throw new ReglaNegocioException("La fecha de fin no puede ser menor que la fecha de inicio");
        }

        if (solicitud.getFechaInicio().isBefore(periodoAcademico.getFechaInicio())
            || solicitud.getFechaFin().isAfter(periodoAcademico.getFechaFin())) {
            throw new ReglaNegocioException("Las fechas del bimestre deben estar dentro del periodo academico seleccionado");
        }

        boolean duplicadoNombre = bimestreRepositorio.findByPeriodoAcademicoId(periodoAcademico.getId())
            .stream()
            .anyMatch(bimestre -> normalizarTexto(solicitud.getNombre()).equalsIgnoreCase(bimestre.getNombre()));

        if (duplicadoNombre) {
            throw new ReglaNegocioException("Ya existe un bimestre con ese nombre para el periodo seleccionado");
        }

        boolean duplicadoNumero = bimestreRepositorio.findByPeriodoAcademicoId(periodoAcademico.getId())
            .stream()
            .anyMatch(bimestre -> solicitud.getNumero().equals(bimestre.getNumero()));

        if (duplicadoNumero) {
            throw new ReglaNegocioException("Ya existe un bimestre con ese numero para el periodo seleccionado");
        }

        Bimestre bimestre = new Bimestre();
        bimestre.setPeriodoAcademico(periodoAcademico);
        bimestre.setNombre(normalizarTexto(solicitud.getNombre()));
        bimestre.setNumero(solicitud.getNumero());
        bimestre.setFechaInicio(solicitud.getFechaInicio());
        bimestre.setFechaFin(solicitud.getFechaFin());

        return mapearRespuesta(bimestreRepositorio.save(bimestre));
    }

    private BimestreRespuestaDto mapearRespuesta(Bimestre bimestre) {
        BimestreRespuestaDto dto = new BimestreRespuestaDto();
        dto.setId(bimestre.getId());
        dto.setNombre(bimestre.getNombre());
        dto.setNumero(bimestre.getNumero());
        dto.setFechaInicio(bimestre.getFechaInicio());
        dto.setFechaFin(bimestre.getFechaFin());
        dto.setEstado(bimestre.getEstado() != null ? bimestre.getEstado().name() : null);
        if (bimestre.getPeriodoAcademico() != null) {
            dto.setPeriodoAcademicoId(bimestre.getPeriodoAcademico().getId());
            dto.setPeriodoAcademicoNombre(bimestre.getPeriodoAcademico().getNombre());
            dto.setAnioAcademico(bimestre.getPeriodoAcademico().getAnio());
        }
        return dto;
    }

    private String normalizarTexto(String texto) {
        return texto == null ? null : texto.trim().toUpperCase();
    }
}
