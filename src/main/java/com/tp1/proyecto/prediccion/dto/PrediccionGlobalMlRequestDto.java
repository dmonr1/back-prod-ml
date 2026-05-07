package com.tp1.proyecto.prediccion.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PrediccionGlobalMlRequestDto {

    @JsonProperty("matricula_id")
    private Long matriculaId;

    @JsonProperty("bimestre_id")
    private Long bimestreId;

    @JsonProperty("promedio_general")
    private Double promedioGeneral;

    @JsonProperty("cantidad_cursos")
    private Integer cantidadCursos;

    @JsonProperty("cantidad_cursos_desaprobados")
    private Integer cantidadCursosDesaprobados;

    @JsonProperty("nota_maxima")
    private Double notaMaxima;

    @JsonProperty("nota_minima")
    private Double notaMinima;

    @JsonProperty("clases_programadas")
    private Integer clasesProgramadas;

    @JsonProperty("clases_asistidas")
    private Integer clasesAsistidas;

    @JsonProperty("porcentaje_asistencia")
    private Double porcentajeAsistencia;

    public Long getMatriculaId() {
        return matriculaId;
    }

    public void setMatriculaId(Long matriculaId) {
        this.matriculaId = matriculaId;
    }

    public Long getBimestreId() {
        return bimestreId;
    }

    public void setBimestreId(Long bimestreId) {
        this.bimestreId = bimestreId;
    }

    public Double getPromedioGeneral() {
        return promedioGeneral;
    }

    public void setPromedioGeneral(Double promedioGeneral) {
        this.promedioGeneral = promedioGeneral;
    }

    public Integer getCantidadCursos() {
        return cantidadCursos;
    }

    public void setCantidadCursos(Integer cantidadCursos) {
        this.cantidadCursos = cantidadCursos;
    }

    public Integer getCantidadCursosDesaprobados() {
        return cantidadCursosDesaprobados;
    }

    public void setCantidadCursosDesaprobados(Integer cantidadCursosDesaprobados) {
        this.cantidadCursosDesaprobados = cantidadCursosDesaprobados;
    }

    public Double getNotaMaxima() {
        return notaMaxima;
    }

    public void setNotaMaxima(Double notaMaxima) {
        this.notaMaxima = notaMaxima;
    }

    public Double getNotaMinima() {
        return notaMinima;
    }

    public void setNotaMinima(Double notaMinima) {
        this.notaMinima = notaMinima;
    }

    public Integer getClasesProgramadas() {
        return clasesProgramadas;
    }

    public void setClasesProgramadas(Integer clasesProgramadas) {
        this.clasesProgramadas = clasesProgramadas;
    }

    public Integer getClasesAsistidas() {
        return clasesAsistidas;
    }

    public void setClasesAsistidas(Integer clasesAsistidas) {
        this.clasesAsistidas = clasesAsistidas;
    }

    public Double getPorcentajeAsistencia() {
        return porcentajeAsistencia;
    }

    public void setPorcentajeAsistencia(Double porcentajeAsistencia) {
        this.porcentajeAsistencia = porcentajeAsistencia;
    }
}
