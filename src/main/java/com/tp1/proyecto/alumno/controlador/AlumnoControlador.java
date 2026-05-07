package com.tp1.proyecto.alumno.controlador;

import com.tp1.proyecto.alumno.dto.AlumnoRespuestaDto;
import com.tp1.proyecto.alumno.dto.AlumnoSolicitudDto;
import com.tp1.proyecto.alumno.servicio.AlumnoServicio;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/alumnos")
public class AlumnoControlador {

    private final AlumnoServicio alumnoServicio;

    public AlumnoControlador(AlumnoServicio alumnoServicio) {
        this.alumnoServicio = alumnoServicio;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','DOCENTE','DOCENTE_TUTOR')")
    public List<AlumnoRespuestaDto> listar() {
        return alumnoServicio.listar();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DOCENTE','DOCENTE_TUTOR')")
    public AlumnoRespuestaDto obtenerPorId(@PathVariable Long id) {
        return alumnoServicio.obtenerPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public AlumnoRespuestaDto crear(@Valid @RequestBody AlumnoSolicitudDto solicitud) {
        return alumnoServicio.crear(solicitud);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public AlumnoRespuestaDto actualizar(@PathVariable Long id, @Valid @RequestBody AlumnoSolicitudDto solicitud) {
        return alumnoServicio.actualizar(id, solicitud);
    }
}
