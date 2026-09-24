package com.tp1.proyecto.prediccion.servicio;

import com.tp1.proyecto.prediccion.dto.PrediccionRiesgoRespuestaDto;
import com.tp1.proyecto.prediccion.dto.ResumenPrediccionDto;
import java.util.List;

public interface CorteSeguimientoPrediccionServicio {
    int recalcular(Long corteSeguimientoId, Long seccionId);
    int actualizarPorMatricula(Long matriculaId);
    List<PrediccionRiesgoRespuestaDto> listarGlobales(Long corteSeguimientoId, Long seccionId);
    List<PrediccionRiesgoRespuestaDto> listarCursos(Long corteSeguimientoId, Long seccionId);
    ResumenPrediccionDto resumen(Long corteSeguimientoId, Long seccionId);
}
