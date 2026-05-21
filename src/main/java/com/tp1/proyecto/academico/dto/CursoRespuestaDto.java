package com.tp1.proyecto.academico.dto;

public class CursoRespuestaDto {

    private Long id;
    private String nombre;
    private String descripcion;
    private String portadaColor;
    private String portadaIcono;
    private String portadaImagen;
    private Long nivelId;
    private String nivelNombre;
    private String estado;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getPortadaColor() {
        return portadaColor;
    }

    public void setPortadaColor(String portadaColor) {
        this.portadaColor = portadaColor;
    }

    public String getPortadaIcono() {
        return portadaIcono;
    }

    public void setPortadaIcono(String portadaIcono) {
        this.portadaIcono = portadaIcono;
    }

    public String getPortadaImagen() {
        return portadaImagen;
    }

    public void setPortadaImagen(String portadaImagen) {
        this.portadaImagen = portadaImagen;
    }

    public Long getNivelId() {
        return nivelId;
    }

    public void setNivelId(Long nivelId) {
        this.nivelId = nivelId;
    }

    public String getNivelNombre() {
        return nivelNombre;
    }

    public void setNivelNombre(String nivelNombre) {
        this.nivelNombre = nivelNombre;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
