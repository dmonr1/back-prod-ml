package com.tp1.proyecto.seguridad.dto;

import jakarta.validation.constraints.NotBlank;

public class RecuperacionVerificarSolicitudDto {

    @NotBlank
    private String identificador;

    @NotBlank
    private String codigo;

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
}
