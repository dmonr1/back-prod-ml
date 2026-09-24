package com.tp1.proyecto.academico.dto;

import java.time.LocalTime;

public class BloqueHorarioRespuestaDto {
    private Long id;
    private Long periodoAcademicoId;
    private Long nivelId;
    private String nivel;
    private String nombre;
    private Short orden;
    private LocalTime horaInicio;
    private LocalTime horaFin;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPeriodoAcademicoId() { return periodoAcademicoId; }
    public void setPeriodoAcademicoId(Long periodoAcademicoId) { this.periodoAcademicoId = periodoAcademicoId; }
    public Long getNivelId() { return nivelId; }
    public void setNivelId(Long nivelId) { this.nivelId = nivelId; }
    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Short getOrden() { return orden; }
    public void setOrden(Short orden) { this.orden = orden; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }
}
