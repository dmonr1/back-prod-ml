package com.tp1.proyecto.academico.servicio.impl;

import com.tp1.proyecto.academico.dto.GradoRespuestaDto;
import com.tp1.proyecto.academico.dto.GradoSolicitudDto;
import com.tp1.proyecto.academico.entidad.Grado;
import com.tp1.proyecto.academico.entidad.Nivel;
import com.tp1.proyecto.academico.repositorio.GradoRepositorio;
import com.tp1.proyecto.academico.repositorio.NivelRepositorio;
import com.tp1.proyecto.academico.repositorio.SeccionRepositorio;
import com.tp1.proyecto.academico.servicio.GradoServicio;
import com.tp1.proyecto.excepcion.RecursoNoEncontradoException;
import com.tp1.proyecto.excepcion.ReglaNegocioException;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class GradoServicioImpl implements GradoServicio {

    private final GradoRepositorio gradoRepositorio;
    private final NivelRepositorio nivelRepositorio;
    private final SeccionRepositorio seccionRepositorio;

    public GradoServicioImpl(
        GradoRepositorio gradoRepositorio,
        NivelRepositorio nivelRepositorio,
        SeccionRepositorio seccionRepositorio
    ) {
        this.gradoRepositorio = gradoRepositorio;
        this.nivelRepositorio = nivelRepositorio;
        this.seccionRepositorio = seccionRepositorio;
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradoRespuestaDto> listar() {
        return gradoRepositorio.findAll()
            .stream()
            .sorted(Comparator
                .comparing((Grado grado) -> grado.getNivel().getNombre(), Comparator.nullsLast(String::compareToIgnoreCase))
                .thenComparing(Grado::getOrden, Comparator.nullsLast(Short::compareTo))
                .thenComparing(Grado::getNombre, Comparator.nullsLast(String::compareToIgnoreCase)))
            .map(this::mapearRespuesta)
            .toList();
    }

    @Override
    public GradoRespuestaDto crear(GradoSolicitudDto solicitud) {
        Nivel nivel = nivelRepositorio.findById(solicitud.getNivelId())
            .orElseThrow(() -> new RecursoNoEncontradoException("Nivel no encontrado con id: " + solicitud.getNivelId()));

        String nombreNormalizado = normalizarTexto(solicitud.getNombre());
        gradoRepositorio.findByNombreAndNivelId(nombreNormalizado, nivel.getId())
            .ifPresent(gradoExistente -> {
                throw new ReglaNegocioException("Ya existe un grado con ese nombre para el nivel seleccionado");
            });

        Grado grado = new Grado();
        grado.setNombre(nombreNormalizado);
        grado.setOrden(solicitud.getOrden());
        grado.setNivel(nivel);

        return mapearRespuesta(gradoRepositorio.save(grado));
    }

    private GradoRespuestaDto mapearRespuesta(Grado grado) {
        GradoRespuestaDto dto = new GradoRespuestaDto();
        dto.setId(grado.getId());
        dto.setNombre(grado.getNombre());
        dto.setOrden(grado.getOrden());
        dto.setEstado(grado.getEstado() != null ? grado.getEstado().name() : null);
        if (grado.getNivel() != null) {
            dto.setNivelId(grado.getNivel().getId());
            dto.setNivelNombre(grado.getNivel().getNombre());
        }
        dto.setTotalSecciones(seccionRepositorio.findByGradoId(grado.getId()).size());
        return dto;
    }

    private String normalizarTexto(String texto) {
        return texto == null ? null : texto.trim().toUpperCase();
    }
}
