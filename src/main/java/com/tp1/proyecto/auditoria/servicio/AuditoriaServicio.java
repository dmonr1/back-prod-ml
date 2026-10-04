package com.tp1.proyecto.auditoria.servicio;

import com.tp1.proyecto.auditoria.dto.EstadisticasAuditoriaDto;
import com.tp1.proyecto.auditoria.dto.RegistroAuditoriaDto;
import java.time.LocalDate;
import java.util.List;

public interface AuditoriaServicio {
    List<RegistroAuditoriaDto> listarLogs(
        String modulo,
        String nivelCriticidad,
        String busqueda,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        Integer limite
    );

    EstadisticasAuditoriaDto obtenerEstadisticas();
}
