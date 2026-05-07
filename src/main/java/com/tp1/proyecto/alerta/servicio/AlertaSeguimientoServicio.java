package com.tp1.proyecto.alerta.servicio;

import com.tp1.proyecto.alerta.dto.AlertaRespuestaDto;
import com.tp1.proyecto.alerta.dto.RecomendacionRespuestaDto;
import java.util.List;

public interface AlertaSeguimientoServicio {

    List<AlertaRespuestaDto> listarAlertas(Long bimestreId, Long seccionId);

    List<RecomendacionRespuestaDto> listarRecomendaciones(Long bimestreId, Long seccionId);

    AlertaRespuestaDto marcarAtendida(Long alertaId);
}
