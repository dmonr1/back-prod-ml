package com.tp1.proyecto.evaluacion.evento;

import java.time.LocalDate;
import java.util.List;

public final class AsistenciaSesionRegistradaEvent {
    private final Long periodoEvaluacionId;
    private final List<Long> matriculaIds;
    private final Long asignacionId;
    private final Long horarioSemanalId;
    private final LocalDate fechaClase;

    public AsistenciaSesionRegistradaEvent(Long periodoEvaluacionId, List<Long> matriculaIds) {
        this(periodoEvaluacionId, matriculaIds, null, null, null);
    }

    public AsistenciaSesionRegistradaEvent(
        Long periodoEvaluacionId,
        List<Long> matriculaIds,
        Long asignacionId,
        Long horarioSemanalId,
        LocalDate fechaClase
    ) {
        this.periodoEvaluacionId = periodoEvaluacionId;
        this.matriculaIds = List.copyOf(matriculaIds);
        this.asignacionId = asignacionId;
        this.horarioSemanalId = horarioSemanalId;
        this.fechaClase = fechaClase;
    }

    public Long periodoEvaluacionId() { return periodoEvaluacionId; }
    public List<Long> matriculaIds() { return matriculaIds; }
    public Long asignacionId() { return asignacionId; }
    public Long horarioSemanalId() { return horarioSemanalId; }
    public LocalDate fechaClase() { return fechaClase; }
}
