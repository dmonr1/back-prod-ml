package com.tp1.proyecto.evaluacion.servicio;

import com.tp1.proyecto.evaluacion.dto.ConfiguracionEvaluacionRespuestaDto;
import com.tp1.proyecto.evaluacion.dto.ConfiguracionEvaluacionSolicitudDto;
import java.util.List;

public interface ConfiguracionEvaluacionServicio {

    ConfiguracionEvaluacionRespuestaDto crear(ConfiguracionEvaluacionSolicitudDto solicitud);

    List<ConfiguracionEvaluacionRespuestaDto> listarPorBimestreYCurso(Long bimestreId, Long cursoId);
}
