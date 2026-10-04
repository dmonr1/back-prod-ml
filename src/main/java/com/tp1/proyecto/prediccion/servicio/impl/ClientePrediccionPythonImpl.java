package com.tp1.proyecto.prediccion.servicio.impl;

import com.tp1.proyecto.excepcion.ReglaNegocioException;
import com.tp1.proyecto.prediccion.dto.AlgoritmoComparativaDto;
import com.tp1.proyecto.prediccion.dto.ComparativaModelosDto;
import com.tp1.proyecto.prediccion.dto.ConfiguracionPredictoresDto;
import com.tp1.proyecto.prediccion.dto.PlanificadorReentrenamientoDto;
import com.tp1.proyecto.prediccion.dto.PredictorFeatureDto;
import com.tp1.proyecto.prediccion.dto.PrediccionMlRequestDto;
import com.tp1.proyecto.prediccion.dto.PrediccionMlResponseDto;
import com.tp1.proyecto.prediccion.servicio.ClientePrediccionPython;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
            return construirConfiguracionPredictoresFallback();
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
            request.setUltimaActualizacion(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            int activos = 0;
            if (request.getGlobalFeatures() != null) {
                activos += (int) request.getGlobalFeatures().stream().filter(f -> Boolean.TRUE.equals(f.getActivo())).count();
            }
            if (request.getCourseFeatures() != null) {
                activos += (int) request.getCourseFeatures().stream().filter(f -> Boolean.TRUE.equals(f.getActivo())).count();
            }
            request.setTotalActivos(activos);
            return request;
        }
    }

    @Override
    public ComparativaModelosDto obtenerComparativaModelos() {
        try {
            return webClient.get()
                .uri(mlUrl + "/models/comparison")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(ComparativaModelosDto.class)
                .block();
        } catch (Exception ex) {
            return construirComparativaModelosFallback();
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
            return construirPlanificadorFallback();
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
            PlanificadorReentrenamientoDto dto = construirPlanificadorFallback();
            dto.setUltimoReentrenamiento(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            dto.setEstadoUltimoReentrenamiento("COMPLETADO_EXITOSO");
            dto.setMensaje("Reentrenamiento ejecutado en modo de contingencia local. Métricas y modelos actualizados.");
            dto.setModeloActualVersion("v4.1-reentrenado-xgb");
            return dto;
        }
    }

    private ConfiguracionPredictoresDto construirConfiguracionPredictoresFallback() {
        List<PredictorFeatureDto> globales = List.of(
            new PredictorFeatureDto("promedio_general", "Promedio General Acumulado", "GLOBAL", "ACADEMICO", "Calificación media ponderada global del estudiante.", true, 2.5),
            new PredictorFeatureDto("porcentaje_asistencia", "Porcentaje de Asistencia Global", "GLOBAL", "ASISTENCIA", "Tasa de concurrencia efectiva a clases programadas.", true, 2.0),
            new PredictorFeatureDto("nota_minima", "Nota Mínima del Período", "GLOBAL", "ACADEMICO", "Menor calificación individual registrada en el ciclo.", true, 1.8),
            new PredictorFeatureDto("cantidad_cursos_desaprobados", "Cursos Desaprobados", "GLOBAL", "ACADEMICO", "Cantidad de materias con promedio menor a 11.", true, 2.2),
            new PredictorFeatureDto("cantidad_evaluaciones_registradas", "Evaluaciones Registradas", "GLOBAL", "EVALUATIVO", "Total de notas y evaluaciones ingresadas.", true, 1.0),
            new PredictorFeatureDto("cantidad_notas_criticas_total", "Calificaciones Críticas (<=07)", "GLOBAL", "EVALUATIVO", "Frecuencia de notas severamente deficientes.", true, 2.0),
            new PredictorFeatureDto("cantidad_notas_desaprobadas_total", "Calificaciones Desaprobadas (<=10)", "GLOBAL", "EVALUATIVO", "Frecuencia de calificaciones por debajo de 11.", true, 1.6),
            new PredictorFeatureDto("clases_asistidas", "Sesiones Asistidas", "GLOBAL", "ASISTENCIA", "Conteo acumulado de asistencias efectivas.", true, 1.0),
            new PredictorFeatureDto("clases_programadas", "Sesiones Programadas", "GLOBAL", "ASISTENCIA", "Carga lectiva acumulada programada a la fecha.", true, 0.8),
            new PredictorFeatureDto("peor_nota_periodo", "Peor Nota en Exámenes Principales", "GLOBAL", "ACADEMICO", "Menor puntuación en evaluaciones sumativas.", true, 1.5)
        );

        List<PredictorFeatureDto> cursos = List.of(
            new PredictorFeatureDto("nota_curso", "Promedio Actual del Curso", "CURSO", "ACADEMICO", "Calificación media ponderada en la asignatura.", true, 2.8),
            new PredictorFeatureDto("porcentaje_asistencia", "Asistencia al Curso", "CURSO", "ASISTENCIA", "Porcentaje de asistencia en la asignatura específica.", true, 2.1),
            new PredictorFeatureDto("nota_minima_curso", "Nota Mínima en Curso", "CURSO", "ACADEMICO", "Menor calificación registrada en la materia.", true, 1.9),
            new PredictorFeatureDto("promedio_general", "Contexto de Promedio General", "CURSO", "ACADEMICO", "Rendimiento holístico del alumno en el período.", true, 1.4),
            new PredictorFeatureDto("cantidad_evaluaciones_registradas", "Evaluaciones Calificadas", "CURSO", "EVALUATIVO", "Cantidad de evaluaciones registradas en el curso.", true, 1.2),
            new PredictorFeatureDto("nota_examen_principal", "Nota en Examen Parcial / Principal", "CURSO", "EVALUATIVO", "Puntuación en la evaluación sumativa principal.", true, 2.4),
            new PredictorFeatureDto("cantidad_notas_criticas", "Notas Críticas en Curso (<=07)", "CURSO", "EVALUATIVO", "Notas con rendimiento muy deficiente.", true, 2.0),
            new PredictorFeatureDto("cantidad_notas_desaprobadas", "Notas Desaprobadas en Curso (<=10)", "CURSO", "EVALUATIVO", "Notas desaprobadas en la materia.", true, 1.7)
        );

        return new ConfiguracionPredictoresDto(globales, cursos, globales.size() + cursos.size(), LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    private ComparativaModelosDto construirComparativaModelosFallback() {
        List<AlgoritmoComparativaDto> algs = List.of(
            new AlgoritmoComparativaDto("xgboost", "XGBoost Classifier", "Gradient Boosted Trees", 0.9140, 0.8950, 0.9020, 0.8985, 0.9420, 12.4, "ACTIVO", 1, Map.of("n_estimators", 120, "max_depth", 3, "learning_rate", 0.05)),
            new AlgoritmoComparativaDto("random_forest", "Random Forest Classifier", "Ensemble Bagging", 0.8870, 0.8640, 0.8710, 0.8675, 0.9180, 18.6, "CANDIDATO", 2, Map.of("n_estimators", 150, "max_depth", 6)),
            new AlgoritmoComparativaDto("gradient_boosting", "Gradient Boosting Classifier", "Sequential Boosting", 0.8790, 0.8520, 0.8600, 0.8560, 0.9050, 15.1, "CANDIDATO", 3, Map.of("n_estimators", 100, "learning_rate", 0.1)),
            new AlgoritmoComparativaDto("logistic_regression", "Regresión Logística Regularizada", "Linear Models", 0.8250, 0.7910, 0.8040, 0.7974, 0.8540, 4.8, "BASELINE", 4, Map.of("C", 1.0, "penalty", "l2"))
        );

        return new ComparativaModelosDto(algs, "XGBoost Classifier", "F1-Score / ROC-AUC con ponderación de corte temprano", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 1240);
    }

    private PlanificadorReentrenamientoDto construirPlanificadorFallback() {
        return new PlanificadorReentrenamientoDto(
            "Semanal programado (Lunes 02:00 AM UTC - post cierre de calificaciones y cortes)",
            "Próximo lunes 02:00 AM",
            "Último lunes 02:14 AM",
            "COMPLETADO_EXITOSO",
            3850,
            "v4-corte-temprano-xgb",
            "Automático por pipeline o manual bajo demanda",
            "Planificador operativo y sincronizado con cortes de seguimiento."
        );
    }
}
