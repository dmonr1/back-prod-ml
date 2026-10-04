package com.tp1.proyecto.prediccion.controlador;

import com.tp1.proyecto.prediccion.dto.ComparativaModelosDto;
import com.tp1.proyecto.prediccion.dto.ConfiguracionPredictoresDto;
import com.tp1.proyecto.prediccion.dto.PlanificadorReentrenamientoDto;
import com.tp1.proyecto.prediccion.servicio.ClientePrediccionPython;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ml")
@PreAuthorize("hasAnyRole('ADMIN','DIRECTOR_ACADEMICO')")
public class AdministracionMlControlador {

    private final ClientePrediccionPython clientePrediccionPython;

    public AdministracionMlControlador(ClientePrediccionPython clientePrediccionPython) {
        this.clientePrediccionPython = clientePrediccionPython;
    }

    @GetMapping("/configuracion-predictores")
    public ConfiguracionPredictoresDto obtenerConfiguracionPredictores() {
        return clientePrediccionPython.obtenerConfiguracionPredictores();
    }

    @PutMapping("/configuracion-predictores")
    public ConfiguracionPredictoresDto actualizarConfiguracionPredictores(
        @RequestBody ConfiguracionPredictoresDto request
    ) {
        return clientePrediccionPython.actualizarConfiguracionPredictores(request);
    }

    @GetMapping("/modelos-comparativa")
    public ComparativaModelosDto obtenerComparativaModelos() {
        return clientePrediccionPython.obtenerComparativaModelos();
    }

    @GetMapping("/planificador-reentrenamiento")
    public PlanificadorReentrenamientoDto obtenerPlanificadorReentrenamiento() {
        return clientePrediccionPython.obtenerPlanificadorReentrenamiento();
    }

    @PostMapping("/reentrenar")
    public PlanificadorReentrenamientoDto ejecutarReentrenamiento() {
        return clientePrediccionPython.ejecutarReentrenamiento();
    }
}
