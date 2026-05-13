package com.tp1.proyecto.academico.dto;

import java.util.List;

public class PeriodoAcademicoConPeriodosRespuestaDto {

    private PeriodoAcademicoRespuestaDto periodoAcademico;
    private List<PeriodoEvaluacionRespuestaDto> periodosEvaluacion;

    public PeriodoAcademicoRespuestaDto getPeriodoAcademico() {
        return periodoAcademico;
    }

    public void setPeriodoAcademico(PeriodoAcademicoRespuestaDto periodoAcademico) {
        this.periodoAcademico = periodoAcademico;
    }

    public List<PeriodoEvaluacionRespuestaDto> getPeriodosEvaluacion() {
        return periodosEvaluacion;
    }

    public void setPeriodosEvaluacion(List<PeriodoEvaluacionRespuestaDto> periodosEvaluacion) {
        this.periodosEvaluacion = periodosEvaluacion;
    }
}
