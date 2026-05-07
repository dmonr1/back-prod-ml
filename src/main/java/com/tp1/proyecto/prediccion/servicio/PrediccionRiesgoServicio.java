package com.tp1.proyecto.prediccion.servicio;

import com.tp1.proyecto.notas.entidad.CargaExcel;
import com.tp1.proyecto.prediccion.dto.PrediccionRiesgoRespuestaDto;
import com.tp1.proyecto.prediccion.dto.ResumenPrediccionDto;
import java.util.List;

public interface PrediccionRiesgoServicio {

    void generarPrediccionesGlobales(CargaExcel cargaExcel);

    void generarPrediccionGlobalPorMatricula(Long matriculaId, Long bimestreId);

    List<PrediccionRiesgoRespuestaDto> listarPrediccionesGlobales(Long bimestreId, Long seccionId);

    List<PrediccionRiesgoRespuestaDto> listarPrediccionesCurso(Long bimestreId, Long seccionId);

    List<PrediccionRiesgoRespuestaDto> listarPrediccionesPorAlumno(Long alumnoId);

    ResumenPrediccionDto obtenerResumenPredicciones(Long bimestreId, Long seccionId);
}
