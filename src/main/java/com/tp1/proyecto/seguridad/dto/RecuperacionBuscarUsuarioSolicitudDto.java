package com.tp1.proyecto.seguridad.dto;

import jakarta.validation.constraints.NotBlank;

public class RecuperacionBuscarUsuarioSolicitudDto {

    @NotBlank
    private String identificador;

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }
}
