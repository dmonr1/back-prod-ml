package com.tp1.proyecto.prediccion.servicio;

import com.tp1.proyecto.prediccion.dto.PrediccionMlRequestDto;
import com.tp1.proyecto.prediccion.dto.PrediccionMlResponseDto;

public interface ClientePrediccionPython {

    PrediccionMlResponseDto predecir(PrediccionMlRequestDto request);
}
