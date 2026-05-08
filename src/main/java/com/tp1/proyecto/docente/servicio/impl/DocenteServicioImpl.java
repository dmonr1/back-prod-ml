package com.tp1.proyecto.docente.servicio.impl;

import com.tp1.proyecto.docente.dto.DocenteRespuestaDto;
import com.tp1.proyecto.docente.entidad.Docente;
import com.tp1.proyecto.docente.repositorio.DocenteRepositorio;
import com.tp1.proyecto.docente.servicio.DocenteServicio;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DocenteServicioImpl implements DocenteServicio {

    private final DocenteRepositorio docenteRepositorio;

    public DocenteServicioImpl(DocenteRepositorio docenteRepositorio) {
        this.docenteRepositorio = docenteRepositorio;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocenteRespuestaDto> listar() {
        return docenteRepositorio.findAll()
            .stream()
            .map(this::mapearRespuesta)
            .toList();
    }

    private DocenteRespuestaDto mapearRespuesta(Docente docente) {
        DocenteRespuestaDto dto = new DocenteRespuestaDto();
        dto.setId(docente.getId());
        dto.setDni(docente.getDni());
        dto.setNombres(docente.getNombres());
        dto.setApellidos(docente.getApellidos());
        dto.setTelefono(docente.getTelefono());
        dto.setEspecialidad(docente.getEspecialidad());
        dto.setEstado(docente.getEstado() != null ? docente.getEstado().name() : null);

        if (docente.getUsuario() != null) {
            dto.setUsuarioId(docente.getUsuario().getId());
            dto.setUsername(docente.getUsuario().getUsername());
            dto.setCorreo(docente.getUsuario().getCorreo());
        }

        return dto;
    }
}
