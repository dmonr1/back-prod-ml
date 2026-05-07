package com.tp1.proyecto.academico.controlador;

import com.tp1.proyecto.academico.dto.CursoRespuestaDto;
import com.tp1.proyecto.academico.dto.CursoSolicitudDto;
import com.tp1.proyecto.academico.servicio.CursoServicio;
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
@RequestMapping("/api/cursos")
public class CursoControlador {

    private final CursoServicio cursoServicio;

    public CursoControlador(CursoServicio cursoServicio) {
        this.cursoServicio = cursoServicio;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','DOCENTE','DOCENTE_TUTOR')")
    public List<CursoRespuestaDto> listar() {
        return cursoServicio.listar();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DOCENTE','DOCENTE_TUTOR')")
    public CursoRespuestaDto obtenerPorId(@PathVariable Long id) {
        return cursoServicio.obtenerPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public CursoRespuestaDto crear(@Valid @RequestBody CursoSolicitudDto solicitud) {
        return cursoServicio.crear(solicitud);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CursoRespuestaDto actualizar(@PathVariable Long id, @Valid @RequestBody CursoSolicitudDto solicitud) {
        return cursoServicio.actualizar(id, solicitud);
    }
}
