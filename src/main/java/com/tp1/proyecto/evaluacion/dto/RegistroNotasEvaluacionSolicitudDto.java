package com.tp1.proyecto.evaluacion.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class RegistroNotasEvaluacionSolicitudDto {

    @Valid
    @NotEmpty
    private List<DetalleNotaEvaluacionSolicitudDto> notas;

    public List<DetalleNotaEvaluacionSolicitudDto> getNotas() {
        return notas;
    }

    public void setNotas(List<DetalleNotaEvaluacionSolicitudDto> notas) {
        this.notas = notas;
    }
}
