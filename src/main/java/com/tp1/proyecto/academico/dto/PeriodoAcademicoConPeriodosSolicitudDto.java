package com.tp1.proyecto.academico.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class PeriodoAcademicoConPeriodosSolicitudDto extends PeriodoAcademicoSolicitudDto {

    @Valid
    @NotEmpty(message = "Debes registrar al menos un periodo de evaluacion")
    private List<PeriodoEvaluacionInicialSolicitudDto> periodosEvaluacion;

    @Valid
    @NotEmpty(message = "Debes registrar al menos una configuracion de evaluacion")
    private List<ConfiguracionEvaluacionDefaultSolicitudDto> configuracionesEvaluacionDefault;

    private List<Long> cursosIds;

    private Boolean copiarCursosPeriodoAnterior;

    public List<PeriodoEvaluacionInicialSolicitudDto> getPeriodosEvaluacion() {
        return periodosEvaluacion;
    }

    public void setPeriodosEvaluacion(List<PeriodoEvaluacionInicialSolicitudDto> periodosEvaluacion) {
        this.periodosEvaluacion = periodosEvaluacion;
    }

    public List<ConfiguracionEvaluacionDefaultSolicitudDto> getConfiguracionesEvaluacionDefault() {
        return configuracionesEvaluacionDefault;
    }

    public void setConfiguracionesEvaluacionDefault(
        List<ConfiguracionEvaluacionDefaultSolicitudDto> configuracionesEvaluacionDefault
    ) {
        this.configuracionesEvaluacionDefault = configuracionesEvaluacionDefault;
    }

    public List<Long> getCursosIds() {
        return cursosIds;
    }

    public void setCursosIds(List<Long> cursosIds) {
        this.cursosIds = cursosIds;
    }

    public Boolean getCopiarCursosPeriodoAnterior() {
        return copiarCursosPeriodoAnterior;
    }

    public void setCopiarCursosPeriodoAnterior(Boolean copiarCursosPeriodoAnterior) {
        this.copiarCursosPeriodoAnterior = copiarCursosPeriodoAnterior;
    }
}
