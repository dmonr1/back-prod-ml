package com.tp1.proyecto.alerta.servicio;

import com.tp1.proyecto.alerta.dto.HallazgoDataMiningRespuestaDto;
import java.util.List;

public interface HallazgoDataMiningServicio {

    List<HallazgoDataMiningRespuestaDto> listarHallazgos(Long periodoEvaluacionId, Long seccionId);

    List<HallazgoDataMiningRespuestaDto> generarHallazgos(Long periodoEvaluacionId, Long seccionId);
}
