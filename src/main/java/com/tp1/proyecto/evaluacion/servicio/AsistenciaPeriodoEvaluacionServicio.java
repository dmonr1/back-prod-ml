package com.tp1.proyecto.evaluacion.servicio;

import com.tp1.proyecto.evaluacion.dto.AsistenciaPeriodoEvaluacionRespuestaDto;
import com.tp1.proyecto.evaluacion.dto.ConfiguracionAsistenciaPeriodoRespuestaDto;
import com.tp1.proyecto.evaluacion.dto.ConfiguracionAsistenciaPeriodoSolicitudDto;
import com.tp1.proyecto.evaluacion.dto.RegistroAsistenciasPeriodoEvaluacionSolicitudDto;
import java.util.List;

public interface AsistenciaPeriodoEvaluacionServicio {

    ConfiguracionAsistenciaPeriodoRespuestaDto guardarConfiguracion(
        Long periodoEvaluacionId,
        ConfiguracionAsistenciaPeriodoSolicitudDto solicitud
    );

    ConfiguracionAsistenciaPeriodoRespuestaDto obtenerConfiguracion(
        Long docenteCursoSeccionId,
        Long periodoEvaluacionId
    );

    List<AsistenciaPeriodoEvaluacionRespuestaDto> registrarAsistencias(Long periodoEvaluacionId, RegistroAsistenciasPeriodoEvaluacionSolicitudDto solicitud);

    List<AsistenciaPeriodoEvaluacionRespuestaDto> listarPorSeccionYPeriodoEvaluacion(Long seccionId, Long periodoAcademicoId, Long periodoEvaluacionId);
}
