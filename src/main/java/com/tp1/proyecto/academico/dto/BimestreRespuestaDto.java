package com.tp1.proyecto.academico.dto;

import java.time.LocalDate;

public class BimestreRespuestaDto {

    private Long id;
    private String nombre;
    private Short numero;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String estado;
    private Long periodoAcademicoId;
    private String periodoAcademicoNombre;
    private Integer anioAcademico;

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

    public Short getNumero() {
        return numero;
    }

    public void setNumero(Short numero) {
        this.numero = numero;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Long getPeriodoAcademicoId() {
        return periodoAcademicoId;
    }

    public void setPeriodoAcademicoId(Long periodoAcademicoId) {
        this.periodoAcademicoId = periodoAcademicoId;
    }

    public String getPeriodoAcademicoNombre() {
        return periodoAcademicoNombre;
    }

    public void setPeriodoAcademicoNombre(String periodoAcademicoNombre) {
        this.periodoAcademicoNombre = periodoAcademicoNombre;
    }

    public Integer getAnioAcademico() {
        return anioAcademico;
    }

    public void setAnioAcademico(Integer anioAcademico) {
        this.anioAcademico = anioAcademico;
    }
}
