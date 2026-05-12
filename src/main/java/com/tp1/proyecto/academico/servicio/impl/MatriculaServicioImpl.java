package com.tp1.proyecto.academico.servicio.impl;

import com.tp1.proyecto.academico.dto.MatriculaRespuestaDto;
import com.tp1.proyecto.academico.dto.MatriculaSolicitudDto;
import com.tp1.proyecto.academico.entidad.Matricula;
import com.tp1.proyecto.academico.entidad.PeriodoAcademico;
import com.tp1.proyecto.academico.entidad.Seccion;
import com.tp1.proyecto.academico.repositorio.MatriculaRepositorio;
import com.tp1.proyecto.academico.repositorio.PeriodoAcademicoRepositorio;
import com.tp1.proyecto.academico.repositorio.SeccionRepositorio;
import com.tp1.proyecto.academico.servicio.MatriculaServicio;
import com.tp1.proyecto.alumno.entidad.Alumno;
import com.tp1.proyecto.alumno.repositorio.AlumnoRepositorio;
import com.tp1.proyecto.excepcion.RecursoNoEncontradoException;
import com.tp1.proyecto.excepcion.ReglaNegocioException;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MatriculaServicioImpl implements MatriculaServicio {

    private final MatriculaRepositorio matriculaRepositorio;
    private final AlumnoRepositorio alumnoRepositorio;
    private final SeccionRepositorio seccionRepositorio;
    private final PeriodoAcademicoRepositorio periodoAcademicoRepositorio;

    public MatriculaServicioImpl(
        MatriculaRepositorio matriculaRepositorio,
        AlumnoRepositorio alumnoRepositorio,
        SeccionRepositorio seccionRepositorio,
        PeriodoAcademicoRepositorio periodoAcademicoRepositorio
    ) {
        this.matriculaRepositorio = matriculaRepositorio;
        this.alumnoRepositorio = alumnoRepositorio;
        this.seccionRepositorio = seccionRepositorio;
        this.periodoAcademicoRepositorio = periodoAcademicoRepositorio;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatriculaRespuestaDto> listar(Long periodoAcademicoId, Long seccionId) {
        obtenerPeriodo(periodoAcademicoId);

        List<Matricula> matriculas = seccionId == null
            ? matriculaRepositorio.findByPeriodoAcademicoId(periodoAcademicoId)
            : matriculaRepositorio.findBySeccionIdAndPeriodoAcademicoId(seccionId, periodoAcademicoId);

        return matriculas.stream()
            .map(this::mapearRespuesta)
            .toList();
    }

    @Override
    public MatriculaRespuestaDto crear(MatriculaSolicitudDto solicitud) {
        Alumno alumno = alumnoRepositorio.findById(solicitud.getAlumnoId())
            .orElseThrow(() -> new RecursoNoEncontradoException("Alumno no encontrado con id: " + solicitud.getAlumnoId()));
        Seccion seccion = seccionRepositorio.findById(solicitud.getSeccionId())
            .orElseThrow(() -> new RecursoNoEncontradoException("Seccion no encontrada con id: " + solicitud.getSeccionId()));
        PeriodoAcademico periodoAcademico = obtenerPeriodo(solicitud.getPeriodoAcademicoId());

        matriculaRepositorio.findByAlumnoIdAndPeriodoAcademicoId(alumno.getId(), periodoAcademico.getId())
            .ifPresent(matriculaExistente -> {
                throw new ReglaNegocioException("El alumno ya esta matriculado en este periodo academico.");
            });

        Matricula matricula = new Matricula();
        matricula.setAlumno(alumno);
        matricula.setGrado(seccion.getGrado());
        matricula.setSeccion(seccion);
        matricula.setPeriodoAcademico(periodoAcademico);
        matricula.setFechaMatricula(
            solicitud.getFechaMatricula() != null ? solicitud.getFechaMatricula() : LocalDate.now()
        );

        return mapearRespuesta(matriculaRepositorio.save(matricula));
    }

    private PeriodoAcademico obtenerPeriodo(Long periodoAcademicoId) {
        return periodoAcademicoRepositorio.findById(periodoAcademicoId)
            .orElseThrow(() ->
                new RecursoNoEncontradoException("Periodo academico no encontrado con id: " + periodoAcademicoId)
            );
    }

    private MatriculaRespuestaDto mapearRespuesta(Matricula matricula) {
        MatriculaRespuestaDto dto = new MatriculaRespuestaDto();
        dto.setId(matricula.getId());
        dto.setAlumnoId(matricula.getAlumno().getId());
        dto.setCodigoAlumno(matricula.getAlumno().getCodigo());
        dto.setAlumnoNombreCompleto(matricula.getAlumno().getNombres() + " " + matricula.getAlumno().getApellidos());
        dto.setGradoId(matricula.getGrado().getId());
        dto.setGrado(matricula.getGrado().getNombre());
        dto.setNivel(matricula.getGrado().getNivel().getNombre());
        dto.setSeccionId(matricula.getSeccion().getId());
        dto.setSeccion(matricula.getSeccion().getNombre());
        dto.setPeriodoAcademicoId(matricula.getPeriodoAcademico().getId());
        dto.setPeriodoAcademico(matricula.getPeriodoAcademico().getNombre());
        dto.setAnioAcademico(matricula.getPeriodoAcademico().getAnio());
        dto.setFechaMatricula(matricula.getFechaMatricula());
        return dto;
    }
}
