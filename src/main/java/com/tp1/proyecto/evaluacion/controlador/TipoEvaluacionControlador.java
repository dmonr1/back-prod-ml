package com.tp1.proyecto.evaluacion.controlador;

import com.tp1.proyecto.evaluacion.dto.TipoEvaluacionRespuestaDto;
import com.tp1.proyecto.evaluacion.dto.TipoEvaluacionSolicitudDto;
import com.tp1.proyecto.evaluacion.servicio.TipoEvaluacionServicio;
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
@RequestMapping("/api/tipos-evaluacion")
@PreAuthorize("hasRole('ADMIN')")
public class TipoEvaluacionControlador {

    private final TipoEvaluacionServicio tipoEvaluacionServicio;

    public TipoEvaluacionControlador(TipoEvaluacionServicio tipoEvaluacionServicio) {
        this.tipoEvaluacionServicio = tipoEvaluacionServicio;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TipoEvaluacionRespuestaDto crear(@Valid @RequestBody TipoEvaluacionSolicitudDto solicitud) {
        return tipoEvaluacionServicio.crear(solicitud);
    }

    @GetMapping
    public List<TipoEvaluacionRespuestaDto> listar() {
        return tipoEvaluacionServicio.listar();
    }
}
