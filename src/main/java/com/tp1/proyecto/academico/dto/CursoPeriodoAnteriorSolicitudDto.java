package com.tp1.proyecto.academico.dto;

import jakarta.validation.constraints.NotNull;

public class CursoPeriodoAnteriorSolicitudDto {

    @NotNull(message = "El periodo academico es obligatorio")
    private Long periodoAcademicoId;

    public Long getPeriodoAcademicoId() {
        return periodoAcademicoId;
    }

    public void setPeriodoAcademicoId(Long periodoAcademicoId) {
        this.periodoAcademicoId = periodoAcademicoId;
    }
}
