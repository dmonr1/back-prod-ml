package com.tp1.proyecto.seguridad.dto;

public class MensajeRespuestaDto {

    private String mensaje;

    public MensajeRespuestaDto() {
    }

    public MensajeRespuestaDto(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
