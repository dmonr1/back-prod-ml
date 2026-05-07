package com.tp1.proyecto.academico.servicio.impl;

import com.tp1.proyecto.academico.dto.AlumnoSeccionRespuestaDto;
import com.tp1.proyecto.academico.dto.AsignacionDocenteRespuestaDto;
import com.tp1.proyecto.academico.entidad.DocenteCursoSeccion;
import com.tp1.proyecto.academico.entidad.Matricula;
import com.tp1.proyecto.academico.repositorio.DocenteCursoSeccionRepositorio;
import com.tp1.proyecto.academico.repositorio.MatriculaRepositorio;
import com.tp1.proyecto.academico.servicio.AsignacionAcademicaServicio;
import com.tp1.proyecto.docente.repositorio.DocenteRepositorio;
import com.tp1.proyecto.excepcion.RecursoNoEncontradoException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AsignacionAcademicaServicioImpl implements AsignacionAcademicaServicio {

    private final DocenteRepositorio docenteRepositorio;
    private final DocenteCursoSeccionRepositorio docenteCursoSeccionRepositorio;
    private final MatriculaRepositorio matriculaRepositorio;

    public AsignacionAcademicaServicioImpl(
        DocenteRepositorio docenteRepositorio,
        DocenteCursoSeccionRepositorio docenteCursoSeccionRepositorio,
        MatriculaRepositorio matriculaRepositorio
    ) {
        this.docenteRepositorio = docenteRepositorio;
        this.docenteCursoSeccionRepositorio = docenteCursoSeccionRepositorio;
        this.matriculaRepositorio = matriculaRepositorio;
    }

    @Override
    public List<AsignacionDocenteRespuestaDto> listarAsignacionesDocente(Long docenteId, Long periodoAcademicoId) {
        docenteRepositorio.findById(docenteId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Docente no encontrado con id: " + docenteId));

        return docenteCursoSeccionRepositorio.findByDocenteIdAndPeriodoAcademicoId(docenteId, periodoAcademicoId)
            .stream()
            .map(this::mapearAsignacion)
            .toList();
    }

    @Override
    public List<AlumnoSeccionRespuestaDto> listarAlumnosPorSeccion(Long seccionId, Long periodoAcademicoId) {
        return matriculaRepositorio.findBySeccionIdAndPeriodoAcademicoId(seccionId, periodoAcademicoId)
            .stream()
            .map(this::mapearAlumno)
            .toList();
    }

    private AsignacionDocenteRespuestaDto mapearAsignacion(DocenteCursoSeccion asignacion) {
        AsignacionDocenteRespuestaDto dto = new AsignacionDocenteRespuestaDto();
        dto.setId(asignacion.getId());
        dto.setDocenteId(asignacion.getDocente().getId());
        dto.setCursoId(asignacion.getCurso().getId());
        dto.setCurso(asignacion.getCurso().getNombre());
        dto.setSeccionId(asignacion.getSeccion().getId());
        dto.setSeccion(asignacion.getSeccion().getNombre());
        dto.setGrado(asignacion.getSeccion().getGrado().getNombre());
        dto.setNivel(asignacion.getSeccion().getGrado().getNivel().getNombre());
        dto.setPeriodoAcademicoId(asignacion.getPeriodoAcademico().getId());
        dto.setPeriodoAcademico(asignacion.getPeriodoAcademico().getNombre());
        dto.setAnioAcademico(asignacion.getPeriodoAcademico().getAnio());
        return dto;
    }

    private AlumnoSeccionRespuestaDto mapearAlumno(Matricula matricula) {
        AlumnoSeccionRespuestaDto dto = new AlumnoSeccionRespuestaDto();
        dto.setMatriculaId(matricula.getId());
        dto.setAlumnoId(matricula.getAlumno().getId());
        dto.setCodigoAlumno(matricula.getAlumno().getCodigo());
        dto.setAlumnoNombreCompleto(matricula.getAlumno().getNombres() + " " + matricula.getAlumno().getApellidos());
        dto.setNivel(matricula.getGrado().getNivel().getNombre());
        dto.setGrado(matricula.getGrado().getNombre());
        dto.setSeccion(matricula.getSeccion().getNombre());
        dto.setPeriodoAcademicoId(matricula.getPeriodoAcademico().getId());
        dto.setAnioAcademico(matricula.getPeriodoAcademico().getAnio());
        return dto;
    }
}
