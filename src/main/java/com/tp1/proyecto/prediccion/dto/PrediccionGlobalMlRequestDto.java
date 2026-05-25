package com.tp1.proyecto.prediccion.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PrediccionGlobalMlRequestDto {

    @JsonProperty("matricula_id")
    private Long matriculaId;

    @JsonProperty("periodo_evaluacion_id")
    private Long periodoEvaluacionId;

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

    @JsonProperty("cantidad_evaluaciones_registradas")
    private Integer cantidadEvaluacionesRegistradas;

    @JsonProperty("cantidad_notas_desaprobadas_total")
    private Integer cantidadNotasDesaprobadasTotal;

    @JsonProperty("cantidad_notas_criticas_total")
    private Integer cantidadNotasCriticasTotal;

    @JsonProperty("peor_nota_periodo")
    private Double peorNotaPeriodo;

    @JsonProperty("cantidad_cursos_c")
    private Integer cantidadCursosC;

    @JsonProperty("cantidad_cursos_b")
    private Integer cantidadCursosB;

    @JsonProperty("cantidad_cursos_a")
    private Integer cantidadCursosA;

    @JsonProperty("cantidad_cursos_ad")
    private Integer cantidadCursosAd;

    public Long getMatriculaId() { return matriculaId; }
    public void setMatriculaId(Long matriculaId) { this.matriculaId = matriculaId; }
    public Long getPeriodoEvaluacionId() { return periodoEvaluacionId; }
    public void setPeriodoEvaluacionId(Long periodoEvaluacionId) { this.periodoEvaluacionId = periodoEvaluacionId; }
    public Double getPromedioGeneral() { return promedioGeneral; }
    public void setPromedioGeneral(Double promedioGeneral) { this.promedioGeneral = promedioGeneral; }
    public Integer getCantidadCursos() { return cantidadCursos; }
    public void setCantidadCursos(Integer cantidadCursos) { this.cantidadCursos = cantidadCursos; }
    public Integer getCantidadCursosDesaprobados() { return cantidadCursosDesaprobados; }
    public void setCantidadCursosDesaprobados(Integer cantidadCursosDesaprobados) { this.cantidadCursosDesaprobados = cantidadCursosDesaprobados; }
    public Double getNotaMaxima() { return notaMaxima; }
    public void setNotaMaxima(Double notaMaxima) { this.notaMaxima = notaMaxima; }
    public Double getNotaMinima() { return notaMinima; }
    public void setNotaMinima(Double notaMinima) { this.notaMinima = notaMinima; }
    public Integer getClasesProgramadas() { return clasesProgramadas; }
    public void setClasesProgramadas(Integer clasesProgramadas) { this.clasesProgramadas = clasesProgramadas; }
    public Integer getClasesAsistidas() { return clasesAsistidas; }
    public void setClasesAsistidas(Integer clasesAsistidas) { this.clasesAsistidas = clasesAsistidas; }
    public Double getPorcentajeAsistencia() { return porcentajeAsistencia; }
    public void setPorcentajeAsistencia(Double porcentajeAsistencia) { this.porcentajeAsistencia = porcentajeAsistencia; }
    public Integer getCantidadEvaluacionesRegistradas() { return cantidadEvaluacionesRegistradas; }
    public void setCantidadEvaluacionesRegistradas(Integer cantidadEvaluacionesRegistradas) { this.cantidadEvaluacionesRegistradas = cantidadEvaluacionesRegistradas; }
    public Integer getCantidadNotasDesaprobadasTotal() { return cantidadNotasDesaprobadasTotal; }
    public void setCantidadNotasDesaprobadasTotal(Integer cantidadNotasDesaprobadasTotal) { this.cantidadNotasDesaprobadasTotal = cantidadNotasDesaprobadasTotal; }
    public Integer getCantidadNotasCriticasTotal() { return cantidadNotasCriticasTotal; }
    public void setCantidadNotasCriticasTotal(Integer cantidadNotasCriticasTotal) { this.cantidadNotasCriticasTotal = cantidadNotasCriticasTotal; }
    public Double getPeorNotaPeriodo() { return peorNotaPeriodo; }
    public void setPeorNotaPeriodo(Double peorNotaPeriodo) { this.peorNotaPeriodo = peorNotaPeriodo; }
    public Integer getCantidadCursosC() { return cantidadCursosC; }
    public void setCantidadCursosC(Integer cantidadCursosC) { this.cantidadCursosC = cantidadCursosC; }
    public Integer getCantidadCursosB() { return cantidadCursosB; }
    public void setCantidadCursosB(Integer cantidadCursosB) { this.cantidadCursosB = cantidadCursosB; }
    public Integer getCantidadCursosA() { return cantidadCursosA; }
    public void setCantidadCursosA(Integer cantidadCursosA) { this.cantidadCursosA = cantidadCursosA; }
    public Integer getCantidadCursosAd() { return cantidadCursosAd; }
    public void setCantidadCursosAd(Integer cantidadCursosAd) { this.cantidadCursosAd = cantidadCursosAd; }
}
