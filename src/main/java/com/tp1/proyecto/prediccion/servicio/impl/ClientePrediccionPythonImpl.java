package com.tp1.proyecto.prediccion.servicio.impl;

import com.tp1.proyecto.excepcion.ReglaNegocioException;
import com.tp1.proyecto.prediccion.dto.ComparativaModelosDto;
import com.tp1.proyecto.prediccion.dto.ConfiguracionPredictoresDto;
import com.tp1.proyecto.prediccion.dto.PlanificadorReentrenamientoDto;
import com.tp1.proyecto.prediccion.dto.PredictorFeatureDto;
import com.tp1.proyecto.prediccion.dto.PrediccionMlRequestDto;
import com.tp1.proyecto.prediccion.dto.PrediccionMlResponseDto;
import com.tp1.proyecto.prediccion.servicio.ClientePrediccionPython;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

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
        } catch (WebClientResponseException ex) {
            String detalle = ex.getResponseBodyAsString();
            throw new ReglaNegocioException(
                "El servicio Python rechazó la predicción (HTTP " + ex.getStatusCode().value() + "): "
                    + (detalle.isBlank() ? ex.getMessage() : detalle)
            );
        } catch (Exception ex) {
            throw new ReglaNegocioException("No se pudo obtener respuesta del servicio Python: " + ex.getMessage());
        }
    }

    @Override
    public ConfiguracionPredictoresDto obtenerConfiguracionPredictores() {
        try {
            return webClient.get()
                .uri(mlUrl + "/predictors/config")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(ConfiguracionPredictoresDto.class)
                .block();
        } catch (Exception ex) {
            throw new ReglaNegocioException("No se pudieron consultar los predictores del modelo: " + ex.getMessage());
        }
    }

    @Override
    public ConfiguracionPredictoresDto actualizarConfiguracionPredictores(ConfiguracionPredictoresDto request) {
        try {
            List<PredictorFeatureDto> todas = new ArrayList<>();
            if (request.getGlobalFeatures() != null) todas.addAll(request.getGlobalFeatures());
            if (request.getCourseFeatures() != null) todas.addAll(request.getCourseFeatures());

            return webClient.put()
                .uri(mlUrl + "/predictors/config")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("features", todas))
                .retrieve()
                .bodyToMono(ConfiguracionPredictoresDto.class)
                .block();
        } catch (Exception ex) {
            throw new ReglaNegocioException("No se pudo actualizar el contrato de predictores: " + ex.getMessage());
        }
    }

    @Override
    public ComparativaModelosDto obtenerComparativaModelos(String tipo) {
        if (!"GLOBAL".equals(tipo) && !"CURSO".equals(tipo)) {
            throw new ReglaNegocioException("El tipo debe ser GLOBAL o CURSO.");
        }
        try {
            return webClient.get()
                .uri(mlUrl + "/models/comparison?task=" + ("CURSO".equals(tipo) ? "course" : "global"))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(ComparativaModelosDto.class)
                .block();
        } catch (Exception ex) {
            throw new ReglaNegocioException("No se pudo obtener la evaluación del servicio ML: " + ex.getMessage());
        }
    }

    @Override
    public PlanificadorReentrenamientoDto obtenerPlanificadorReentrenamiento() {
        try {
            return webClient.get()
                .uri(mlUrl + "/retraining/schedule")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(PlanificadorReentrenamientoDto.class)
                .block();
        } catch (Exception ex) {
            throw new ReglaNegocioException("No se pudo consultar el estado de entrenamiento: " + ex.getMessage());
        }
    }

    @Override
    public PlanificadorReentrenamientoDto ejecutarReentrenamiento() {
        try {
            return webClient.post()
                .uri(mlUrl + "/retraining/run")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(PlanificadorReentrenamientoDto.class)
                .block();
        } catch (Exception ex) {
            throw new ReglaNegocioException("El entrenamiento no pudo completarse en el servicio ML: " + ex.getMessage());
        }
    }

}
