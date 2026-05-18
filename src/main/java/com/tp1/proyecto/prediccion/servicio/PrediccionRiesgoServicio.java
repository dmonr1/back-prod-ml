package com.tp1.proyecto.prediccion.servicio;

import com.tp1.proyecto.notas.entidad.CargaExcel;
import com.tp1.proyecto.prediccion.dto.PrediccionRiesgoRespuestaDto;
import com.tp1.proyecto.prediccion.dto.ResumenPrediccionDto;
import java.util.List;

public interface PrediccionRiesgoServicio {

    void generarPrediccionesGlobales(CargaExcel cargaExcel);

    void generarPrediccionGlobalPorMatricula(Long matriculaId, Long periodoEvaluacionId);

    int recalcularPrediccionesPorSeccionYPeriodo(Long seccionId, Long periodoEvaluacionId);

    List<PrediccionRiesgoRespuestaDto> listarPrediccionesGlobales(Long periodoEvaluacionId, Long seccionId);

    List<PrediccionRiesgoRespuestaDto> listarPrediccionesCurso(Long periodoEvaluacionId, Long seccionId);

    List<PrediccionRiesgoRespuestaDto> listarPrediccionesPorAlumno(Long alumnoId);

    ResumenPrediccionDto obtenerResumenPredicciones(Long periodoEvaluacionId, Long seccionId);
}
