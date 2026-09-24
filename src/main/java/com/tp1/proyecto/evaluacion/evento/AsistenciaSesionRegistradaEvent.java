package com.tp1.proyecto.evaluacion.evento;

import java.util.List;

public final class AsistenciaSesionRegistradaEvent {
    private final Long periodoEvaluacionId;
    private final List<Long> matriculaIds;

    public AsistenciaSesionRegistradaEvent(Long periodoEvaluacionId, List<Long> matriculaIds) {
        this.periodoEvaluacionId = periodoEvaluacionId;
        this.matriculaIds = List.copyOf(matriculaIds);
    }

    public Long periodoEvaluacionId() { return periodoEvaluacionId; }
    public List<Long> matriculaIds() { return matriculaIds; }
}
