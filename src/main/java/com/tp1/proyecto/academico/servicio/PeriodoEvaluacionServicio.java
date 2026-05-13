package com.tp1.proyecto.academico.servicio;

import com.tp1.proyecto.academico.dto.PeriodoEvaluacionRespuestaDto;
import com.tp1.proyecto.academico.dto.PeriodoEvaluacionSolicitudDto;
import java.util.List;

public interface PeriodoEvaluacionServicio {

    List<PeriodoEvaluacionRespuestaDto> listar();

    PeriodoEvaluacionRespuestaDto crear(PeriodoEvaluacionSolicitudDto solicitud);
}
