package com.tp1.proyecto.academico.dto;

import java.time.LocalDate;

public class CorteSeguimientoRespuestaDto {
    private Long id;
    private Long periodoAcademicoId;
    private Integer semana;
    private LocalDate fechaCorte;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPeriodoAcademicoId() { return periodoAcademicoId; }
    public void setPeriodoAcademicoId(Long periodoAcademicoId) { this.periodoAcademicoId = periodoAcademicoId; }
    public Integer getSemana() { return semana; }
    public void setSemana(Integer semana) { this.semana = semana; }
    public LocalDate getFechaCorte() { return fechaCorte; }
    public void setFechaCorte(LocalDate fechaCorte) { this.fechaCorte = fechaCorte; }
}
