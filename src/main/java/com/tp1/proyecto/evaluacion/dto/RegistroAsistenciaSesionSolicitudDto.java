package com.tp1.proyecto.evaluacion.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public class RegistroAsistenciaSesionSolicitudDto {

    @NotNull
    private Long docenteCursoSeccionId;

    private Long horarioSemanalId;

    @NotNull
    private Long periodoEvaluacionId;

    @NotNull
    private LocalDate fechaClase;

    @Valid
    @NotEmpty
    private List<AsistenciaSesionItemSolicitudDto> asistencias;

    public Long getDocenteCursoSeccionId() { return docenteCursoSeccionId; }
    public void setDocenteCursoSeccionId(Long docenteCursoSeccionId) { this.docenteCursoSeccionId = docenteCursoSeccionId; }
    public Long getHorarioSemanalId() { return horarioSemanalId; }
    public void setHorarioSemanalId(Long horarioSemanalId) { this.horarioSemanalId = horarioSemanalId; }
    public Long getPeriodoEvaluacionId() { return periodoEvaluacionId; }
    public void setPeriodoEvaluacionId(Long periodoEvaluacionId) { this.periodoEvaluacionId = periodoEvaluacionId; }
    public LocalDate getFechaClase() { return fechaClase; }
    public void setFechaClase(LocalDate fechaClase) { this.fechaClase = fechaClase; }
    public List<AsistenciaSesionItemSolicitudDto> getAsistencias() { return asistencias; }
    public void setAsistencias(List<AsistenciaSesionItemSolicitudDto> asistencias) { this.asistencias = asistencias; }
}
