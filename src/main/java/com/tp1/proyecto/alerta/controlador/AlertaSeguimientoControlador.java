package com.tp1.proyecto.alerta.controlador;

import com.tp1.proyecto.alerta.dto.AlertaRespuestaDto;
import com.tp1.proyecto.alerta.dto.RecomendacionRespuestaDto;
import com.tp1.proyecto.alerta.servicio.AlertaSeguimientoServicio;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/alertas")
@PreAuthorize("hasAnyRole('ADMIN','DOCENTE','DOCENTE_TUTOR')")
public class AlertaSeguimientoControlador {

    private final AlertaSeguimientoServicio alertaSeguimientoServicio;

    public AlertaSeguimientoControlador(AlertaSeguimientoServicio alertaSeguimientoServicio) {
        this.alertaSeguimientoServicio = alertaSeguimientoServicio;
    }

    @GetMapping
    public List<AlertaRespuestaDto> listarAlertas(
        @RequestParam Long bimestreId,
        @RequestParam Long seccionId
    ) {
        return alertaSeguimientoServicio.listarAlertas(bimestreId, seccionId);
    }

    @GetMapping("/recomendaciones")
    public List<RecomendacionRespuestaDto> listarRecomendaciones(
        @RequestParam Long bimestreId,
        @RequestParam Long seccionId
    ) {
        return alertaSeguimientoServicio.listarRecomendaciones(bimestreId, seccionId);
    }

    @PatchMapping("/{alertaId}/atender")
    public AlertaRespuestaDto marcarAtendida(@PathVariable Long alertaId) {
        return alertaSeguimientoServicio.marcarAtendida(alertaId);
    }
}
