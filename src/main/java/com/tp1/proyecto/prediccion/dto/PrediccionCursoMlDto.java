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

    @JsonProperty("nota_minima_curso")
    private Double notaMinimaCurso;

    @JsonProperty("nota_maxima_curso")
    private Double notaMaximaCurso;

    @JsonProperty("cantidad_notas_desaprobadas")
    private Integer cantidadNotasDesaprobadas;

    @JsonProperty("cantidad_notas_criticas")
    private Integer cantidadNotasCriticas;

    @JsonProperty("nota_examen_principal")
    private Double notaExamenPrincipal;

    @JsonProperty("cantidad_notas_c")
    private Integer cantidadNotasC;

    @JsonProperty("cantidad_notas_b")
    private Integer cantidadNotasB;

    @JsonProperty("cantidad_notas_a")
    private Integer cantidadNotasA;

    @JsonProperty("cantidad_notas_ad")
    private Integer cantidadNotasAd;

    public Long getMatriculaId() { return matriculaId; }
    public void setMatriculaId(Long matriculaId) { this.matriculaId = matriculaId; }
    public Long getCursoId() { return cursoId; }
    public void setCursoId(Long cursoId) { this.cursoId = cursoId; }
    public String getCursoNombre() { return cursoNombre; }
    public void setCursoNombre(String cursoNombre) { this.cursoNombre = cursoNombre; }
    public Long getPeriodoEvaluacionId() { return periodoEvaluacionId; }
    public void setPeriodoEvaluacionId(Long periodoEvaluacionId) { this.periodoEvaluacionId = periodoEvaluacionId; }
    public Double getNotaCurso() { return notaCurso; }
    public void setNotaCurso(Double notaCurso) { this.notaCurso = notaCurso; }
    public Double getPromedioGeneral() { return promedioGeneral; }
    public void setPromedioGeneral(Double promedioGeneral) { this.promedioGeneral = promedioGeneral; }
    public Integer getCantidadCursosDesaprobados() { return cantidadCursosDesaprobados; }
    public void setCantidadCursosDesaprobados(Integer cantidadCursosDesaprobados) { this.cantidadCursosDesaprobados = cantidadCursosDesaprobados; }
    public Double getPorcentajeAsistencia() { return porcentajeAsistencia; }
    public void setPorcentajeAsistencia(Double porcentajeAsistencia) { this.porcentajeAsistencia = porcentajeAsistencia; }
    public Integer getCantidadEvaluacionesRegistradas() { return cantidadEvaluacionesRegistradas; }
    public void setCantidadEvaluacionesRegistradas(Integer cantidadEvaluacionesRegistradas) { this.cantidadEvaluacionesRegistradas = cantidadEvaluacionesRegistradas; }
    public Double getNotaMinimaCurso() { return notaMinimaCurso; }
    public void setNotaMinimaCurso(Double notaMinimaCurso) { this.notaMinimaCurso = notaMinimaCurso; }
    public Double getNotaMaximaCurso() { return notaMaximaCurso; }
    public void setNotaMaximaCurso(Double notaMaximaCurso) { this.notaMaximaCurso = notaMaximaCurso; }
    public Integer getCantidadNotasDesaprobadas() { return cantidadNotasDesaprobadas; }
    public void setCantidadNotasDesaprobadas(Integer cantidadNotasDesaprobadas) { this.cantidadNotasDesaprobadas = cantidadNotasDesaprobadas; }
    public Integer getCantidadNotasCriticas() { return cantidadNotasCriticas; }
    public void setCantidadNotasCriticas(Integer cantidadNotasCriticas) { this.cantidadNotasCriticas = cantidadNotasCriticas; }
    public Double getNotaExamenPrincipal() { return notaExamenPrincipal; }
    public void setNotaExamenPrincipal(Double notaExamenPrincipal) { this.notaExamenPrincipal = notaExamenPrincipal; }
    public Integer getCantidadNotasC() { return cantidadNotasC; }
    public void setCantidadNotasC(Integer cantidadNotasC) { this.cantidadNotasC = cantidadNotasC; }
    public Integer getCantidadNotasB() { return cantidadNotasB; }
    public void setCantidadNotasB(Integer cantidadNotasB) { this.cantidadNotasB = cantidadNotasB; }
    public Integer getCantidadNotasA() { return cantidadNotasA; }
    public void setCantidadNotasA(Integer cantidadNotasA) { this.cantidadNotasA = cantidadNotasA; }
    public Integer getCantidadNotasAd() { return cantidadNotasAd; }
    public void setCantidadNotasAd(Integer cantidadNotasAd) { this.cantidadNotasAd = cantidadNotasAd; }
}
