package com.tp1.proyecto.academico.dto;

public class GradoRespuestaDto {

    private Long id;
    private String nombre;
    private Short orden;
    private String estado;
    private Long nivelId;
    private String nivelNombre;
    private int totalSecciones;

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

    public Short getOrden() {
        return orden;
    }

    public void setOrden(Short orden) {
        this.orden = orden;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
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

    public int getTotalSecciones() {
        return totalSecciones;
    }

    public void setTotalSecciones(int totalSecciones) {
        this.totalSecciones = totalSecciones;
    }
}
