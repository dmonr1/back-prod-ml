package com.tp1.proyecto.prediccion.dto;

import java.util.List;

public class ComparativaModelosDto {
    private List<AlgoritmoComparativaDto> algoritmos;
    private String modeloRecomendado;
    private String metricaOptimizada;
    private String fechaEvaluacion;
    private Integer totalRegistrosEvaluados;

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
}
