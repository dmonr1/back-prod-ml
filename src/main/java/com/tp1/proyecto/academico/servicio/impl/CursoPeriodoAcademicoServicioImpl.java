package com.tp1.proyecto.academico.servicio.impl;

import com.tp1.proyecto.academico.dto.CursoPeriodoAcademicoRespuestaDto;
import com.tp1.proyecto.academico.dto.CursoPeriodoAcademicoSolicitudDto;
import com.tp1.proyecto.academico.dto.CursoPeriodoAnteriorSolicitudDto;
import com.tp1.proyecto.academico.entidad.Curso;
import com.tp1.proyecto.academico.entidad.CursoPeriodoAcademico;
import com.tp1.proyecto.academico.entidad.PeriodoAcademico;
import com.tp1.proyecto.academico.repositorio.CursoPeriodoAcademicoRepositorio;
import com.tp1.proyecto.academico.repositorio.CursoRepositorio;
import com.tp1.proyecto.academico.repositorio.PeriodoAcademicoRepositorio;
import com.tp1.proyecto.academico.servicio.CursoPeriodoAcademicoServicio;
import com.tp1.proyecto.comun.enumeracion.EstadoRegistro;
import com.tp1.proyecto.excepcion.RecursoNoEncontradoException;
import com.tp1.proyecto.excepcion.ReglaNegocioException;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CursoPeriodoAcademicoServicioImpl implements CursoPeriodoAcademicoServicio {

    private final CursoPeriodoAcademicoRepositorio cursoPeriodoAcademicoRepositorio;
    private final CursoRepositorio cursoRepositorio;
    private final PeriodoAcademicoRepositorio periodoAcademicoRepositorio;

    public CursoPeriodoAcademicoServicioImpl(
        CursoPeriodoAcademicoRepositorio cursoPeriodoAcademicoRepositorio,
        CursoRepositorio cursoRepositorio,
        PeriodoAcademicoRepositorio periodoAcademicoRepositorio
    ) {
        this.cursoPeriodoAcademicoRepositorio = cursoPeriodoAcademicoRepositorio;
        this.cursoRepositorio = cursoRepositorio;
        this.periodoAcademicoRepositorio = periodoAcademicoRepositorio;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CursoPeriodoAcademicoRespuestaDto> listar(Long periodoAcademicoId) {
        List<CursoPeriodoAcademico> cursosPeriodo = periodoAcademicoId == null
            ? cursoPeriodoAcademicoRepositorio.findAll()
            : cursoPeriodoAcademicoRepositorio.findByPeriodoAcademicoId(periodoAcademicoId);

        return cursosPeriodo.stream()
            .sorted(
                Comparator
                    .comparing((CursoPeriodoAcademico item) -> item.getPeriodoAcademico().getAnio(), Comparator.nullsLast(Integer::compareTo))
                    .thenComparing(item -> item.getCurso().getNivel().getNombre(), Comparator.nullsLast(String::compareToIgnoreCase))
                    .thenComparing(item -> item.getCurso().getNombre(), Comparator.nullsLast(String::compareToIgnoreCase))
            )
            .map(this::mapear)
            .toList();
    }

    @Override
    public CursoPeriodoAcademicoRespuestaDto crear(CursoPeriodoAcademicoSolicitudDto solicitud) {
        PeriodoAcademico periodoAcademico = obtenerPeriodo(solicitud.getPeriodoAcademicoId());
        Curso curso = cursoRepositorio.findById(solicitud.getCursoId())
            .orElseThrow(() -> new RecursoNoEncontradoException("Curso no encontrado con id: " + solicitud.getCursoId()));

        if (cursoPeriodoAcademicoRepositorio.existsByPeriodoAcademicoIdAndCursoId(periodoAcademico.getId(), curso.getId())) {
            throw new ReglaNegocioException("El curso ya esta registrado para ese periodo academico.");
        }

        CursoPeriodoAcademico entidad = new CursoPeriodoAcademico();
        entidad.setPeriodoAcademico(periodoAcademico);
        entidad.setCurso(curso);

        return mapear(cursoPeriodoAcademicoRepositorio.save(entidad));
    }

    @Override
    public List<CursoPeriodoAcademicoRespuestaDto> copiarDesdePeriodoAnterior(CursoPeriodoAnteriorSolicitudDto solicitud) {
        PeriodoAcademico periodoActual = obtenerPeriodo(solicitud.getPeriodoAcademicoId());
        PeriodoAcademico periodoAnterior = periodoAcademicoRepositorio.findFirstByAnioLessThanOrderByAnioDesc(periodoActual.getAnio())
            .orElseThrow(() -> new ReglaNegocioException("No existe un periodo academico anterior para copiar cursos."));

        List<CursoPeriodoAcademico> cursosAnteriores = cursoPeriodoAcademicoRepositorio.findByPeriodoAcademicoId(periodoAnterior.getId())
            .stream()
            .filter(cursoPeriodo -> cursoPeriodo.getEstado() == EstadoRegistro.ACTIVO)
            .toList();

        if (cursosAnteriores.isEmpty()) {
            throw new ReglaNegocioException("El periodo anterior no tiene cursos registrados para copiar.");
        }

        List<CursoPeriodoAcademico> creados = cursosAnteriores.stream()
            .filter(cursoPeriodoAnterior ->
                !cursoPeriodoAcademicoRepositorio.existsByPeriodoAcademicoIdAndCursoId(
                    periodoActual.getId(),
                    cursoPeriodoAnterior.getCurso().getId()
                )
            )
            .map(cursoPeriodoAnterior -> {
                CursoPeriodoAcademico nuevo = new CursoPeriodoAcademico();
                nuevo.setPeriodoAcademico(periodoActual);
                nuevo.setCurso(cursoPeriodoAnterior.getCurso());
                return cursoPeriodoAcademicoRepositorio.save(nuevo);
            })
            .toList();

        if (creados.isEmpty()) {
            throw new ReglaNegocioException("Los cursos del periodo anterior ya fueron cargados para este anio academico.");
        }

        return creados.stream().map(this::mapear).toList();
    }

    @Override
    public CursoPeriodoAcademicoRespuestaDto actualizarEstado(Long cursoPeriodoAcademicoId, boolean activo) {
        CursoPeriodoAcademico cursoPeriodo = cursoPeriodoAcademicoRepositorio.findById(cursoPeriodoAcademicoId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Curso del periodo no encontrado con id: " + cursoPeriodoAcademicoId));

        cursoPeriodo.setEstado(activo ? EstadoRegistro.ACTIVO : EstadoRegistro.INACTIVO);
        return mapear(cursoPeriodoAcademicoRepositorio.save(cursoPeriodo));
    }

    private CursoPeriodoAcademicoRespuestaDto mapear(CursoPeriodoAcademico entidad) {
        CursoPeriodoAcademicoRespuestaDto dto = new CursoPeriodoAcademicoRespuestaDto();
        dto.setId(entidad.getId());
        dto.setPeriodoAcademicoId(entidad.getPeriodoAcademico().getId());
        dto.setPeriodoAcademicoNombre(entidad.getPeriodoAcademico().getNombre());
        dto.setAnioAcademico(entidad.getPeriodoAcademico().getAnio());
        dto.setCursoId(entidad.getCurso().getId());
        dto.setCursoNombre(entidad.getCurso().getNombre());
        dto.setCursoDescripcion(entidad.getCurso().getDescripcion());
        dto.setNivelId(entidad.getCurso().getNivel().getId());
        dto.setNivelNombre(entidad.getCurso().getNivel().getNombre());
        dto.setEstado(entidad.getEstado() != null ? entidad.getEstado().name() : null);
        return dto;
    }

    private PeriodoAcademico obtenerPeriodo(Long periodoAcademicoId) {
        return periodoAcademicoRepositorio.findById(periodoAcademicoId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Periodo academico no encontrado con id: " + periodoAcademicoId));
    }
}
