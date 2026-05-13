package com.tp1.proyecto.academico.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class PeriodoAcademicoConPeriodosSolicitudDto extends PeriodoAcademicoSolicitudDto {

    @Valid
    @NotEmpty(message = "Debes registrar al menos un periodo de evaluacion")
    private List<PeriodoEvaluacionInicialSolicitudDto> periodosEvaluacion;

    public List<PeriodoEvaluacionInicialSolicitudDto> getPeriodosEvaluacion() {
        return periodosEvaluacion;
    }

    public void setPeriodosEvaluacion(List<PeriodoEvaluacionInicialSolicitudDto> periodosEvaluacion) {
        this.periodosEvaluacion = periodosEvaluacion;
    }
}
