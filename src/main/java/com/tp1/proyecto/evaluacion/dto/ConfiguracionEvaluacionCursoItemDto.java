package com.tp1.proyecto.evaluacion.dto;

public class ConfiguracionEvaluacionCursoItemDto {

    private Long tipoEvaluacionId;
    private String nombreTipoEvaluacion;
    private String descripcionTipoEvaluacion;
    private Integer cantidadBasePeriodo;
    private Integer cantidadEvaluaciones;
    private Boolean calcularEnPromedio;

    public Long getTipoEvaluacionId() {
        return tipoEvaluacionId;
    }

    public void setTipoEvaluacionId(Long tipoEvaluacionId) {
        this.tipoEvaluacionId = tipoEvaluacionId;
    }

    public String getNombreTipoEvaluacion() {
        return nombreTipoEvaluacion;
    }

    public void setNombreTipoEvaluacion(String nombreTipoEvaluacion) {
        this.nombreTipoEvaluacion = nombreTipoEvaluacion;
    }

    public String getDescripcionTipoEvaluacion() {
        return descripcionTipoEvaluacion;
    }

    public void setDescripcionTipoEvaluacion(String descripcionTipoEvaluacion) {
        this.descripcionTipoEvaluacion = descripcionTipoEvaluacion;
    }

    public Integer getCantidadEvaluaciones() {
        return cantidadEvaluaciones;
    }

    public void setCantidadEvaluaciones(Integer cantidadEvaluaciones) {
        this.cantidadEvaluaciones = cantidadEvaluaciones;
    }

    public Integer getCantidadBasePeriodo() {
        return cantidadBasePeriodo;
    }

    public void setCantidadBasePeriodo(Integer cantidadBasePeriodo) {
        this.cantidadBasePeriodo = cantidadBasePeriodo;
    }

    public Boolean getCalcularEnPromedio() {
        return calcularEnPromedio;
    }

    public void setCalcularEnPromedio(Boolean calcularEnPromedio) {
        this.calcularEnPromedio = calcularEnPromedio;
    }
}
