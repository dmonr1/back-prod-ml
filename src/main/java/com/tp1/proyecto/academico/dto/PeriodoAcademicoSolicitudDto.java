package com.tp1.proyecto.academico.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;

public class PeriodoAcademicoSolicitudDto {

    @NotBlank(message = "El nombre del periodo es obligatorio")
    @Size(max = 100, message = "El nombre del periodo no debe exceder 100 caracteres")
    private String nombre;

    @NotNull(message = "El anio es obligatorio")
    @Min(value = 2000, message = "El anio debe ser valido")
    @Max(value = 2100, message = "El anio debe ser valido")
    private Integer anio;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate fechaFin;

    @NotBlank(message = "El tipo de periodo de evaluacion es obligatorio")
    @Pattern(
        regexp = "BIMESTRAL|TRIMESTRAL|SEMESTRAL|ANUAL",
        message = "El tipo de periodo de evaluacion debe ser BIMESTRAL, TRIMESTRAL, SEMESTRAL o ANUAL"
    )
    private String tipoPeriodoEvaluacion;

    @NotNull @Min(30) @Max(180)
    private Integer duracionHoraPrimariaMinutos = 50;
    @NotNull @Min(5) @Max(60)
    private Integer duracionRecreoPrimariaMinutos = 20;
    @NotNull
    private LocalTime horaInicioJornadaPrimaria = LocalTime.of(7, 0);
    @NotNull
    private LocalTime horaFinJornadaPrimaria = LocalTime.of(18, 0);
    @NotNull @Min(30) @Max(180)
    private Integer duracionHoraSecundariaMinutos = 90;
    @NotNull @Min(5) @Max(60)
    private Integer duracionRecreoSecundariaMinutos = 20;
    @NotNull
    private LocalTime horaInicioJornadaSecundaria = LocalTime.of(7, 0);
    @NotNull
    private LocalTime horaFinJornadaSecundaria = LocalTime.of(18, 0);

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
