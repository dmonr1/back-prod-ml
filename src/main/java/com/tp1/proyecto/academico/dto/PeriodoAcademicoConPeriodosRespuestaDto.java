package com.tp1.proyecto.academico.dto;

import java.util.List;

public class PeriodoAcademicoConPeriodosRespuestaDto {

    private PeriodoAcademicoRespuestaDto periodoAcademico;
    private List<PeriodoEvaluacionRespuestaDto> periodosEvaluacion;
    private List<ConfiguracionEvaluacionDefaultSolicitudDto> configuracionesEvaluacionDefault;
    private List<CursoPeriodoAcademicoRespuestaDto> cursosPeriodoAcademico;

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

    public List<ConfiguracionEvaluacionDefaultSolicitudDto> getConfiguracionesEvaluacionDefault() {
        return configuracionesEvaluacionDefault;
    }

    public void setConfiguracionesEvaluacionDefault(
        List<ConfiguracionEvaluacionDefaultSolicitudDto> configuracionesEvaluacionDefault
    ) {
        this.configuracionesEvaluacionDefault = configuracionesEvaluacionDefault;
    }

    public List<CursoPeriodoAcademicoRespuestaDto> getCursosPeriodoAcademico() {
        return cursosPeriodoAcademico;
    }

    public void setCursosPeriodoAcademico(List<CursoPeriodoAcademicoRespuestaDto> cursosPeriodoAcademico) {
        this.cursosPeriodoAcademico = cursosPeriodoAcademico;
    }
}
