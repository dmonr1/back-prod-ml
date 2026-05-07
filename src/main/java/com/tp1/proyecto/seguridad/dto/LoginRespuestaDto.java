package com.tp1.proyecto.seguridad.dto;

public class LoginRespuestaDto {

    private String token;
    private String tipoToken;
    private Long expiracionSegundos;
    private UsuarioSesionDto usuario;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTipoToken() {
        return tipoToken;
    }

    public void setTipoToken(String tipoToken) {
        this.tipoToken = tipoToken;
    }

    public Long getExpiracionSegundos() {
        return expiracionSegundos;
    }

    public void setExpiracionSegundos(Long expiracionSegundos) {
        this.expiracionSegundos = expiracionSegundos;
    }

    public UsuarioSesionDto getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioSesionDto usuario) {
        this.usuario = usuario;
    }
}
