package com.tp1.proyecto.prediccion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tp1.proyecto.excepcion.ReglaNegocioException;
import com.tp1.proyecto.prediccion.dto.ComparativaModelosDto;
import com.tp1.proyecto.prediccion.dto.PlanificadorReentrenamientoDto;
import com.tp1.proyecto.prediccion.servicio.impl.ClientePrediccionPythonImpl;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import static org.junit.jupiter.api.Assertions.*;

class ClientePrediccionPythonTest {
    @Test
    void readsPythonSnakeCaseAndSerializesCamelCaseForAngular() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String body = """
            {"algoritmos":[{"id":"xgboost","f1_score":0.67,"roc_auc":0.88,"latencia_ms":0.01}],
             "modelo_recomendado":"XGBoost","total_registros_evaluados":10000,
             "tipo_modelo":"CURSO","origen_datos":"synthetic_cutoff_simulation",
             "alumnos_prueba":500,"registros_entrenamiento":30000,"variables":["nota_curso"]}
            """;
        ComparativaModelosDto result = mapper.readValue(body, ComparativaModelosDto.class);
        assertEquals(0.67, result.getAlgoritmos().get(0).getF1Score());
        assertEquals(10000, result.getTotalRegistrosEvaluados());
        assertEquals("CURSO", result.getTipoModelo());
        assertEquals(500, result.getAlumnosPrueba());
        String encoded = mapper.writeValueAsString(result);
        assertTrue(encoded.contains("\"f1Score\":0.67"));
        assertTrue(encoded.contains("\"tipoModelo\":\"CURSO\""));
        assertFalse(encoded.contains("f1_score"));
        PlanificadorReentrenamientoDto schedule = mapper.readValue(
            "{\"estado_ultimo_reentrenamiento\":\"FALLIDO\",\"modelo_actual_version\":\"run-1\"}",
            PlanificadorReentrenamientoDto.class);
        assertEquals("FALLIDO", schedule.getEstadoUltimoReentrenamiento());
        assertEquals("run-1", schedule.getModeloActualVersion());
    }

    @Test
    void errorsDoNotBecomeSuccessfulTrainingOrInventedMetrics() {
        WebClient client = WebClient.builder().exchangeFunction(request -> Mono.just(
            ClientResponse.create(HttpStatus.INTERNAL_SERVER_ERROR).body("Training failed").build())).build();
        ClientePrediccionPythonImpl service = new ClientePrediccionPythonImpl(client, "http://ml", "/predict");
        assertThrows(ReglaNegocioException.class, service::ejecutarReentrenamiento);
        assertThrows(ReglaNegocioException.class, () -> service.obtenerComparativaModelos("GLOBAL"));
        assertThrows(ReglaNegocioException.class, service::obtenerPlanificadorReentrenamiento);
        assertThrows(ReglaNegocioException.class, service::obtenerConfiguracionPredictores);
    }

    @Test
    void forwardsCourseTaskToPython() {
        WebClient client = WebClient.builder().exchangeFunction(request -> {
            assertEquals("task=course", request.url().getQuery());
            return Mono.just(ClientResponse.create(HttpStatus.OK).header("Content-Type", "application/json")
                .body("{\"tipo_modelo\":\"CURSO\",\"total_registros_evaluados\":10000}").build());
        }).build();
        ClientePrediccionPythonImpl service = new ClientePrediccionPythonImpl(client, "http://ml", "/predict");
        assertEquals("CURSO", service.obtenerComparativaModelos("CURSO").getTipoModelo());
    }
}
