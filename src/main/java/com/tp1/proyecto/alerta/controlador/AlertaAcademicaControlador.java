package com.tp1.proyecto.alerta.controlador;

import com.tp1.proyecto.alerta.dto.AlertaAcademicaRespuestaDto;
import com.tp1.proyecto.alerta.servicio.AlertaAcademicaServicio;
import com.tp1.proyecto.seguridad.servicio.UsuarioAutenticado;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/alertas-academicas")
@PreAuthorize("hasAnyRole('ADMIN','DIRECTOR_ACADEMICO','DOCENTE','DOCENTE_TUTOR')")
public class AlertaAcademicaControlador {
    private final AlertaAcademicaServicio servicio;

    public AlertaAcademicaControlador(AlertaAcademicaServicio servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<AlertaAcademicaRespuestaDto> listar(
        @RequestParam(defaultValue = "PENDIENTE") String estado,
        @AuthenticationPrincipal UsuarioAutenticado actor
    ) {
        return servicio.listar(estado, actor);
    }

    @GetMapping("/contador")
    public long contarPendientes(@AuthenticationPrincipal UsuarioAutenticado actor) {
        return servicio.contarPendientes(actor);
    }
}
