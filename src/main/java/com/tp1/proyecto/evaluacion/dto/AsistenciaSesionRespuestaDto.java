package com.tp1.proyecto.evaluacion.dto;

import com.tp1.proyecto.evaluacion.enumeracion.EstadoAsistenciaSesion;
import java.time.LocalDate;

public class AsistenciaSesionRespuestaDto {

    private Long id;
    private Long matriculaId;
    private Long periodoEvaluacionId;
    private Long horarioSemanalId;
    private LocalDate fechaClase;
    private EstadoAsistenciaSesion estado;
    private String observacion;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMatriculaId() { return matriculaId; }
    public void setMatriculaId(Long matriculaId) { this.matriculaId = matriculaId; }
    public Long getPeriodoEvaluacionId() { return periodoEvaluacionId; }
    public void setPeriodoEvaluacionId(Long periodoEvaluacionId) { this.periodoEvaluacionId = periodoEvaluacionId; }
    public Long getHorarioSemanalId() { return horarioSemanalId; }
    public void setHorarioSemanalId(Long horarioSemanalId) { this.horarioSemanalId = horarioSemanalId; }
    public LocalDate getFechaClase() { return fechaClase; }
    public void setFechaClase(LocalDate fechaClase) { this.fechaClase = fechaClase; }
    public EstadoAsistenciaSesion getEstado() { return estado; }
    public void setEstado(EstadoAsistenciaSesion estado) { this.estado = estado; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
}
