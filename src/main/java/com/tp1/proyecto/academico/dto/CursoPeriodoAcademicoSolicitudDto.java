package com.tp1.proyecto.academico.dto;

import jakarta.validation.constraints.NotNull;

public class CursoPeriodoAcademicoSolicitudDto {

    @NotNull(message = "El periodo academico es obligatorio")
    private Long periodoAcademicoId;

    @NotNull(message = "El curso es obligatorio")
    private Long cursoId;

    public Long getPeriodoAcademicoId() {
        return periodoAcademicoId;
    }

    public void setPeriodoAcademicoId(Long periodoAcademicoId) {
        this.periodoAcademicoId = periodoAcademicoId;
    }

    public Long getCursoId() {
        return cursoId;
    }

    public void setCursoId(Long cursoId) {
        this.cursoId = cursoId;
    }
}
