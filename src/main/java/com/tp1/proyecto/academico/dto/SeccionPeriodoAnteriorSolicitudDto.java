package com.tp1.proyecto.academico.dto;

import jakarta.validation.constraints.NotNull;

public class SeccionPeriodoAnteriorSolicitudDto {

    @NotNull(message = "El grado es obligatorio")
    private Long gradoId;

    @NotNull(message = "El periodo academico es obligatorio")
    private Long periodoAcademicoId;

    public Long getGradoId() {
        return gradoId;
    }

    public void setGradoId(Long gradoId) {
        this.gradoId = gradoId;
    }

    public Long getPeriodoAcademicoId() {
        return periodoAcademicoId;
    }

    public void setPeriodoAcademicoId(Long periodoAcademicoId) {
        this.periodoAcademicoId = periodoAcademicoId;
    }
}
