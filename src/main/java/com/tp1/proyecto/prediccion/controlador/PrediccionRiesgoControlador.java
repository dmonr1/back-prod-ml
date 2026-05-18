package com.tp1.proyecto.prediccion.controlador;

import com.tp1.proyecto.prediccion.dto.PrediccionRiesgoRespuestaDto;
import com.tp1.proyecto.prediccion.dto.ResumenPrediccionDto;
import com.tp1.proyecto.prediccion.servicio.PrediccionRiesgoServicio;
import java.util.List;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/predicciones")
@PreAuthorize("hasAnyRole('ADMIN','DOCENTE','DOCENTE_TUTOR')")
public class PrediccionRiesgoControlador {

    private final PrediccionRiesgoServicio prediccionRiesgoServicio;

    public PrediccionRiesgoControlador(PrediccionRiesgoServicio prediccionRiesgoServicio) {
        this.prediccionRiesgoServicio = prediccionRiesgoServicio;
    }

    @GetMapping("/globales")
    public List<PrediccionRiesgoRespuestaDto> listarPrediccionesGlobales(
        @RequestParam Long periodoEvaluacionId,
        @RequestParam Long seccionId
    ) {
        return prediccionRiesgoServicio.listarPrediccionesGlobales(periodoEvaluacionId, seccionId);
    }

    @GetMapping("/cursos")
    public List<PrediccionRiesgoRespuestaDto> listarPrediccionesCurso(
        @RequestParam Long periodoEvaluacionId,
        @RequestParam Long seccionId
    ) {
        return prediccionRiesgoServicio.listarPrediccionesCurso(periodoEvaluacionId, seccionId);
    }

    @GetMapping("/alumno/{alumnoId}")
    public List<PrediccionRiesgoRespuestaDto> listarPrediccionesPorAlumno(@PathVariable Long alumnoId) {
        return prediccionRiesgoServicio.listarPrediccionesPorAlumno(alumnoId);
    }

    @GetMapping("/resumen")
    public ResumenPrediccionDto obtenerResumen(
        @RequestParam Long periodoEvaluacionId,
        @RequestParam Long seccionId
    ) {
        return prediccionRiesgoServicio.obtenerResumenPredicciones(periodoEvaluacionId, seccionId);
    }

    @PostMapping("/recalcular")
    @PreAuthorize("hasAnyRole('ADMIN','DOCENTE_TUTOR')")
    public Map<String, Object> recalcularPredicciones(
        @RequestParam Long periodoEvaluacionId,
        @RequestParam Long seccionId
    ) {
        int procesadas = prediccionRiesgoServicio.recalcularPrediccionesPorSeccionYPeriodo(
            seccionId,
            periodoEvaluacionId
        );

        return Map.of(
            "mensaje", "Predicciones recalculadas con el modelo v2-fracaso.",
            "periodoEvaluacionId", periodoEvaluacionId,
            "seccionId", seccionId,
            "matriculasProcesadas", procesadas,
            "modeloVersion", "v2-fracaso"
        );
    }
}
