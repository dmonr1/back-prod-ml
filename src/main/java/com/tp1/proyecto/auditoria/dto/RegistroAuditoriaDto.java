package com.tp1.proyecto.auditoria.dto;

import java.time.LocalDateTime;

public class RegistroAuditoriaDto {
    private String id;
    private String modulo;
    private String tipoEvento;
    private String descripcion;
    private String entidadAfectada;
    private String usuarioUsername;
    private String usuarioNombre;
    private String nivelCriticidad; // CRITICO, ADVERTENCIA, INFO
    private LocalDateTime fechaEvento;
    private String detalleAdicional;

    public RegistroAuditoriaDto() {}

    public RegistroAuditoriaDto(
        String id,
        String modulo,
        String tipoEvento,
        String descripcion,
        String entidadAfectada,
        String usuarioUsername,
        String usuarioNombre,
        String nivelCriticidad,
        LocalDateTime fechaEvento,
        String detalleAdicional
    ) {
        this.id = id;
        this.modulo = modulo;
        this.tipoEvento = tipoEvento;
        this.descripcion = descripcion;
        this.entidadAfectada = entidadAfectada;
        this.usuarioUsername = usuarioUsername;
        this.usuarioNombre = usuarioNombre;
        this.nivelCriticidad = nivelCriticidad;
        this.fechaEvento = fechaEvento;
        this.detalleAdicional = detalleAdicional;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEntidadAfectada() {
        return entidadAfectada;
    }

    public void setEntidadAfectada(String entidadAfectada) {
        this.entidadAfectada = entidadAfectada;
    }

    public String getUsuarioUsername() {
        return usuarioUsername;
    }

    public void setUsuarioUsername(String usuarioUsername) {
        this.usuarioUsername = usuarioUsername;
    }

    public String getUsuarioNombre() {
        return usuarioNombre;
    }

    public void setUsuarioNombre(String usuarioNombre) {
        this.usuarioNombre = usuarioNombre;
    }

    public String getNivelCriticidad() {
        return nivelCriticidad;
    }

    public void setNivelCriticidad(String nivelCriticidad) {
        this.nivelCriticidad = nivelCriticidad;
    }

    public LocalDateTime getFechaEvento() {
        return fechaEvento;
    }

    public void setFechaEvento(LocalDateTime fechaEvento) {
        this.fechaEvento = fechaEvento;
    }

    public String getDetalleAdicional() {
        return detalleAdicional;
    }

    public void setDetalleAdicional(String detalleAdicional) {
        this.detalleAdicional = detalleAdicional;
    }
}
