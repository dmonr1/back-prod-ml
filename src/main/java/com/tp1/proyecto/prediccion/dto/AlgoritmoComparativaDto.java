package com.tp1.proyecto.prediccion.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.Map;

public class AlgoritmoComparativaDto {
    private String id;
    private String nombre;
    private String familia;
    private Double accuracy;
    private Double precision;
    private Double recall;
    @JsonAlias("f1_score")
    private Double f1Score;
    @JsonAlias("roc_auc")
    private Double rocAuc;
    @JsonAlias("latencia_ms")
    private Double latenciaMs;
    private String estado; // ACTIVO, CANDIDATO, BASELINE
    private Integer ranking;
    private Map<String, Object> hiperparametros;

    public AlgoritmoComparativaDto() {}

    public AlgoritmoComparativaDto(
        String id,
        String nombre,
        String familia,
        Double accuracy,
        Double precision,
        Double recall,
        Double f1Score,
        Double rocAuc,
        Double latenciaMs,
        String estado,
        Integer ranking,
        Map<String, Object> hiperparametros
    ) {
        this.id = id;
        this.nombre = nombre;
        this.familia = familia;
        this.accuracy = accuracy;
        this.precision = precision;
        this.recall = recall;
        this.f1Score = f1Score;
        this.rocAuc = rocAuc;
        this.latenciaMs = latenciaMs;
        this.estado = estado;
        this.ranking = ranking;
        this.hiperparametros = hiperparametros;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFamilia() {
        return familia;
    }

    public void setFamilia(String familia) {
        this.familia = familia;
    }

    public Double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Double accuracy) {
        this.accuracy = accuracy;
    }

    public Double getPrecision() {
        return precision;
    }

    public void setPrecision(Double precision) {
        this.precision = precision;
    }

    public Double getRecall() {
        return recall;
    }

    public void setRecall(Double recall) {
        this.recall = recall;
    }

    public Double getF1Score() {
        return f1Score;
    }

    public void setF1Score(Double f1Score) {
        this.f1Score = f1Score;
    }

    public Double getRocAuc() {
        return rocAuc;
    }

    public void setRocAuc(Double rocAuc) {
        this.rocAuc = rocAuc;
    }

    public Double getLatenciaMs() {
        return latenciaMs;
    }

    public void setLatenciaMs(Double latenciaMs) {
        this.latenciaMs = latenciaMs;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Integer getRanking() {
        return ranking;
    }

    public void setRanking(Integer ranking) {
        this.ranking = ranking;
    }

    public Map<String, Object> getHiperparametros() {
        return hiperparametros;
    }

    public void setHiperparametros(Map<String, Object> hiperparametros) {
        this.hiperparametros = hiperparametros;
    }
}
