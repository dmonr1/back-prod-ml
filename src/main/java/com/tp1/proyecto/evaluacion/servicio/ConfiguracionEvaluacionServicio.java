package com.tp1.proyecto.evaluacion.servicio;

import com.tp1.proyecto.evaluacion.dto.ConfiguracionEvaluacionRespuestaDto;
import com.tp1.proyecto.evaluacion.dto.ConfiguracionEvaluacionSolicitudDto;
import com.tp1.proyecto.evaluacion.dto.ConfiguracionEvaluacionCursoDetalleDto;
import com.tp1.proyecto.evaluacion.dto.ConfiguracionEvaluacionCursoGuardarSolicitudDto;
import com.tp1.proyecto.evaluacion.dto.ConfiguracionEvaluacionCursoResumenDto;
import java.util.List;

public interface ConfiguracionEvaluacionServicio {

    ConfiguracionEvaluacionRespuestaDto crear(ConfiguracionEvaluacionSolicitudDto solicitud);

    List<ConfiguracionEvaluacionRespuestaDto> listarPorPeriodoEvaluacionYCurso(Long periodoEvaluacionId, Long cursoId);

    List<ConfiguracionEvaluacionCursoResumenDto> listarCursosPorPeriodo(Long periodoAcademicoId);

    ConfiguracionEvaluacionCursoDetalleDto obtenerDetalleCurso(Long periodoAcademicoId, Long cursoId);

    ConfiguracionEvaluacionCursoDetalleDto guardarConfiguracionCurso(ConfiguracionEvaluacionCursoGuardarSolicitudDto solicitud);
}
