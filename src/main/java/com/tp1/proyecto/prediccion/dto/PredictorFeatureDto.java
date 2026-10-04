package com.tp1.proyecto.prediccion.dto;

public class PredictorFeatureDto {
    private String key;
    private String label;
    private String tipo; // GLOBAL, CURSO
    private String categoria; // ACADEMICO, ASISTENCIA, EVALUATIVO
    private String descripcion;
    private Boolean activo;
    private Double peso;

    public PredictorFeatureDto() {}

    public PredictorFeatureDto(String key, String label, String tipo, String categoria, String descripcion, Boolean activo, Double peso) {
        this.key = key;
        this.label = label;
        this.tipo = tipo;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.activo = activo;
        this.peso = peso;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Double getPeso() {
        return peso;
    }

    public void setPeso(Double peso) {
        this.peso = peso;
    }
}
