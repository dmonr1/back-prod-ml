package com.tp1.proyecto.evaluacion.servicio;

import com.tp1.proyecto.evaluacion.dto.DetalleNotaEvaluacionRespuestaDto;
import com.tp1.proyecto.evaluacion.dto.EvaluacionRespuestaDto;
import com.tp1.proyecto.evaluacion.dto.EvaluacionSolicitudDto;
import com.tp1.proyecto.evaluacion.dto.RegistroNotasEvaluacionSolicitudDto;
import java.util.List;

public interface EvaluacionServicio {

    EvaluacionRespuestaDto crear(EvaluacionSolicitudDto solicitud);

    List<EvaluacionRespuestaDto> listarPorAsignacionYBimestre(Long docenteCursoSeccionId, Long bimestreId);

    List<DetalleNotaEvaluacionRespuestaDto> registrarNotas(Long evaluacionId, RegistroNotasEvaluacionSolicitudDto solicitud);

    List<DetalleNotaEvaluacionRespuestaDto> listarNotasPorEvaluacion(Long evaluacionId);
}
