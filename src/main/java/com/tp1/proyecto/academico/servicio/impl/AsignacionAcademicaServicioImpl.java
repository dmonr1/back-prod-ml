package com.tp1.proyecto.academico.servicio.impl;

import com.tp1.proyecto.academico.dto.AlumnoSeccionRespuestaDto;
import com.tp1.proyecto.academico.dto.AsignacionDocenteSolicitudDto;
import com.tp1.proyecto.academico.dto.AsignacionDocenteRespuestaDto;
import com.tp1.proyecto.academico.dto.TutoriaRespuestaDto;
import com.tp1.proyecto.academico.dto.TutoriaSolicitudDto;
import com.tp1.proyecto.academico.entidad.Curso;
import com.tp1.proyecto.academico.entidad.DocenteCursoSeccion;
import com.tp1.proyecto.academico.entidad.Matricula;
import com.tp1.proyecto.academico.entidad.PeriodoAcademico;
import com.tp1.proyecto.academico.entidad.Seccion;
import com.tp1.proyecto.academico.entidad.Tutoria;
import com.tp1.proyecto.academico.repositorio.CursoRepositorio;
import com.tp1.proyecto.academico.repositorio.DocenteCursoSeccionRepositorio;
import com.tp1.proyecto.academico.repositorio.MatriculaRepositorio;
import com.tp1.proyecto.academico.repositorio.PeriodoAcademicoRepositorio;
import com.tp1.proyecto.academico.repositorio.SeccionRepositorio;
import com.tp1.proyecto.academico.repositorio.TutoriaRepositorio;
import com.tp1.proyecto.academico.servicio.AsignacionAcademicaServicio;
import com.tp1.proyecto.docente.entidad.Docente;
import com.tp1.proyecto.docente.repositorio.DocenteRepositorio;
import com.tp1.proyecto.excepcion.RecursoNoEncontradoException;
import com.tp1.proyecto.excepcion.ReglaNegocioException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AsignacionAcademicaServicioImpl implements AsignacionAcademicaServicio {

    private final DocenteRepositorio docenteRepositorio;
    private final CursoRepositorio cursoRepositorio;
    private final SeccionRepositorio seccionRepositorio;
    private final PeriodoAcademicoRepositorio periodoAcademicoRepositorio;
    private final DocenteCursoSeccionRepositorio docenteCursoSeccionRepositorio;
    private final TutoriaRepositorio tutoriaRepositorio;
    private final MatriculaRepositorio matriculaRepositorio;

    public AsignacionAcademicaServicioImpl(
        DocenteRepositorio docenteRepositorio,
        CursoRepositorio cursoRepositorio,
        SeccionRepositorio seccionRepositorio,
        PeriodoAcademicoRepositorio periodoAcademicoRepositorio,
        DocenteCursoSeccionRepositorio docenteCursoSeccionRepositorio,
        TutoriaRepositorio tutoriaRepositorio,
        MatriculaRepositorio matriculaRepositorio
    ) {
        this.docenteRepositorio = docenteRepositorio;
        this.cursoRepositorio = cursoRepositorio;
        this.seccionRepositorio = seccionRepositorio;
        this.periodoAcademicoRepositorio = periodoAcademicoRepositorio;
        this.docenteCursoSeccionRepositorio = docenteCursoSeccionRepositorio;
        this.tutoriaRepositorio = tutoriaRepositorio;
        this.matriculaRepositorio = matriculaRepositorio;
    }

    @Override
    public AsignacionDocenteRespuestaDto crearAsignacionDocente(AsignacionDocenteSolicitudDto solicitud) {
        Docente docente = obtenerDocente(solicitud.getDocenteId());
        Curso curso = cursoRepositorio.findById(solicitud.getCursoId())
            .orElseThrow(() -> new RecursoNoEncontradoException("Curso no encontrado con id: " + solicitud.getCursoId()));
        Seccion seccion = seccionRepositorio.findById(solicitud.getSeccionId())
            .orElseThrow(() -> new RecursoNoEncontradoException("Seccion no encontrada con id: " + solicitud.getSeccionId()));
        PeriodoAcademico periodoAcademico = obtenerPeriodo(solicitud.getPeriodoAcademicoId());

        if (docenteCursoSeccionRepositorio.existsByDocenteIdAndCursoIdAndSeccionIdAndPeriodoAcademicoId(
            docente.getId(),
            curso.getId(),
            seccion.getId(),
            periodoAcademico.getId()
        )) {
            throw new ReglaNegocioException("La asignacion docente ya existe para este curso, seccion y periodo.");
        }

        DocenteCursoSeccion asignacion = new DocenteCursoSeccion();
        asignacion.setDocente(docente);
        asignacion.setCurso(curso);
        asignacion.setSeccion(seccion);
        asignacion.setPeriodoAcademico(periodoAcademico);

        return mapearAsignacion(docenteCursoSeccionRepositorio.save(asignacion));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AsignacionDocenteRespuestaDto> listarAsignacionesPorPeriodo(Long periodoAcademicoId) {
        obtenerPeriodo(periodoAcademicoId);

        return docenteCursoSeccionRepositorio.findByPeriodoAcademicoId(periodoAcademicoId)
            .stream()
            .map(this::mapearAsignacion)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AsignacionDocenteRespuestaDto> listarAsignacionesDocente(Long docenteId, Long periodoAcademicoId) {
        obtenerDocente(docenteId);

        return docenteCursoSeccionRepositorio.findByDocenteIdAndPeriodoAcademicoId(docenteId, periodoAcademicoId)
            .stream()
            .map(this::mapearAsignacion)
            .toList();
    }

    @Override
    public TutoriaRespuestaDto crearTutoria(TutoriaSolicitudDto solicitud) {
        Docente docente = obtenerDocente(solicitud.getDocenteId());
        Seccion seccion = seccionRepositorio.findById(solicitud.getSeccionId())
            .orElseThrow(() -> new RecursoNoEncontradoException("Seccion no encontrada con id: " + solicitud.getSeccionId()));
        PeriodoAcademico periodoAcademico = obtenerPeriodo(solicitud.getPeriodoAcademicoId());

        if (tutoriaRepositorio.existsByDocenteIdAndPeriodoAcademicoId(docente.getId(), periodoAcademico.getId())) {
            throw new ReglaNegocioException("El docente ya tiene una tutoria registrada en este periodo.");
        }

        if (tutoriaRepositorio.existsBySeccionIdAndPeriodoAcademicoId(seccion.getId(), periodoAcademico.getId())) {
            throw new ReglaNegocioException("La seccion ya tiene una tutoria asignada en este periodo.");
        }

        Tutoria tutoria = new Tutoria();
        tutoria.setDocente(docente);
        tutoria.setSeccion(seccion);
        tutoria.setPeriodoAcademico(periodoAcademico);

        return mapearTutoria(tutoriaRepositorio.save(tutoria));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TutoriaRespuestaDto> listarTutoriasPorPeriodo(Long periodoAcademicoId) {
        obtenerPeriodo(periodoAcademicoId);

        return tutoriaRepositorio.findByPeriodoAcademicoId(periodoAcademicoId)
            .stream()
            .map(this::mapearTutoria)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
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
        dto.setDocenteNombreCompleto(
            asignacion.getDocente().getNombres() + " " + asignacion.getDocente().getApellidos()
        );
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

    private TutoriaRespuestaDto mapearTutoria(Tutoria tutoria) {
        TutoriaRespuestaDto dto = new TutoriaRespuestaDto();
        dto.setId(tutoria.getId());
        dto.setDocenteId(tutoria.getDocente().getId());
        dto.setDocenteNombreCompleto(tutoria.getDocente().getNombres() + " " + tutoria.getDocente().getApellidos());
        dto.setSeccionId(tutoria.getSeccion().getId());
        dto.setSeccion(tutoria.getSeccion().getNombre());
        dto.setGrado(tutoria.getSeccion().getGrado().getNombre());
        dto.setNivel(tutoria.getSeccion().getGrado().getNivel().getNombre());
        dto.setPeriodoAcademicoId(tutoria.getPeriodoAcademico().getId());
        dto.setPeriodoAcademico(tutoria.getPeriodoAcademico().getNombre());
        dto.setAnioAcademico(tutoria.getPeriodoAcademico().getAnio());
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

    private Docente obtenerDocente(Long docenteId) {
        return docenteRepositorio.findById(docenteId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Docente no encontrado con id: " + docenteId));
    }

    private PeriodoAcademico obtenerPeriodo(Long periodoAcademicoId) {
        return periodoAcademicoRepositorio.findById(periodoAcademicoId)
            .orElseThrow(() ->
                new RecursoNoEncontradoException("Periodo academico no encontrado con id: " + periodoAcademicoId)
            );
    }
}
