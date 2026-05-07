package com.tp1.proyecto.academico.controlador;

import com.tp1.proyecto.academico.dto.AlumnoSeccionRespuestaDto;
import com.tp1.proyecto.academico.dto.AsignacionDocenteRespuestaDto;
import com.tp1.proyecto.academico.servicio.AsignacionAcademicaServicio;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/docentes/{docenteId}/asignaciones")
    public List<AsignacionDocenteRespuestaDto> listarAsignacionesDocente(
        @PathVariable Long docenteId,
        @RequestParam Long periodoAcademicoId
    ) {
        return asignacionAcademicaServicio.listarAsignacionesDocente(docenteId, periodoAcademicoId);
    }

    @GetMapping("/secciones/{seccionId}/alumnos")
    public List<AlumnoSeccionRespuestaDto> listarAlumnosPorSeccion(
        @PathVariable Long seccionId,
        @RequestParam Long periodoAcademicoId
    ) {
        return asignacionAcademicaServicio.listarAlumnosPorSeccion(seccionId, periodoAcademicoId);
    }
}
