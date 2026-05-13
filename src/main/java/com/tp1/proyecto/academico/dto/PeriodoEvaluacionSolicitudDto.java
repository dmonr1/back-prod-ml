package com.tp1.proyecto.academico.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class PeriodoEvaluacionSolicitudDto {

    @NotNull(message = "El periodo academico es obligatorio")
    private Long periodoAcademicoId;

    @NotBlank(message = "El nombre del periodoEvaluacion es obligatorio")
    @Size(max = 50, message = "El nombre del periodoEvaluacion no debe exceder 50 caracteres")
    private String nombre;

    @NotNull(message = "El numero del periodoEvaluacion es obligatorio")
    @Min(value = 1, message = "El numero del periodo de evaluacion debe ser mayor a 0")
    private Short numero;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate fechaFin;

    public Long getPeriodoAcademicoId() {
        return periodoAcademicoId;
    }

    public void setPeriodoAcademicoId(Long periodoAcademicoId) {
        this.periodoAcademicoId = periodoAcademicoId;
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
}
