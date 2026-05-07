package com.tp1.proyecto.evaluacion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class EvaluacionSolicitudDto {

    @NotNull
    private Long configuracionEvaluacionId;

    @NotNull
    private Long docenteCursoSeccionId;

    @NotNull
    private Long bimestreId;

    @NotNull
    private Long tipoEvaluacionId;

    @NotNull
    private Integer numeroEvaluacion;

    @NotBlank
    private String nombre;

    private LocalDate fechaEvaluacion;

    public Long getConfiguracionEvaluacionId() {
        return configuracionEvaluacionId;
    }

    public void setConfiguracionEvaluacionId(Long configuracionEvaluacionId) {
        this.configuracionEvaluacionId = configuracionEvaluacionId;
    }

    public Long getDocenteCursoSeccionId() {
        return docenteCursoSeccionId;
    }

    public void setDocenteCursoSeccionId(Long docenteCursoSeccionId) {
        this.docenteCursoSeccionId = docenteCursoSeccionId;
    }

    public Long getBimestreId() {
        return bimestreId;
    }

    public void setBimestreId(Long bimestreId) {
        this.bimestreId = bimestreId;
    }

    public Long getTipoEvaluacionId() {
        return tipoEvaluacionId;
    }

    public void setTipoEvaluacionId(Long tipoEvaluacionId) {
        this.tipoEvaluacionId = tipoEvaluacionId;
    }

    public Integer getNumeroEvaluacion() {
        return numeroEvaluacion;
    }

    public void setNumeroEvaluacion(Integer numeroEvaluacion) {
        this.numeroEvaluacion = numeroEvaluacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDate getFechaEvaluacion() {
        return fechaEvaluacion;
    }

    public void setFechaEvaluacion(LocalDate fechaEvaluacion) {
        this.fechaEvaluacion = fechaEvaluacion;
    }
}
