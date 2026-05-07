package com.tp1.proyecto.prediccion.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public class PrediccionGlobalMlResponseDto {

    @JsonProperty("matricula_id")
    private Long matriculaId;

    @JsonProperty("bimestre_id")
    private Long bimestreId;

    @JsonProperty("puntaje_riesgo")
    private Double puntajeRiesgo;

    @JsonProperty("nivel_riesgo")
    private String nivelRiesgo;

    @JsonProperty("modelo_version")
    private String modeloVersion;

    @JsonProperty("variables_entrada")
    private Map<String, Object> variablesEntrada;

    public Long getMatriculaId() {
        return matriculaId;
    }

    public void setMatriculaId(Long matriculaId) {
        this.matriculaId = matriculaId;
    }

    public Long getBimestreId() {
        return bimestreId;
    }

    public void setBimestreId(Long bimestreId) {
        this.bimestreId = bimestreId;
    }

    public Double getPuntajeRiesgo() {
        return puntajeRiesgo;
    }

    public void setPuntajeRiesgo(Double puntajeRiesgo) {
        this.puntajeRiesgo = puntajeRiesgo;
    }

    public String getNivelRiesgo() {
        return nivelRiesgo;
    }

    public void setNivelRiesgo(String nivelRiesgo) {
        this.nivelRiesgo = nivelRiesgo;
    }

    public String getModeloVersion() {
        return modeloVersion;
    }

    public void setModeloVersion(String modeloVersion) {
        this.modeloVersion = modeloVersion;
    }

    public Map<String, Object> getVariablesEntrada() {
        return variablesEntrada;
    }

    public void setVariablesEntrada(Map<String, Object> variablesEntrada) {
        this.variablesEntrada = variablesEntrada;
    }
}
