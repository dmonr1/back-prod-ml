package com.tp1.proyecto.evaluacion.controlador;

import com.tp1.proyecto.evaluacion.dto.ConfiguracionEvaluacionRespuestaDto;
import com.tp1.proyecto.evaluacion.dto.ConfiguracionEvaluacionSolicitudDto;
import com.tp1.proyecto.evaluacion.servicio.ConfiguracionEvaluacionServicio;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/configuraciones-evaluacion")
@PreAuthorize("hasRole('ADMIN')")
public class ConfiguracionEvaluacionControlador {

    private final ConfiguracionEvaluacionServicio configuracionEvaluacionServicio;

    public ConfiguracionEvaluacionControlador(ConfiguracionEvaluacionServicio configuracionEvaluacionServicio) {
        this.configuracionEvaluacionServicio = configuracionEvaluacionServicio;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ConfiguracionEvaluacionRespuestaDto crear(@Valid @RequestBody ConfiguracionEvaluacionSolicitudDto solicitud) {
        return configuracionEvaluacionServicio.crear(solicitud);
    }

    @GetMapping
    public List<ConfiguracionEvaluacionRespuestaDto> listar(
        @RequestParam Long bimestreId,
        @RequestParam Long cursoId
    ) {
        return configuracionEvaluacionServicio.listarPorBimestreYCurso(bimestreId, cursoId);
    }
}
