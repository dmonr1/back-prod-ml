package com.tp1.proyecto.prediccion.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PrediccionCursoMlDto {

    @JsonProperty("matricula_id")
    private Long matriculaId;

    @JsonProperty("curso_id")
    private Long cursoId;

    @JsonProperty("curso_nombre")
    private String cursoNombre;

    @JsonProperty("periodo_evaluacion_id")
    private Long periodoEvaluacionId;

    @JsonProperty("nota_curso")
    private Double notaCurso;

    @JsonProperty("promedio_general")
    private Double promedioGeneral;

    @JsonProperty("cantidad_cursos_desaprobados")
    private Integer cantidadCursosDesaprobados;

    @JsonProperty("porcentaje_asistencia")
    private Double porcentajeAsistencia;

    @JsonProperty("cantidad_evaluaciones_registradas")
    private Integer cantidadEvaluacionesRegistradas;

    public Long getMatriculaId() {
        return matriculaId;
    }

    public void setMatriculaId(Long matriculaId) {
        this.matriculaId = matriculaId;
    }

    public Long getCursoId() {
        return cursoId;
    }

    public void setCursoId(Long cursoId) {
        this.cursoId = cursoId;
    }

    public String getCursoNombre() {
        return cursoNombre;
    }

    public void setCursoNombre(String cursoNombre) {
        this.cursoNombre = cursoNombre;
    }

    public Long getPeriodoEvaluacionId() {
        return periodoEvaluacionId;
    }

    public void setPeriodoEvaluacionId(Long periodoEvaluacionId) {
        this.periodoEvaluacionId = periodoEvaluacionId;
    }

    public Double getNotaCurso() {
        return notaCurso;
    }

    public void setNotaCurso(Double notaCurso) {
        this.notaCurso = notaCurso;
    }

    public Double getPromedioGeneral() {
        return promedioGeneral;
    }

    public void setPromedioGeneral(Double promedioGeneral) {
        this.promedioGeneral = promedioGeneral;
    }

    public Integer getCantidadCursosDesaprobados() {
        return cantidadCursosDesaprobados;
    }

    public void setCantidadCursosDesaprobados(Integer cantidadCursosDesaprobados) {
        this.cantidadCursosDesaprobados = cantidadCursosDesaprobados;
    }

    public Double getPorcentajeAsistencia() {
        return porcentajeAsistencia;
    }

    public void setPorcentajeAsistencia(Double porcentajeAsistencia) {
        this.porcentajeAsistencia = porcentajeAsistencia;
    }

    public Integer getCantidadEvaluacionesRegistradas() {
        return cantidadEvaluacionesRegistradas;
    }

    public void setCantidadEvaluacionesRegistradas(Integer cantidadEvaluacionesRegistradas) {
        this.cantidadEvaluacionesRegistradas = cantidadEvaluacionesRegistradas;
    }
}
