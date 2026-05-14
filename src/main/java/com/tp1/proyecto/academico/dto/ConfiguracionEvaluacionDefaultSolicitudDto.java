package com.tp1.proyecto.academico.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class ConfiguracionEvaluacionDefaultSolicitudDto {

    @NotNull(message = "El tipo de evaluacion es obligatorio")
    private Long tipoEvaluacionId;

    @NotNull(message = "La cantidad de evaluaciones es obligatoria")
    @Min(value = 0, message = "La cantidad de evaluaciones no puede ser negativa")
    private Integer cantidadEvaluaciones;

    @NotNull(message = "Debes indicar si la evaluacion calcula en promedio")
    private Boolean calcularEnPromedio;

    public Long getTipoEvaluacionId() {
        return tipoEvaluacionId;
    }

    public void setTipoEvaluacionId(Long tipoEvaluacionId) {
        this.tipoEvaluacionId = tipoEvaluacionId;
    }

    public Integer getCantidadEvaluaciones() {
        return cantidadEvaluaciones;
    }

    public void setCantidadEvaluaciones(Integer cantidadEvaluaciones) {
        this.cantidadEvaluaciones = cantidadEvaluaciones;
    }

    public Boolean getCalcularEnPromedio() {
        return calcularEnPromedio;
    }

    public void setCalcularEnPromedio(Boolean calcularEnPromedio) {
        this.calcularEnPromedio = calcularEnPromedio;
    }
}
