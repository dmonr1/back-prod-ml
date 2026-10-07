package com.tp1.proyecto.prediccion.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.List;

public class ComparativaModelosDto {
    private List<AlgoritmoComparativaDto> algoritmos;
    @JsonAlias("modelo_recomendado")
    private String modeloRecomendado;
    @JsonAlias("metrica_optimizada")
    private String metricaOptimizada;
    @JsonAlias("fecha_evaluacion")
    private String fechaEvaluacion;
    @JsonAlias("total_registros_evaluados")
    private Integer totalRegistrosEvaluados;

    @JsonAlias("tipo_modelo")
    private String tipoModelo;
    @JsonAlias("origen_datos")
    private String origenDatos;
    private List<String> variables;
    @JsonAlias("registros_entrenamiento")
    private Integer registrosEntrenamiento;
    @JsonAlias("alumnos_prueba")
    private Integer alumnosPrueba;
    @JsonAlias("alcance_metricas")
    private String alcanceMetricas;

    public ComparativaModelosDto() {}

    public ComparativaModelosDto(
        List<AlgoritmoComparativaDto> algoritmos,
        String modeloRecomendado,
        String metricaOptimizada,
        String fechaEvaluacion,
        Integer totalRegistrosEvaluados
    ) {
        this.algoritmos = algoritmos;
        this.modeloRecomendado = modeloRecomendado;
        this.metricaOptimizada = metricaOptimizada;
        this.fechaEvaluacion = fechaEvaluacion;
        this.totalRegistrosEvaluados = totalRegistrosEvaluados;
    }

    public List<AlgoritmoComparativaDto> getAlgoritmos() {
        return algoritmos;
    }

    public void setAlgoritmos(List<AlgoritmoComparativaDto> algoritmos) {
        this.algoritmos = algoritmos;
    }

    public String getModeloRecomendado() {
        return modeloRecomendado;
    }

    public void setModeloRecomendado(String modeloRecomendado) {
        this.modeloRecomendado = modeloRecomendado;
    }

    public String getMetricaOptimizada() {
        return metricaOptimizada;
    }

    public void setMetricaOptimizada(String metricaOptimizada) {
        this.metricaOptimizada = metricaOptimizada;
    }

    public String getFechaEvaluacion() {
        return fechaEvaluacion;
    }

    public void setFechaEvaluacion(String fechaEvaluacion) {
        this.fechaEvaluacion = fechaEvaluacion;
    }

    public Integer getTotalRegistrosEvaluados() {
        return totalRegistrosEvaluados;
    }

    public void setTotalRegistrosEvaluados(Integer totalRegistrosEvaluados) {
        this.totalRegistrosEvaluados = totalRegistrosEvaluados;
    }
    public String getTipoModelo() { return tipoModelo; }
    public void setTipoModelo(String value) { this.tipoModelo = value; }
    public String getOrigenDatos() { return origenDatos; }
    public void setOrigenDatos(String value) { this.origenDatos = value; }
    public List<String> getVariables() { return variables; }
    public void setVariables(List<String> value) { this.variables = value; }
    public Integer getRegistrosEntrenamiento() { return registrosEntrenamiento; }
    public void setRegistrosEntrenamiento(Integer value) { this.registrosEntrenamiento = value; }
    public Integer getAlumnosPrueba() { return alumnosPrueba; }
    public void setAlumnosPrueba(Integer value) { this.alumnosPrueba = value; }
    public String getAlcanceMetricas() { return alcanceMetricas; }
    public void setAlcanceMetricas(String value) { this.alcanceMetricas = value; }
}
