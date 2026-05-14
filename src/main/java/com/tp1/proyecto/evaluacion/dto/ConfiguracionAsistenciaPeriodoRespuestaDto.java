package com.tp1.proyecto.evaluacion.dto;

public class ConfiguracionAsistenciaPeriodoRespuestaDto {

    private Long id;
    private Long docenteCursoSeccionId;
    private Long periodoEvaluacionId;
    private Integer clasesProgramadas;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDocenteCursoSeccionId() {
        return docenteCursoSeccionId;
    }

    public void setDocenteCursoSeccionId(Long docenteCursoSeccionId) {
        this.docenteCursoSeccionId = docenteCursoSeccionId;
    }

    public Long getPeriodoEvaluacionId() {
        return periodoEvaluacionId;
    }

    public void setPeriodoEvaluacionId(Long periodoEvaluacionId) {
        this.periodoEvaluacionId = periodoEvaluacionId;
    }

    public Integer getClasesProgramadas() {
        return clasesProgramadas;
    }

    public void setClasesProgramadas(Integer clasesProgramadas) {
        this.clasesProgramadas = clasesProgramadas;
    }
}
