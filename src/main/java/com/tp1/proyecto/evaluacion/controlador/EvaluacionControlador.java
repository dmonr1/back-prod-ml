package com.tp1.proyecto.evaluacion.controlador;

import com.tp1.proyecto.evaluacion.dto.DetalleNotaEvaluacionRespuestaDto;
import com.tp1.proyecto.evaluacion.dto.EvaluacionRespuestaDto;
import com.tp1.proyecto.evaluacion.dto.EvaluacionSolicitudDto;
import com.tp1.proyecto.evaluacion.dto.RegistroNotasEvaluacionSolicitudDto;
import com.tp1.proyecto.evaluacion.servicio.EvaluacionServicio;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/evaluaciones")
@PreAuthorize("hasAnyRole('ADMIN','DOCENTE','DOCENTE_TUTOR')")
public class EvaluacionControlador {

    private final EvaluacionServicio evaluacionServicio;

    public EvaluacionControlador(EvaluacionServicio evaluacionServicio) {
        this.evaluacionServicio = evaluacionServicio;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EvaluacionRespuestaDto crear(@Valid @RequestBody EvaluacionSolicitudDto solicitud) {
        return evaluacionServicio.crear(solicitud);
    }

    @GetMapping
    public List<EvaluacionRespuestaDto> listar(
        @RequestParam Long docenteCursoSeccionId,
        @RequestParam Long periodoEvaluacionId
    ) {
        return evaluacionServicio.listarPorAsignacionYPeriodoEvaluacion(docenteCursoSeccionId, periodoEvaluacionId);
    }

    @PostMapping("/{evaluacionId}/notas")
    public List<DetalleNotaEvaluacionRespuestaDto> registrarNotas(
        @PathVariable Long evaluacionId,
        @Valid @RequestBody RegistroNotasEvaluacionSolicitudDto solicitud
    ) {
        return evaluacionServicio.registrarNotas(evaluacionId, solicitud);
    }

    @GetMapping("/{evaluacionId}/notas")
    public List<DetalleNotaEvaluacionRespuestaDto> listarNotas(@PathVariable Long evaluacionId) {
        return evaluacionServicio.listarNotasPorEvaluacion(evaluacionId);
    }
}
