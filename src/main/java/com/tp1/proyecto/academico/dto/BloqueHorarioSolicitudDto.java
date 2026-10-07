package com.tp1.proyecto.academico.dto;

import com.tp1.proyecto.academico.enumeracion.TipoBloqueHorario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalTime;

public class BloqueHorarioSolicitudDto {
    @NotNull private Long periodoAcademicoId;
    @NotNull private Long nivelId;
    @NotBlank private String nombre;
    @NotNull @Positive private Short orden;
    @NotNull private LocalTime horaInicio;
    @NotNull private LocalTime horaFin;
    private boolean esRecreo;
    private TipoBloqueHorario tipoBloque;

    public Long getPeriodoAcademicoId() { return periodoAcademicoId; }
    public void setPeriodoAcademicoId(Long periodoAcademicoId) { this.periodoAcademicoId = periodoAcademicoId; }
    public Long getNivelId() { return nivelId; }
    public void setNivelId(Long nivelId) { this.nivelId = nivelId; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Short getOrden() { return orden; }
    public void setOrden(Short orden) { this.orden = orden; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }
    public boolean isEsRecreo() { return esRecreo; }
    public void setEsRecreo(boolean esRecreo) { this.esRecreo = esRecreo; }
    public TipoBloqueHorario getTipoBloque() { return tipoBloque; }
    public void setTipoBloque(TipoBloqueHorario tipoBloque) { this.tipoBloque = tipoBloque; }
}
