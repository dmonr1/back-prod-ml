package com.tp1.proyecto.usuario.dto;

import java.util.ArrayList;
import java.util.List;

public class UsuarioGestionRespuestaDto {

    private Long id;
    private String username;
    private String correo;
    private String estado;
    private Boolean debeCambiarPassword;
    private Long docenteId;
    private String docenteNombreCompleto;
    private List<String> roles = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Boolean getDebeCambiarPassword() {
        return debeCambiarPassword;
    }

    public void setDebeCambiarPassword(Boolean debeCambiarPassword) {
        this.debeCambiarPassword = debeCambiarPassword;
    }

    public Long getDocenteId() {
        return docenteId;
    }

    public void setDocenteId(Long docenteId) {
        this.docenteId = docenteId;
    }

    public String getDocenteNombreCompleto() {
        return docenteNombreCompleto;
    }

    public void setDocenteNombreCompleto(String docenteNombreCompleto) {
        this.docenteNombreCompleto = docenteNombreCompleto;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
