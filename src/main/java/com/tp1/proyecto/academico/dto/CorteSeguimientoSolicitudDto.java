package com.tp1.proyecto.academico.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class CorteSeguimientoSolicitudDto {
    @NotNull
    @Min(1)
    private Integer semana;

    @NotNull
    private LocalDate fechaCorte;

    public Integer getSemana() { return semana; }
    public void setSemana(Integer semana) { this.semana = semana; }
    public LocalDate getFechaCorte() { return fechaCorte; }
    public void setFechaCorte(LocalDate fechaCorte) { this.fechaCorte = fechaCorte; }
}
