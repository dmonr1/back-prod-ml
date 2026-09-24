package com.tp1.proyecto.academico.dto;

import com.tp1.proyecto.academico.entidad.DiaSemana;
import java.time.LocalTime;

public class HorarioSemanalRespuestaDto {
    private Long id;
    private Long asignacionId;
    private Long docenteId;
    private String docente;
    private Long cursoId;
    private String curso;
    private Long seccionId;
    private String seccion;
    private String grado;
    private String nivel;
    private Long periodoAcademicoId;
    private Long bloqueHorarioId;
    private String bloque;
    private Short ordenBloque;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private DiaSemana diaSemana;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getAsignacionId() { return asignacionId; }
    public void setAsignacionId(Long asignacionId) { this.asignacionId = asignacionId; }
    public Long getDocenteId() { return docenteId; }
    public void setDocenteId(Long docenteId) { this.docenteId = docenteId; }
    public String getDocente() { return docente; }
    public void setDocente(String docente) { this.docente = docente; }
    public Long getCursoId() { return cursoId; }
    public void setCursoId(Long cursoId) { this.cursoId = cursoId; }
    public String getCurso() { return curso; }
    public void setCurso(String curso) { this.curso = curso; }
    public Long getSeccionId() { return seccionId; }
    public void setSeccionId(Long seccionId) { this.seccionId = seccionId; }
    public String getSeccion() { return seccion; }
    public void setSeccion(String seccion) { this.seccion = seccion; }
    public String getGrado() { return grado; }
    public void setGrado(String grado) { this.grado = grado; }
    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }
    public Long getPeriodoAcademicoId() { return periodoAcademicoId; }
    public void setPeriodoAcademicoId(Long periodoAcademicoId) { this.periodoAcademicoId = periodoAcademicoId; }
    public Long getBloqueHorarioId() { return bloqueHorarioId; }
    public void setBloqueHorarioId(Long bloqueHorarioId) { this.bloqueHorarioId = bloqueHorarioId; }
    public String getBloque() { return bloque; }
    public void setBloque(String bloque) { this.bloque = bloque; }
    public Short getOrdenBloque() { return ordenBloque; }
    public void setOrdenBloque(Short ordenBloque) { this.ordenBloque = ordenBloque; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }
    public DiaSemana getDiaSemana() { return diaSemana; }
    public void setDiaSemana(DiaSemana diaSemana) { this.diaSemana = diaSemana; }
}
