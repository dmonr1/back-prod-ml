package com.tp1.proyecto.docente.controlador;

import com.tp1.proyecto.docente.dto.DocenteRegistroSolicitudDto;
import com.tp1.proyecto.docente.dto.DocenteRespuestaDto;
import com.tp1.proyecto.docente.servicio.DocenteServicio;
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
@RequestMapping("/api/docentes")
public class DocenteControlador {

    private final DocenteServicio docenteServicio;

    public DocenteControlador(DocenteServicio docenteServicio) {
        this.docenteServicio = docenteServicio;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','DOCENTE','DOCENTE_TUTOR')")
    public List<DocenteRespuestaDto> listar() {
        return docenteServicio.listar();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public DocenteRespuestaDto crear(@Valid @RequestBody DocenteRegistroSolicitudDto solicitud) {
        return docenteServicio.crear(solicitud);
    }
}
