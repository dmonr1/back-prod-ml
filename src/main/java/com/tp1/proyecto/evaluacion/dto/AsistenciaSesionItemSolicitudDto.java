package com.tp1.proyecto.evaluacion.dto;

import com.tp1.proyecto.evaluacion.enumeracion.EstadoAsistenciaSesion;
import jakarta.validation.constraints.NotNull;

public class AsistenciaSesionItemSolicitudDto {

    @NotNull
    private Long matriculaId;

    @NotNull
    private EstadoAsistenciaSesion estado;

    private String observacion;

    public Long getMatriculaId() { return matriculaId; }
    public void setMatriculaId(Long matriculaId) { this.matriculaId = matriculaId; }
    public EstadoAsistenciaSesion getEstado() { return estado; }
    public void setEstado(EstadoAsistenciaSesion estado) { this.estado = estado; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
}
