package com.tp1.proyecto.evaluacion.dto;

import java.time.LocalDate;

public class EvaluacionPendienteFechaRespuestaDto {
    private Long evaluacionId;
    private Long asignacionId;
    private Long periodoEvaluacionId;
    private String periodoEvaluacion;
    private String curso;
    private String tipoEvaluacion;
    private String nombre;
    private Integer numeroEvaluacion;
    private LocalDate fechaEvaluacion;

    public Long getEvaluacionId() { return evaluacionId; }
    public void setEvaluacionId(Long evaluacionId) { this.evaluacionId = evaluacionId; }
    public Long getAsignacionId() { return asignacionId; }
    public void setAsignacionId(Long asignacionId) { this.asignacionId = asignacionId; }
    public Long getPeriodoEvaluacionId() { return periodoEvaluacionId; }
    public void setPeriodoEvaluacionId(Long periodoEvaluacionId) { this.periodoEvaluacionId = periodoEvaluacionId; }
    public String getPeriodoEvaluacion() { return periodoEvaluacion; }
    public void setPeriodoEvaluacion(String periodoEvaluacion) { this.periodoEvaluacion = periodoEvaluacion; }
    public String getCurso() { return curso; }
    public void setCurso(String curso) { this.curso = curso; }
    public String getTipoEvaluacion() { return tipoEvaluacion; }
    public void setTipoEvaluacion(String tipoEvaluacion) { this.tipoEvaluacion = tipoEvaluacion; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Integer getNumeroEvaluacion() { return numeroEvaluacion; }
    public void setNumeroEvaluacion(Integer numeroEvaluacion) { this.numeroEvaluacion = numeroEvaluacion; }
    public LocalDate getFechaEvaluacion() { return fechaEvaluacion; }
    public void setFechaEvaluacion(LocalDate fechaEvaluacion) { this.fechaEvaluacion = fechaEvaluacion; }
}
