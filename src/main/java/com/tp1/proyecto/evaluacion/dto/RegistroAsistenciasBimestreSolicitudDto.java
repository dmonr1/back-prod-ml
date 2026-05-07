package com.tp1.proyecto.evaluacion.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class RegistroAsistenciasBimestreSolicitudDto {

    @Valid
    @NotEmpty
    private List<AsistenciaBimestreSolicitudDto> asistencias;

    public List<AsistenciaBimestreSolicitudDto> getAsistencias() {
        return asistencias;
    }

    public void setAsistencias(List<AsistenciaBimestreSolicitudDto> asistencias) {
        this.asistencias = asistencias;
    }
}
