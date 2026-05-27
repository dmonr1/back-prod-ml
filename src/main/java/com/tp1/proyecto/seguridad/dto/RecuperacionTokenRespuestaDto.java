package com.tp1.proyecto.seguridad.dto;

public class RecuperacionTokenRespuestaDto {

    private String mensaje;
    private String tokenRecuperacion;

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getTokenRecuperacion() {
        return tokenRecuperacion;
    }

    public void setTokenRecuperacion(String tokenRecuperacion) {
        this.tokenRecuperacion = tokenRecuperacion;
    }
}
