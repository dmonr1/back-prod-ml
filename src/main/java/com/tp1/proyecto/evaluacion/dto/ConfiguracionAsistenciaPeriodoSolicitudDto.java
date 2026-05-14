package com.tp1.proyecto.evaluacion.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class ConfiguracionAsistenciaPeriodoSolicitudDto {

    @NotNull
    private Long docenteCursoSeccionId;

    @NotNull
    @Min(0)
    private Integer clasesProgramadas;

    public Long getDocenteCursoSeccionId() {
        return docenteCursoSeccionId;
    }

    public void setDocenteCursoSeccionId(Long docenteCursoSeccionId) {
        this.docenteCursoSeccionId = docenteCursoSeccionId;
    }

    public Integer getClasesProgramadas() {
        return clasesProgramadas;
    }

    public void setClasesProgramadas(Integer clasesProgramadas) {
        this.clasesProgramadas = clasesProgramadas;
    }
}
