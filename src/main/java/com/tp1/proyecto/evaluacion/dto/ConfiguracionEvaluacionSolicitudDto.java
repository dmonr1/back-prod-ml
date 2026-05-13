package com.tp1.proyecto.evaluacion.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class ConfiguracionEvaluacionSolicitudDto {

    @NotNull
    private Long periodoAcademicoId;

    @NotNull
    private Long periodoEvaluacionId;

    @NotNull
    private Long cursoId;

    private Long gradoId;

    @NotNull
    private Long tipoEvaluacionId;

    @NotNull
    @Min(1)
    private Integer cantidadEvaluaciones;

    @NotNull
    private Boolean calcularEnPromedio;

    public Long getPeriodoAcademicoId() {
        return periodoAcademicoId;
    }

    public void setPeriodoAcademicoId(Long periodoAcademicoId) {
        this.periodoAcademicoId = periodoAcademicoId;
    }

    public Long getPeriodoEvaluacionId() {
        return periodoEvaluacionId;
    }

    public void setPeriodoEvaluacionId(Long periodoEvaluacionId) {
        this.periodoEvaluacionId = periodoEvaluacionId;
    }

    public Long getCursoId() {
        return cursoId;
    }

    public void setCursoId(Long cursoId) {
        this.cursoId = cursoId;
    }

    public Long getGradoId() {
        return gradoId;
    }

    public void setGradoId(Long gradoId) {
        this.gradoId = gradoId;
    }

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
