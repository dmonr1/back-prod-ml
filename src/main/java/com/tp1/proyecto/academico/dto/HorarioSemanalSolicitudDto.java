package com.tp1.proyecto.academico.dto;

import com.tp1.proyecto.academico.entidad.DiaSemana;
import jakarta.validation.constraints.NotNull;

public class HorarioSemanalSolicitudDto {
    @NotNull private Long asignacionId;
    @NotNull private Long bloqueHorarioId;
    @NotNull private DiaSemana diaSemana;

    public Long getAsignacionId() { return asignacionId; }
    public void setAsignacionId(Long asignacionId) { this.asignacionId = asignacionId; }
    public Long getBloqueHorarioId() { return bloqueHorarioId; }
    public void setBloqueHorarioId(Long bloqueHorarioId) { this.bloqueHorarioId = bloqueHorarioId; }
    public DiaSemana getDiaSemana() { return diaSemana; }
    public void setDiaSemana(DiaSemana diaSemana) { this.diaSemana = diaSemana; }
}
