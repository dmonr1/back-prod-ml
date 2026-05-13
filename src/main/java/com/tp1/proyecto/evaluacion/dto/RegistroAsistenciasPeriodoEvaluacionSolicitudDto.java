package com.tp1.proyecto.evaluacion.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class RegistroAsistenciasPeriodoEvaluacionSolicitudDto {

    @Valid
    @NotEmpty
    private List<AsistenciaPeriodoEvaluacionSolicitudDto> asistencias;

    public List<AsistenciaPeriodoEvaluacionSolicitudDto> getAsistencias() {
        return asistencias;
    }

    public void setAsistencias(List<AsistenciaPeriodoEvaluacionSolicitudDto> asistencias) {
        this.asistencias = asistencias;
    }
}
