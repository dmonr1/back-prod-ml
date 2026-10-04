package com.tp1.proyecto.auditoria.controlador;

import com.tp1.proyecto.auditoria.dto.EstadisticasAuditoriaDto;
import com.tp1.proyecto.auditoria.dto.RegistroAuditoriaDto;
import com.tp1.proyecto.auditoria.servicio.AuditoriaServicio;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auditoria")
@PreAuthorize("hasAnyRole('ADMIN','DIRECTOR_ACADEMICO')")
public class AuditoriaControlador {

    private final AuditoriaServicio auditoriaServicio;

    public AuditoriaControlador(AuditoriaServicio auditoriaServicio) {
        this.auditoriaServicio = auditoriaServicio;
    }

    @GetMapping("/logs")
    public List<RegistroAuditoriaDto> listarLogs(
        @RequestParam(required = false) String modulo,
        @RequestParam(required = false) String nivelCriticidad,
        @RequestParam(required = false) String busqueda,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
        @RequestParam(required = false, defaultValue = "100") Integer limite
    ) {
        return auditoriaServicio.listarLogs(modulo, nivelCriticidad, busqueda, fechaInicio, fechaFin, limite);
    }

    @GetMapping("/estadisticas")
    public EstadisticasAuditoriaDto obtenerEstadisticas() {
        return auditoriaServicio.obtenerEstadisticas();
    }
}
