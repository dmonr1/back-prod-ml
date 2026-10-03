package com.tp1.proyecto.evaluacion.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public final class PlanificacionEvaluacionSolicitudDto {

    private PlanificacionEvaluacionSolicitudDto() {
    }

    public record NuevoTipo(
        @NotNull Long asignacionId,
        @NotNull Long periodoEvaluacionId,
        @NotBlank String nombre,
        String descripcion,
        @NotEmpty List<LocalDate> fechas
    ) {
    }

    public record Agregar(
        @NotNull Long asignacionId,
        @NotNull Long periodoEvaluacionId,
        @NotNull Long tipoEvaluacionId,
        @NotNull LocalDate fecha
    ) {
    }

    public record Cantidad(
        @NotNull Long asignacionId,
        @NotNull Long periodoEvaluacionId,
        @NotNull Long tipoEvaluacionId,
        @NotNull @Min(0) @Max(99) Integer cantidad
    ) {
    }
}
