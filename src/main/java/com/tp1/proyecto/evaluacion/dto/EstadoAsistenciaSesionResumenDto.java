package com.tp1.proyecto.evaluacion.dto;

import java.time.LocalDate;

public class EstadoAsistenciaSesionResumenDto {
    private Long asignacionId;
    private Long horarioSemanalId;
    private LocalDate fechaClase;
    private int registradas;
    private int total;

    public Long getAsignacionId() { return asignacionId; }
    public void setAsignacionId(Long asignacionId) { this.asignacionId = asignacionId; }
    public Long getHorarioSemanalId() { return horarioSemanalId; }
    public void setHorarioSemanalId(Long horarioSemanalId) { this.horarioSemanalId = horarioSemanalId; }
    public LocalDate getFechaClase() { return fechaClase; }
    public void setFechaClase(LocalDate fechaClase) { this.fechaClase = fechaClase; }
    public int getRegistradas() { return registradas; }
    public void setRegistradas(int registradas) { this.registradas = registradas; }
    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }
}
