package com.tp1.proyecto.academico.controlador;

import com.tp1.proyecto.academico.dto.GradoRespuestaDto;
import com.tp1.proyecto.academico.dto.GradoSolicitudDto;
import com.tp1.proyecto.academico.servicio.GradoServicio;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/grados")
public class GradoControlador {

    private final GradoServicio gradoServicio;

    public GradoControlador(GradoServicio gradoServicio) {
        this.gradoServicio = gradoServicio;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','DOCENTE','DOCENTE_TUTOR')")
    public List<GradoRespuestaDto> listar() {
        return gradoServicio.listar();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public GradoRespuestaDto crear(@Valid @RequestBody GradoSolicitudDto solicitud) {
        return gradoServicio.crear(solicitud);
    }
}
