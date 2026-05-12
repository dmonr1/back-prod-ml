package com.tp1.proyecto.academico.servicio.impl;

import com.tp1.proyecto.academico.dto.SeccionPeriodoAnteriorSolicitudDto;
import com.tp1.proyecto.academico.dto.SeccionRespuestaDto;
import com.tp1.proyecto.academico.dto.SeccionSolicitudDto;
import com.tp1.proyecto.academico.entidad.Grado;
import com.tp1.proyecto.academico.entidad.PeriodoAcademico;
import com.tp1.proyecto.academico.entidad.Seccion;
import com.tp1.proyecto.academico.repositorio.GradoRepositorio;
import com.tp1.proyecto.academico.repositorio.PeriodoAcademicoRepositorio;
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
    private final PeriodoAcademicoRepositorio periodoAcademicoRepositorio;

    public SeccionServicioImpl(
        SeccionRepositorio seccionRepositorio,
        GradoRepositorio gradoRepositorio,
        PeriodoAcademicoRepositorio periodoAcademicoRepositorio
    ) {
        this.seccionRepositorio = seccionRepositorio;
        this.gradoRepositorio = gradoRepositorio;
        this.periodoAcademicoRepositorio = periodoAcademicoRepositorio;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeccionRespuestaDto> listar(Long periodoAcademicoId) {
        List<Seccion> secciones = periodoAcademicoId == null
            ? seccionRepositorio.findAll()
            : seccionRepositorio.findByPeriodoAcademicoId(periodoAcademicoId);

        return secciones.stream()
            .sorted(Comparator
                .comparing((Seccion seccion) -> seccion.getPeriodoAcademico().getAnio(), Comparator.nullsLast(Integer::compareTo))
                .thenComparing(seccion -> seccion.getGrado().getNivel().getNombre(), Comparator.nullsLast(String::compareToIgnoreCase))
                .thenComparing(seccion -> seccion.getGrado().getOrden(), Comparator.nullsLast(Short::compareTo))
                .thenComparing(Seccion::getNombre, Comparator.nullsLast(String::compareToIgnoreCase)))
            .map(this::mapearRespuesta)
            .toList();
    }

    @Override
    public SeccionRespuestaDto crear(SeccionSolicitudDto solicitud) {
        Grado grado = gradoRepositorio.findById(solicitud.getGradoId())
            .orElseThrow(() -> new RecursoNoEncontradoException("Grado no encontrado con id: " + solicitud.getGradoId()));
        PeriodoAcademico periodoAcademico = periodoAcademicoRepositorio.findById(solicitud.getPeriodoAcademicoId())
            .orElseThrow(() -> new RecursoNoEncontradoException("Periodo academico no encontrado con id: " + solicitud.getPeriodoAcademicoId()));

        String nombreNormalizado = normalizarTexto(solicitud.getNombre());
        validarReglasSeccion(grado.getId(), periodoAcademico.getId(), nombreNormalizado);

        Seccion seccion = new Seccion();
        seccion.setGrado(grado);
        seccion.setPeriodoAcademico(periodoAcademico);
        seccion.setNombre(nombreNormalizado);
        seccion.setCapacidad(solicitud.getCapacidad());

        return mapearRespuesta(seccionRepositorio.save(seccion));
    }

    @Override
    public List<SeccionRespuestaDto> copiarDesdePeriodoAnterior(SeccionPeriodoAnteriorSolicitudDto solicitud) {
        PeriodoAcademico periodoActual = periodoAcademicoRepositorio.findById(solicitud.getPeriodoAcademicoId())
            .orElseThrow(() -> new RecursoNoEncontradoException("Periodo academico no encontrado con id: " + solicitud.getPeriodoAcademicoId()));
        Grado grado = gradoRepositorio.findById(solicitud.getGradoId())
            .orElseThrow(() -> new RecursoNoEncontradoException("Grado no encontrado con id: " + solicitud.getGradoId()));

        PeriodoAcademico periodoAnterior = periodoAcademicoRepositorio
            .findFirstByAnioLessThanOrderByAnioDesc(periodoActual.getAnio())
            .orElseThrow(() -> new ReglaNegocioException("No existe un periodo academico anterior para copiar secciones."));

        List<Seccion> seccionesAnteriores = seccionRepositorio.findByGradoIdAndPeriodoAcademicoId(grado.getId(), periodoAnterior.getId());
        if (seccionesAnteriores.isEmpty()) {
            throw new ReglaNegocioException("El grado seleccionado no tiene secciones en el periodo anterior.");
        }

        List<Seccion> seccionesActuales = seccionRepositorio.findByGradoIdAndPeriodoAcademicoId(grado.getId(), periodoActual.getId());

        List<Seccion> creadas = seccionesAnteriores.stream()
            .filter(seccionAnterior -> seccionesActuales.stream()
                .noneMatch(seccionActual -> seccionActual.getNombre().equalsIgnoreCase(seccionAnterior.getNombre())))
            .map(seccionAnterior -> {
                validarReglasSeccion(grado.getId(), periodoActual.getId(), seccionAnterior.getNombre());
                Seccion seccionNueva = new Seccion();
                seccionNueva.setGrado(grado);
                seccionNueva.setPeriodoAcademico(periodoActual);
                seccionNueva.setNombre(seccionAnterior.getNombre());
                seccionNueva.setCapacidad(seccionAnterior.getCapacidad());
                return seccionRepositorio.save(seccionNueva);
            })
            .toList();

        if (creadas.isEmpty()) {
            throw new ReglaNegocioException("Las secciones del periodo anterior ya fueron cargadas para este grado.");
        }

        return creadas.stream().map(this::mapearRespuesta).toList();
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

        if (seccion.getPeriodoAcademico() != null) {
            dto.setPeriodoAcademicoId(seccion.getPeriodoAcademico().getId());
            dto.setPeriodoAcademicoNombre(seccion.getPeriodoAcademico().getNombre());
            dto.setAnioAcademico(seccion.getPeriodoAcademico().getAnio());
        }

        return dto;
    }

    private void validarReglasSeccion(Long gradoId, Long periodoAcademicoId, String nombreNormalizado) {
        List<Seccion> seccionesActuales = seccionRepositorio.findByGradoIdAndPeriodoAcademicoId(gradoId, periodoAcademicoId);

        boolean existeUnica = seccionesActuales.stream()
            .anyMatch(seccion -> "UNICA".equalsIgnoreCase(seccion.getNombre()));

        boolean existeMismoNombre = seccionesActuales.stream()
            .anyMatch(seccion -> nombreNormalizado.equalsIgnoreCase(seccion.getNombre()));

        if (existeMismoNombre) {
            throw new ReglaNegocioException("Ya existe una seccion con ese nombre para el grado y periodo seleccionados");
        }

        if ("UNICA".equals(nombreNormalizado) && !seccionesActuales.isEmpty()) {
            throw new ReglaNegocioException("No se puede registrar seccion unica porque el grado ya tiene otras secciones en este periodo");
        }

        if (!"UNICA".equals(nombreNormalizado) && existeUnica) {
            throw new ReglaNegocioException("No se pueden registrar otras secciones porque el grado ya tiene una seccion unica en este periodo");
        }
    }

    private String normalizarTexto(String texto) {
        return texto == null ? null : texto.trim().toUpperCase();
    }
}
