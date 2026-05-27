package com.tp1.proyecto.seguridad.dto;

import jakarta.validation.constraints.NotBlank;

public class RecuperacionCambiarPasswordSolicitudDto {

    @NotBlank
    private String tokenRecuperacion;

    @NotBlank
    private String nuevaPassword;

    @NotBlank
    private String confirmarPassword;

    public String getTokenRecuperacion() {
        return tokenRecuperacion;
    }

    public void setTokenRecuperacion(String tokenRecuperacion) {
        this.tokenRecuperacion = tokenRecuperacion;
    }

    public String getNuevaPassword() {
        return nuevaPassword;
    }

    public void setNuevaPassword(String nuevaPassword) {
        this.nuevaPassword = nuevaPassword;
    }

    public String getConfirmarPassword() {
        return confirmarPassword;
    }

    public void setConfirmarPassword(String confirmarPassword) {
        this.confirmarPassword = confirmarPassword;
    }
}
