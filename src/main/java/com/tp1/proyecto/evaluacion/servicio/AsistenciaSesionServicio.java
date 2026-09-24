package com.tp1.proyecto.evaluacion.servicio;

import com.tp1.proyecto.evaluacion.dto.AsistenciaSesionRespuestaDto;
import com.tp1.proyecto.evaluacion.dto.RegistroAsistenciaSesionSolicitudDto;
import com.tp1.proyecto.seguridad.servicio.UsuarioAutenticado;
import java.time.LocalDate;
import java.util.List;

public interface AsistenciaSesionServicio {

    List<AsistenciaSesionRespuestaDto> listar(
        Long asignacionId,
        Long periodoEvaluacionId,
        LocalDate fecha,
        Long horarioSemanalId,
        UsuarioAutenticado actor
    );

    List<AsistenciaSesionRespuestaDto> registrar(
        RegistroAsistenciaSesionSolicitudDto solicitud,
        UsuarioAutenticado actor
    );
}
