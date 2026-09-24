package com.tp1.proyecto.prediccion.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

public class PrediccionCursoMlResponseDto {

    @JsonProperty("matricula_id")
    private Long matriculaId;

    @JsonProperty("curso_id")
    private Long cursoId;

    @JsonProperty("curso_nombre")
    private String cursoNombre;

    @JsonProperty("periodo_evaluacion_id")
    private Long periodoEvaluacionId;

    @JsonProperty("corte_seguimiento_id")
    private Long corteSeguimientoId;
    @JsonProperty("semana_corte")
    private Integer semanaCorte;
    @JsonProperty("fecha_corte")
    private LocalDate fechaCorte;

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

    public Long getPeriodoEvaluacionId() {
        return periodoEvaluacionId;
    }

    public void setPeriodoEvaluacionId(Long periodoEvaluacionId) {
        this.periodoEvaluacionId = periodoEvaluacionId;
    }

    public Long getCorteSeguimientoId() { return corteSeguimientoId; }
    public void setCorteSeguimientoId(Long corteSeguimientoId) { this.corteSeguimientoId = corteSeguimientoId; }
    public Integer getSemanaCorte() { return semanaCorte; }
    public void setSemanaCorte(Integer semanaCorte) { this.semanaCorte = semanaCorte; }
    public LocalDate getFechaCorte() { return fechaCorte; }
    public void setFechaCorte(LocalDate fechaCorte) { this.fechaCorte = fechaCorte; }

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
