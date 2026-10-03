package com.tp1.proyecto.academico.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class PeriodoAcademicoRespuestaDto {

    private Long id;
    private String nombre;
    private Integer anio;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String tipoPeriodoEvaluacion;
    private String estado;
    private Integer duracionHoraPrimariaMinutos;
    private Integer duracionRecreoPrimariaMinutos;
    private Integer duracionHoraSecundariaMinutos;
    private Integer duracionRecreoSecundariaMinutos;
    private LocalTime horaInicioJornadaPrimaria;
    private LocalTime horaFinJornadaPrimaria;
    private LocalTime horaInicioJornadaSecundaria;
    private LocalTime horaFinJornadaSecundaria;

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

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
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

    public String getTipoPeriodoEvaluacion() {
        return tipoPeriodoEvaluacion;
    }

    public void setTipoPeriodoEvaluacion(String tipoPeriodoEvaluacion) {
        this.tipoPeriodoEvaluacion = tipoPeriodoEvaluacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Integer getDuracionHoraPrimariaMinutos() { return duracionHoraPrimariaMinutos; }
    public void setDuracionHoraPrimariaMinutos(Integer value) { this.duracionHoraPrimariaMinutos = value; }
    public Integer getDuracionRecreoPrimariaMinutos() { return duracionRecreoPrimariaMinutos; }
    public void setDuracionRecreoPrimariaMinutos(Integer value) { this.duracionRecreoPrimariaMinutos = value; }
    public Integer getDuracionHoraSecundariaMinutos() { return duracionHoraSecundariaMinutos; }
    public void setDuracionHoraSecundariaMinutos(Integer value) { this.duracionHoraSecundariaMinutos = value; }
    public Integer getDuracionRecreoSecundariaMinutos() { return duracionRecreoSecundariaMinutos; }
    public void setDuracionRecreoSecundariaMinutos(Integer value) { this.duracionRecreoSecundariaMinutos = value; }
    public LocalTime getHoraInicioJornadaPrimaria() { return horaInicioJornadaPrimaria; }
    public void setHoraInicioJornadaPrimaria(LocalTime value) { this.horaInicioJornadaPrimaria = value; }
    public LocalTime getHoraFinJornadaPrimaria() { return horaFinJornadaPrimaria; }
    public void setHoraFinJornadaPrimaria(LocalTime value) { this.horaFinJornadaPrimaria = value; }
    public LocalTime getHoraInicioJornadaSecundaria() { return horaInicioJornadaSecundaria; }
    public void setHoraInicioJornadaSecundaria(LocalTime value) { this.horaInicioJornadaSecundaria = value; }
    public LocalTime getHoraFinJornadaSecundaria() { return horaFinJornadaSecundaria; }
    public void setHoraFinJornadaSecundaria(LocalTime value) { this.horaFinJornadaSecundaria = value; }
}
