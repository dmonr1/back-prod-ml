package com.tp1.proyecto.evaluacion.controlador;

import com.tp1.proyecto.evaluacion.dto.AsistenciaPeriodoEvaluacionRespuestaDto;
import com.tp1.proyecto.evaluacion.dto.RegistroAsistenciasPeriodoEvaluacionSolicitudDto;
import com.tp1.proyecto.evaluacion.servicio.AsistenciaPeriodoEvaluacionServicio;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/asistencias-periodo-evaluacion")
@PreAuthorize("hasAnyRole('ADMIN','DOCENTE','DOCENTE_TUTOR')")
public class AsistenciaPeriodoEvaluacionControlador {

    private final AsistenciaPeriodoEvaluacionServicio asistenciaPeriodoEvaluacionServicio;

    public AsistenciaPeriodoEvaluacionControlador(AsistenciaPeriodoEvaluacionServicio asistenciaPeriodoEvaluacionServicio) {
        this.asistenciaPeriodoEvaluacionServicio = asistenciaPeriodoEvaluacionServicio;
    }

    @PostMapping("/{periodoEvaluacionId}")
    public List<AsistenciaPeriodoEvaluacionRespuestaDto> registrar(
        @PathVariable Long periodoEvaluacionId,
        @Valid @RequestBody RegistroAsistenciasPeriodoEvaluacionSolicitudDto solicitud
    ) {
        return asistenciaPeriodoEvaluacionServicio.registrarAsistencias(periodoEvaluacionId, solicitud);
    }

    @GetMapping
    public List<AsistenciaPeriodoEvaluacionRespuestaDto> listar(
        @RequestParam Long seccionId,
        @RequestParam Long periodoAcademicoId,
        @RequestParam Long periodoEvaluacionId
    ) {
        return asistenciaPeriodoEvaluacionServicio.listarPorSeccionYPeriodoEvaluacion(seccionId, periodoAcademicoId, periodoEvaluacionId);
    }
}
