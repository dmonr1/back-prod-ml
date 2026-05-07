package com.tp1.proyecto.academico.servicio.impl;

import com.tp1.proyecto.academico.dto.CursoRespuestaDto;
import com.tp1.proyecto.academico.dto.CursoSolicitudDto;
import com.tp1.proyecto.academico.entidad.Curso;
import com.tp1.proyecto.academico.entidad.Nivel;
import com.tp1.proyecto.academico.repositorio.CursoRepositorio;
import com.tp1.proyecto.academico.repositorio.NivelRepositorio;
import com.tp1.proyecto.academico.servicio.CursoServicio;
import com.tp1.proyecto.excepcion.RecursoNoEncontradoException;
import com.tp1.proyecto.excepcion.ReglaNegocioException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CursoServicioImpl implements CursoServicio {

    private final CursoRepositorio cursoRepositorio;
    private final NivelRepositorio nivelRepositorio;

    public CursoServicioImpl(CursoRepositorio cursoRepositorio, NivelRepositorio nivelRepositorio) {
        this.cursoRepositorio = cursoRepositorio;
        this.nivelRepositorio = nivelRepositorio;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CursoRespuestaDto> listar() {
        return cursoRepositorio.findAll()
            .stream()
            .map(this::mapearRespuesta)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CursoRespuestaDto obtenerPorId(Long id) {
        Curso curso = buscarCurso(id);
        return mapearRespuesta(curso);
    }

    @Override
    public CursoRespuestaDto crear(CursoSolicitudDto solicitud) {
        Nivel nivel = buscarNivel(solicitud.getNivelId());
        validarDuplicado(solicitud.getNombre(), nivel.getId(), null);

        Curso curso = new Curso();
        curso.setNombre(normalizarTexto(solicitud.getNombre()));
        curso.setDescripcion(solicitud.getDescripcion());
        curso.setNivel(nivel);

        return mapearRespuesta(cursoRepositorio.save(curso));
    }

    @Override
    public CursoRespuestaDto actualizar(Long id, CursoSolicitudDto solicitud) {
        Curso curso = buscarCurso(id);
        Nivel nivel = buscarNivel(solicitud.getNivelId());
        validarDuplicado(solicitud.getNombre(), nivel.getId(), curso.getId());

        curso.setNombre(normalizarTexto(solicitud.getNombre()));
        curso.setDescripcion(solicitud.getDescripcion());
        curso.setNivel(nivel);

        return mapearRespuesta(cursoRepositorio.save(curso));
    }

    private Curso buscarCurso(Long id) {
        return cursoRepositorio.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Curso no encontrado con id: " + id));
    }

    private Nivel buscarNivel(Long id) {
        return nivelRepositorio.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Nivel no encontrado con id: " + id));
    }

    private void validarDuplicado(String nombre, Long nivelId, Long cursoActualId) {
        cursoRepositorio.findByNombre(normalizarTexto(nombre))
            .ifPresent(cursoExistente -> {
                boolean mismoNivel = cursoExistente.getNivel() != null
                    && cursoExistente.getNivel().getId().equals(nivelId);
                boolean esOtroCurso = cursoActualId == null || !cursoExistente.getId().equals(cursoActualId);
                if (mismoNivel && esOtroCurso) {
                    throw new ReglaNegocioException("Ya existe un curso con ese nombre para el nivel seleccionado");
                }
            });
    }

    private CursoRespuestaDto mapearRespuesta(Curso curso) {
        CursoRespuestaDto dto = new CursoRespuestaDto();
        dto.setId(curso.getId());
        dto.setNombre(curso.getNombre());
        dto.setDescripcion(curso.getDescripcion());
        dto.setEstado(curso.getEstado() != null ? curso.getEstado().name() : null);
        if (curso.getNivel() != null) {
            dto.setNivelId(curso.getNivel().getId());
            dto.setNivelNombre(curso.getNivel().getNombre());
        }
        return dto;
    }

    private String normalizarTexto(String texto) {
        return texto == null ? null : texto.trim().toUpperCase();
    }
}
