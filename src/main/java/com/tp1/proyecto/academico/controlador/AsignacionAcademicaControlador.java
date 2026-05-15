package com.tp1.proyecto.academico.controlador;

import com.tp1.proyecto.academico.dto.AlumnoSeccionRespuestaDto;
import com.tp1.proyecto.academico.dto.AsignacionDocenteSolicitudDto;
import com.tp1.proyecto.academico.dto.AsignacionDocenteRespuestaDto;
import com.tp1.proyecto.academico.dto.TutoriaRespuestaDto;
import com.tp1.proyecto.academico.dto.TutoriaResumenAcademicoRespuestaDto;
import com.tp1.proyecto.academico.dto.TutoriaSolicitudDto;
import com.tp1.proyecto.academico.servicio.AsignacionAcademicaServicio;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@PreAuthorize("hasAnyRole('ADMIN','DOCENTE','DOCENTE_TUTOR')")
public class AsignacionAcademicaControlador {

    private final AsignacionAcademicaServicio asignacionAcademicaServicio;

    public AsignacionAcademicaControlador(AsignacionAcademicaServicio asignacionAcademicaServicio) {
        this.asignacionAcademicaServicio = asignacionAcademicaServicio;
    }

    @PostMapping("/asignaciones-docente")
    @PreAuthorize("hasRole('ADMIN')")
    public AsignacionDocenteRespuestaDto crearAsignacionDocente(@RequestBody AsignacionDocenteSolicitudDto solicitud) {
        return asignacionAcademicaServicio.crearAsignacionDocente(solicitud);
    }

    @GetMapping("/asignaciones-docente")
    @PreAuthorize("hasRole('ADMIN')")
    public List<AsignacionDocenteRespuestaDto> listarAsignacionesPorPeriodo(@RequestParam Long periodoAcademicoId) {
        return asignacionAcademicaServicio.listarAsignacionesPorPeriodo(periodoAcademicoId);
    }

    @GetMapping("/docentes/{docenteId}/asignaciones")
    public List<AsignacionDocenteRespuestaDto> listarAsignacionesDocente(
        @PathVariable Long docenteId,
        @RequestParam Long periodoAcademicoId
    ) {
        return asignacionAcademicaServicio.listarAsignacionesDocente(docenteId, periodoAcademicoId);
    }

    @PostMapping("/tutorias")
    @PreAuthorize("hasRole('ADMIN')")
    public TutoriaRespuestaDto crearTutoria(@RequestBody TutoriaSolicitudDto solicitud) {
        return asignacionAcademicaServicio.crearTutoria(solicitud);
    }

    @GetMapping("/tutorias")
    @PreAuthorize("hasRole('ADMIN')")
    public List<TutoriaRespuestaDto> listarTutoriasPorPeriodo(@RequestParam Long periodoAcademicoId) {
        return asignacionAcademicaServicio.listarTutoriasPorPeriodo(periodoAcademicoId);
    }

    @PatchMapping("/tutorias/{tutoriaId}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public TutoriaRespuestaDto actualizarEstadoTutoria(
        @PathVariable Long tutoriaId,
        @RequestParam boolean activo
    ) {
        return asignacionAcademicaServicio.actualizarEstadoTutoria(tutoriaId, activo);
    }

    @GetMapping("/docentes/{docenteId}/tutorias")
    public List<TutoriaRespuestaDto> listarTutoriasDocente(
        @PathVariable Long docenteId,
        @RequestParam Long periodoAcademicoId
    ) {
        return asignacionAcademicaServicio.listarTutoriasDocente(docenteId, periodoAcademicoId);
    }

    @GetMapping("/secciones/{seccionId}/alumnos")
    public List<AlumnoSeccionRespuestaDto> listarAlumnosPorSeccion(
        @PathVariable Long seccionId,
        @RequestParam Long periodoAcademicoId
    ) {
        return asignacionAcademicaServicio.listarAlumnosPorSeccion(seccionId, periodoAcademicoId);
    }

    @GetMapping("/tutorias/{tutoriaId}/resumen-academico")
    public TutoriaResumenAcademicoRespuestaDto obtenerResumenAcademicoTutoria(
        @PathVariable Long tutoriaId,
        @RequestParam Long periodoEvaluacionId
    ) {
        return asignacionAcademicaServicio.obtenerResumenAcademicoTutoria(tutoriaId, periodoEvaluacionId);
    }
}
