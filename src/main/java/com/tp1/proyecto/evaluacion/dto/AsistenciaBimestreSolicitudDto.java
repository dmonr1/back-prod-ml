package com.tp1.proyecto.evaluacion.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AsistenciaBimestreSolicitudDto {

    @NotNull
    private Long matriculaId;

    @NotNull
    @Min(0)
    private Integer clasesProgramadas;

    @NotNull
    @Min(0)
    private Integer clasesAsistidas;

    private String observacion;

    public Long getMatriculaId() {
        return matriculaId;
    }

    public void setMatriculaId(Long matriculaId) {
        this.matriculaId = matriculaId;
    }

    public Integer getClasesProgramadas() {
        return clasesProgramadas;
    }

    public void setClasesProgramadas(Integer clasesProgramadas) {
        this.clasesProgramadas = clasesProgramadas;
    }

    public Integer getClasesAsistidas() {
        return clasesAsistidas;
    }

    public void setClasesAsistidas(Integer clasesAsistidas) {
        this.clasesAsistidas = clasesAsistidas;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}
