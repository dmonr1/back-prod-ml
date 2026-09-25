package com.tp1.proyecto.alerta.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

public class AlertaAcademicaRespuestaDto {
    private Long id;
    private String tipo;
    private String titulo;
    private String curso;
    private String seccion;
    private String grado;
    private String docente;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fechaReferencia;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaLimite;
    private Integer cantidadPendiente;
    private String estado;
    private Long asignacionId;
    private Long horarioId;
    private Long evaluacionId;
    private Long periodoEvaluacionId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getCurso() { return curso; }
    public void setCurso(String curso) { this.curso = curso; }
    public String getSeccion() { return seccion; }
    public void setSeccion(String seccion) { this.seccion = seccion; }
    public String getGrado() { return grado; }
    public void setGrado(String grado) { this.grado = grado; }
    public String getDocente() { return docente; }
    public void setDocente(String docente) { this.docente = docente; }
    public LocalDate getFechaReferencia() { return fechaReferencia; }
    public void setFechaReferencia(LocalDate fechaReferencia) { this.fechaReferencia = fechaReferencia; }
    public LocalDateTime getFechaLimite() { return fechaLimite; }
    public void setFechaLimite(LocalDateTime fechaLimite) { this.fechaLimite = fechaLimite; }
    public Integer getCantidadPendiente() { return cantidadPendiente; }
    public void setCantidadPendiente(Integer cantidadPendiente) { this.cantidadPendiente = cantidadPendiente; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Long getAsignacionId() { return asignacionId; }
    public void setAsignacionId(Long asignacionId) { this.asignacionId = asignacionId; }
    public Long getHorarioId() { return horarioId; }
    public void setHorarioId(Long horarioId) { this.horarioId = horarioId; }
    public Long getEvaluacionId() { return evaluacionId; }
    public void setEvaluacionId(Long evaluacionId) { this.evaluacionId = evaluacionId; }
    public Long getPeriodoEvaluacionId() { return periodoEvaluacionId; }
    public void setPeriodoEvaluacionId(Long periodoEvaluacionId) { this.periodoEvaluacionId = periodoEvaluacionId; }
}
