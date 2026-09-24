package com.tp1.proyecto.evaluacion.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class ActualizarFechaEvaluacionSolicitudDto {

    @NotNull
    private LocalDate fechaEvaluacion;

    public LocalDate getFechaEvaluacion() {
        return fechaEvaluacion;
    }

    public void setFechaEvaluacion(LocalDate fechaEvaluacion) {
        this.fechaEvaluacion = fechaEvaluacion;
    }
}
