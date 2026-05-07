package com.tp1.proyecto.seguridad.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginSolicitudDto {

    @NotBlank
    private String identificador;

    @NotBlank
    private String password;

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
