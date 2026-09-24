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
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/predicciones")
@PreAuthorize("hasAnyRole('ADMIN','DOCENTE','DOCENTE_TUTOR')")
public class PrediccionRiesgoControlador {

    private final PrediccionRiesgoServicio prediccionRiesgoServicio;
    private final com.tp1.proyecto.prediccion.servicio.CorteSeguimientoPrediccionServicio cortePrediccionServicio;

    public PrediccionRiesgoControlador(PrediccionRiesgoServicio prediccionRiesgoServicio,
        com.tp1.proyecto.prediccion.servicio.CorteSeguimientoPrediccionServicio cortePrediccionServicio) {
        this.prediccionRiesgoServicio = prediccionRiesgoServicio;
        this.cortePrediccionServicio = cortePrediccionServicio;
    }

    @GetMapping("/globales")
    public List<PrediccionRiesgoRespuestaDto> listarPrediccionesGlobales(
        @RequestParam(required = false) Long periodoEvaluacionId,
        @RequestParam(required = false) Long corteSeguimientoId,
        @RequestParam Long seccionId
    ) {
        if (corteSeguimientoId != null) return cortePrediccionServicio.listarGlobales(corteSeguimientoId, seccionId);
        if (periodoEvaluacionId == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Indica el período o el corte de seguimiento.");
        return prediccionRiesgoServicio.listarPrediccionesGlobales(periodoEvaluacionId, seccionId);
    }

    @GetMapping("/cursos")
    public List<PrediccionRiesgoRespuestaDto> listarPrediccionesCurso(
        @RequestParam(required = false) Long periodoEvaluacionId,
        @RequestParam(required = false) Long corteSeguimientoId,
        @RequestParam Long seccionId
    ) {
        if (corteSeguimientoId != null) return cortePrediccionServicio.listarCursos(corteSeguimientoId, seccionId);
        if (periodoEvaluacionId == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Indica el período o el corte de seguimiento.");
        return prediccionRiesgoServicio.listarPrediccionesCurso(periodoEvaluacionId, seccionId);
    }

    @GetMapping("/alumno/{alumnoId}")
    public List<PrediccionRiesgoRespuestaDto> listarPrediccionesPorAlumno(@PathVariable Long alumnoId) {
        return prediccionRiesgoServicio.listarPrediccionesPorAlumno(alumnoId);
    }

    @GetMapping("/resumen")
    public ResumenPrediccionDto obtenerResumen(
        @RequestParam(required = false) Long periodoEvaluacionId,
        @RequestParam(required = false) Long corteSeguimientoId,
        @RequestParam Long seccionId
    ) {
        if (corteSeguimientoId != null) return cortePrediccionServicio.resumen(corteSeguimientoId, seccionId);
        if (periodoEvaluacionId == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Indica el período o el corte de seguimiento.");
        return prediccionRiesgoServicio.obtenerResumenPredicciones(periodoEvaluacionId, seccionId);
    }

    @PostMapping("/recalcular")
    @PreAuthorize("hasAnyRole('ADMIN','DOCENTE_TUTOR')")
    public Map<String, Object> recalcularPredicciones(
        @RequestParam(required = false) Long periodoEvaluacionId,
        @RequestParam(required = false) Long corteSeguimientoId,
        @RequestParam Long seccionId
    ) {
        if (corteSeguimientoId != null) {
            int procesadas = cortePrediccionServicio.recalcular(corteSeguimientoId, seccionId);
            return Map.of("mensaje", "Predicciones recalculadas para el corte semanal.", "corteSeguimientoId", corteSeguimientoId,
                "seccionId", seccionId, "matriculasProcesadas", procesadas, "modeloVersion", "v4-corte-temprano");
        }
        if (periodoEvaluacionId == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Indica el período o el corte de seguimiento.");
        int procesadas = prediccionRiesgoServicio.recalcularPrediccionesPorSeccionYPeriodo(
            seccionId,
            periodoEvaluacionId
        );

        return Map.of(
            "mensaje", "Predicciones recalculadas con el modelo v3-fracaso.",
            "periodoEvaluacionId", periodoEvaluacionId,
            "seccionId", seccionId,
            "matriculasProcesadas", procesadas,
            "modeloVersion", "v3-fracaso"
        );
    }
}

