package com.tp1.proyecto.academico.servicio.impl;

import com.tp1.proyecto.academico.dto.SeccionRespuestaDto;
import com.tp1.proyecto.academico.dto.SeccionSolicitudDto;
import com.tp1.proyecto.academico.entidad.Grado;
import com.tp1.proyecto.academico.entidad.Seccion;
import com.tp1.proyecto.academico.repositorio.GradoRepositorio;
import com.tp1.proyecto.academico.repositorio.SeccionRepositorio;
import com.tp1.proyecto.academico.servicio.SeccionServicio;
import com.tp1.proyecto.excepcion.RecursoNoEncontradoException;
import com.tp1.proyecto.excepcion.ReglaNegocioException;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SeccionServicioImpl implements SeccionServicio {

    private final SeccionRepositorio seccionRepositorio;
    private final GradoRepositorio gradoRepositorio;

    public SeccionServicioImpl(SeccionRepositorio seccionRepositorio, GradoRepositorio gradoRepositorio) {
        this.seccionRepositorio = seccionRepositorio;
        this.gradoRepositorio = gradoRepositorio;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeccionRespuestaDto> listar() {
        return seccionRepositorio.findAll()
            .stream()
            .sorted(Comparator
                .comparing((Seccion seccion) -> seccion.getGrado().getNivel().getNombre(), Comparator.nullsLast(String::compareToIgnoreCase))
                .thenComparing(seccion -> seccion.getGrado().getOrden(), Comparator.nullsLast(Short::compareTo))
                .thenComparing(Seccion::getNombre, Comparator.nullsLast(String::compareToIgnoreCase)))
            .map(this::mapearRespuesta)
            .toList();
    }

    @Override
    public SeccionRespuestaDto crear(SeccionSolicitudDto solicitud) {
        Grado grado = gradoRepositorio.findById(solicitud.getGradoId())
            .orElseThrow(() -> new RecursoNoEncontradoException("Grado no encontrado con id: " + solicitud.getGradoId()));

        String nombreNormalizado = normalizarTexto(solicitud.getNombre());
        validarReglasSeccion(grado.getId(), nombreNormalizado);

        Seccion seccion = new Seccion();
        seccion.setGrado(grado);
        seccion.setNombre(nombreNormalizado);
        seccion.setCapacidad(solicitud.getCapacidad());

        return mapearRespuesta(seccionRepositorio.save(seccion));
    }

    private SeccionRespuestaDto mapearRespuesta(Seccion seccion) {
        SeccionRespuestaDto dto = new SeccionRespuestaDto();
        dto.setId(seccion.getId());
        dto.setNombre(seccion.getNombre());
        dto.setCapacidad(seccion.getCapacidad());
        dto.setEstado(seccion.getEstado() != null ? seccion.getEstado().name() : null);

        if (seccion.getGrado() != null) {
            dto.setGradoId(seccion.getGrado().getId());
            dto.setGradoNombre(seccion.getGrado().getNombre());
            if (seccion.getGrado().getNivel() != null) {
                dto.setNivelId(seccion.getGrado().getNivel().getId());
                dto.setNivelNombre(seccion.getGrado().getNivel().getNombre());
            }
        }

        return dto;
    }

    private void validarReglasSeccion(Long gradoId, String nombreNormalizado) {
        List<Seccion> seccionesActuales = seccionRepositorio.findByGradoId(gradoId);

        boolean existeUnica = seccionesActuales.stream()
            .anyMatch(seccion -> "UNICA".equalsIgnoreCase(seccion.getNombre()));

        boolean existeMismoNombre = seccionesActuales.stream()
            .anyMatch(seccion -> nombreNormalizado.equalsIgnoreCase(seccion.getNombre()));

        if (existeMismoNombre) {
            throw new ReglaNegocioException("Ya existe una seccion con ese nombre para el grado seleccionado");
        }

        if ("UNICA".equals(nombreNormalizado) && !seccionesActuales.isEmpty()) {
            throw new ReglaNegocioException("No se puede registrar seccion unica porque el grado ya tiene otras secciones");
        }

        if (!"UNICA".equals(nombreNormalizado) && existeUnica) {
            throw new ReglaNegocioException("No se pueden registrar otras secciones porque el grado ya tiene una seccion unica");
        }
    }

    private String normalizarTexto(String texto) {
        return texto == null ? null : texto.trim().toUpperCase();
    }
}
