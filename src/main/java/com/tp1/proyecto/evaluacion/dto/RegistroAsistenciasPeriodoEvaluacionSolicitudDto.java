package com.tp1.proyecto.evaluacion.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class RegistroAsistenciasPeriodoEvaluacionSolicitudDto {

    @NotNull
    private Long docenteCursoSeccionId;

    @Valid
    @NotEmpty
    private List<AsistenciaPeriodoEvaluacionSolicitudDto> asistencias;

    public Long getDocenteCursoSeccionId() {
        return docenteCursoSeccionId;
    }

    public void setDocenteCursoSeccionId(Long docenteCursoSeccionId) {
        this.docenteCursoSeccionId = docenteCursoSeccionId;
    }

    public List<AsistenciaPeriodoEvaluacionSolicitudDto> getAsistencias() {
        return asistencias;
    }

    public void setAsistencias(List<AsistenciaPeriodoEvaluacionSolicitudDto> asistencias) {
        this.asistencias = asistencias;
    }
}
