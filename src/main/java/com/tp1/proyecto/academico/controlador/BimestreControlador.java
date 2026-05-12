package com.tp1.proyecto.academico.controlador;

import com.tp1.proyecto.academico.dto.BimestreRespuestaDto;
import com.tp1.proyecto.academico.dto.BimestreSolicitudDto;
import com.tp1.proyecto.academico.servicio.BimestreServicio;
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
@RequestMapping("/api/bimestres")
public class BimestreControlador {

    private final BimestreServicio bimestreServicio;

    public BimestreControlador(BimestreServicio bimestreServicio) {
        this.bimestreServicio = bimestreServicio;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','DOCENTE','DOCENTE_TUTOR')")
    public List<BimestreRespuestaDto> listar() {
        return bimestreServicio.listar();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public BimestreRespuestaDto crear(@Valid @RequestBody BimestreSolicitudDto solicitud) {
        return bimestreServicio.crear(solicitud);
    }
}
