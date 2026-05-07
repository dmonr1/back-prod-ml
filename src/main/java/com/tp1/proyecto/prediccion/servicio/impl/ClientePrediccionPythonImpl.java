package com.tp1.proyecto.prediccion.servicio.impl;

import com.tp1.proyecto.excepcion.ReglaNegocioException;
import com.tp1.proyecto.prediccion.dto.PrediccionMlRequestDto;
import com.tp1.proyecto.prediccion.dto.PrediccionMlResponseDto;
import com.tp1.proyecto.prediccion.servicio.ClientePrediccionPython;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ClientePrediccionPythonImpl implements ClientePrediccionPython {

    private final WebClient webClient;
    private final String mlUrl;
    private final String rutaPrediccion;

    public ClientePrediccionPythonImpl(
        WebClient webClient,
        @Value("${app.ml.url}") String mlUrl,
        @Value("${app.ml.ruta-prediccion}") String rutaPrediccion
    ) {
        this.webClient = webClient;
        this.mlUrl = mlUrl;
        this.rutaPrediccion = rutaPrediccion;
    }

    @Override
    public PrediccionMlResponseDto predecir(PrediccionMlRequestDto request) {
        try {
            return webClient.post()
                .uri(mlUrl + rutaPrediccion)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(PrediccionMlResponseDto.class)
                .block();
        } catch (Exception ex) {
            throw new ReglaNegocioException("No se pudo obtener respuesta del servicio Python: " + ex.getMessage());
        }
    }
}
