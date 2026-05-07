package com.tp1.proyecto.prediccion.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PrediccionCursoMlResponseDto {

    @JsonProperty("matricula_id")
    private Long matriculaId;

    @JsonProperty("curso_id")
    private Long cursoId;

    @JsonProperty("curso_nombre")
    private String cursoNombre;

    @JsonProperty("bimestre_id")
    private Long bimestreId;

    @JsonProperty("puntaje_riesgo")
    private Double puntajeRiesgo;

    @JsonProperty("nivel_riesgo")
    private String nivelRiesgo;

    @JsonProperty("modelo_version")
    private String modeloVersion;

    @JsonProperty("variables_entrada")
    private Object variablesEntrada;

    public Long getMatriculaId() {
        return matriculaId;
    }

    public void setMatriculaId(Long matriculaId) {
        this.matriculaId = matriculaId;
    }

    public Long getCursoId() {
        return cursoId;
    }

    public void setCursoId(Long cursoId) {
        this.cursoId = cursoId;
    }

    public String getCursoNombre() {
        return cursoNombre;
    }

    public void setCursoNombre(String cursoNombre) {
        this.cursoNombre = cursoNombre;
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

    public Object getVariablesEntrada() {
        return variablesEntrada;
    }

    public void setVariablesEntrada(Object variablesEntrada) {
        this.variablesEntrada = variablesEntrada;
    }
}
