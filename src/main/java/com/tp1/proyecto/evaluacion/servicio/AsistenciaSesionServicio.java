package com.tp1.proyecto.evaluacion.servicio;

import com.tp1.proyecto.evaluacion.dto.AsistenciaSesionRespuestaDto;
import com.tp1.proyecto.evaluacion.dto.EstadoAsistenciaSesionResumenDto;
import com.tp1.proyecto.evaluacion.dto.RegistroAsistenciaSesionSolicitudDto;
import com.tp1.proyecto.seguridad.servicio.UsuarioAutenticado;
import java.time.LocalDate;
import java.util.Collection;
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

    List<AsistenciaSesionRespuestaDto> editar(
        RegistroAsistenciaSesionSolicitudDto solicitud,
        UsuarioAutenticado actor
    );

    List<EstadoAsistenciaSesionResumenDto> resumir(
        Collection<Long> asignacionIds,
        LocalDate desde,
        LocalDate hasta,
        UsuarioAutenticado actor
    );
}
