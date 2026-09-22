package com.tp1.proyecto.seguridad.dto;

public class RecuperacionBuscarUsuarioRespuestaDto {

    private String mensaje;
    private String correoEnmascarado;

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getCorreoEnmascarado() {
        return correoEnmascarado;
    }

    public void setCorreoEnmascarado(String correoEnmascarado) {
        this.correoEnmascarado = correoEnmascarado;
    }
}
