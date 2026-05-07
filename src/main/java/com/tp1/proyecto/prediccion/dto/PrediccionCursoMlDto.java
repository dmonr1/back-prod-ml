package com.tp1.proyecto.prediccion.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PrediccionCursoMlDto {

    @JsonProperty("matricula_id")
    private Long matriculaId;

    @JsonProperty("curso_id")
    private Long cursoId;

    @JsonProperty("curso_nombre")
    private String cursoNombre;

    @JsonProperty("bimestre_id")
    private Long bimestreId;

    @JsonProperty("nota_curso")
    private Double notaCurso;

    @JsonProperty("promedio_general")
    private Double promedioGeneral;

    @JsonProperty("cantidad_cursos_desaprobados")
    private Integer cantidadCursosDesaprobados;

    @JsonProperty("porcentaje_asistencia")
    private Double porcentajeAsistencia;

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

    public Long getBimestreId() {
        return bimestreId;
    }

    public void setBimestreId(Long bimestreId) {
        this.bimestreId = bimestreId;
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
}
