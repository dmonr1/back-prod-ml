package com.tp1.proyecto.evaluacion.servicio;

import com.tp1.proyecto.evaluacion.dto.TipoEvaluacionRespuestaDto;
import com.tp1.proyecto.evaluacion.dto.TipoEvaluacionSolicitudDto;
import java.util.List;

public interface TipoEvaluacionServicio {

    TipoEvaluacionRespuestaDto crear(TipoEvaluacionSolicitudDto solicitud);

    List<TipoEvaluacionRespuestaDto> listar();
}
