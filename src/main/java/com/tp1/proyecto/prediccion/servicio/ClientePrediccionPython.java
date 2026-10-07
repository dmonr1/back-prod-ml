package com.tp1.proyecto.prediccion.servicio;

import com.tp1.proyecto.prediccion.dto.ComparativaModelosDto;
import com.tp1.proyecto.prediccion.dto.ConfiguracionPredictoresDto;
import com.tp1.proyecto.prediccion.dto.PlanificadorReentrenamientoDto;
import com.tp1.proyecto.prediccion.dto.PrediccionMlRequestDto;
import com.tp1.proyecto.prediccion.dto.PrediccionMlResponseDto;

public interface ClientePrediccionPython {

    PrediccionMlResponseDto predecir(PrediccionMlRequestDto request);

    ConfiguracionPredictoresDto obtenerConfiguracionPredictores();

    ConfiguracionPredictoresDto actualizarConfiguracionPredictores(ConfiguracionPredictoresDto request);

    ComparativaModelosDto obtenerComparativaModelos(String tipo);

    PlanificadorReentrenamientoDto obtenerPlanificadorReentrenamiento();

    PlanificadorReentrenamientoDto ejecutarReentrenamiento();
}
