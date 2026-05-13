package com.tp1.proyecto.academico.controlador;

import com.tp1.proyecto.academico.dto.PeriodoEvaluacionRespuestaDto;
import com.tp1.proyecto.academico.dto.PeriodoEvaluacionSolicitudDto;
import com.tp1.proyecto.academico.servicio.PeriodoEvaluacionServicio;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/periodos-evaluacion")
public class PeriodoEvaluacionControlador {

    private final PeriodoEvaluacionServicio periodoEvaluacionServicio;

    public PeriodoEvaluacionControlador(PeriodoEvaluacionServicio periodoEvaluacionServicio) {
        this.periodoEvaluacionServicio = periodoEvaluacionServicio;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','DOCENTE','DOCENTE_TUTOR')")
    public List<PeriodoEvaluacionRespuestaDto> listar() {
        return periodoEvaluacionServicio.listar();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public PeriodoEvaluacionRespuestaDto crear(@Valid @RequestBody PeriodoEvaluacionSolicitudDto solicitud) {
        return periodoEvaluacionServicio.crear(solicitud);
    }
}
