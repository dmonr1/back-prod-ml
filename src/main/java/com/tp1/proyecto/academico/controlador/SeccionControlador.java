package com.tp1.proyecto.academico.controlador;

import com.tp1.proyecto.academico.dto.SeccionPeriodoAnteriorSolicitudDto;
import com.tp1.proyecto.academico.dto.SeccionRespuestaDto;
import com.tp1.proyecto.academico.dto.SeccionSolicitudDto;
import com.tp1.proyecto.academico.servicio.SeccionServicio;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/secciones")
public class SeccionControlador {

    private final SeccionServicio seccionServicio;

    public SeccionControlador(SeccionServicio seccionServicio) {
        this.seccionServicio = seccionServicio;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','DOCENTE','DOCENTE_TUTOR')")
    public List<SeccionRespuestaDto> listar(@RequestParam(required = false) Long periodoAcademicoId) {
        return seccionServicio.listar(periodoAcademicoId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public SeccionRespuestaDto crear(@Valid @RequestBody SeccionSolicitudDto solicitud) {
        return seccionServicio.crear(solicitud);
    }

    @PostMapping("/copiar-periodo-anterior")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public List<SeccionRespuestaDto> copiarPeriodoAnterior(
        @Valid @RequestBody SeccionPeriodoAnteriorSolicitudDto solicitud
    ) {
        return seccionServicio.copiarDesdePeriodoAnterior(solicitud);
    }
}
