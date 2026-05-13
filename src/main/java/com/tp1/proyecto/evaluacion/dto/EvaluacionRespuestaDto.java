package com.tp1.proyecto.evaluacion.dto;

import java.time.LocalDate;

public class EvaluacionRespuestaDto {

    private Long id;
    private Long configuracionEvaluacionId;
    private Long docenteCursoSeccionId;
    private Long periodoEvaluacionId;
    private String nombrePeriodoEvaluacion;
    private Long tipoEvaluacionId;
    private String tipoEvaluacion;
    private Integer numeroEvaluacion;
    private String nombre;
    private LocalDate fechaEvaluacion;
    private String curso;
    private String seccion;
    private String grado;
    private String nivel;
    private String estado;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getConfiguracionEvaluacionId() {
        return configuracionEvaluacionId;
    }

    public void setConfiguracionEvaluacionId(Long configuracionEvaluacionId) {
        this.configuracionEvaluacionId = configuracionEvaluacionId;
    }

    public Long getDocenteCursoSeccionId() {
        return docenteCursoSeccionId;
    }

    public void setDocenteCursoSeccionId(Long docenteCursoSeccionId) {
        this.docenteCursoSeccionId = docenteCursoSeccionId;
    }

    public Long getPeriodoEvaluacionId() {
        return periodoEvaluacionId;
    }

    public void setPeriodoEvaluacionId(Long periodoEvaluacionId) {
        this.periodoEvaluacionId = periodoEvaluacionId;
    }

    public String getNombrePeriodoEvaluacion() {
        return nombrePeriodoEvaluacion;
    }

    public void setNombrePeriodoEvaluacion(String nombrePeriodoEvaluacion) {
        this.nombrePeriodoEvaluacion = nombrePeriodoEvaluacion;
    }

    public Long getTipoEvaluacionId() {
        return tipoEvaluacionId;
    }

    public void setTipoEvaluacionId(Long tipoEvaluacionId) {
        this.tipoEvaluacionId = tipoEvaluacionId;
    }

    public String getTipoEvaluacion() {
        return tipoEvaluacion;
    }

    public void setTipoEvaluacion(String tipoEvaluacion) {
        this.tipoEvaluacion = tipoEvaluacion;
    }

    public Integer getNumeroEvaluacion() {
        return numeroEvaluacion;
    }

    public void setNumeroEvaluacion(Integer numeroEvaluacion) {
        this.numeroEvaluacion = numeroEvaluacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDate getFechaEvaluacion() {
        return fechaEvaluacion;
    }

    public void setFechaEvaluacion(LocalDate fechaEvaluacion) {
        this.fechaEvaluacion = fechaEvaluacion;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public String getSeccion() {
        return seccion;
    }

    public void setSeccion(String seccion) {
        this.seccion = seccion;
    }

    public String getGrado() {
        return grado;
    }

    public void setGrado(String grado) {
        this.grado = grado;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
