package com.tp1.proyecto.evaluacion.dto;

public class AsistenciaPeriodoEvaluacionRespuestaDto {

    private Long id;
    private Long matriculaId;
    private Long alumnoId;
    private String codigoAlumno;
    private String alumnoNombreCompleto;
    private Long periodoEvaluacionId;
    private Integer clasesProgramadas;
    private Integer clasesAsistidas;
    private Double porcentajeAsistencia;
    private String observacion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMatriculaId() {
        return matriculaId;
    }

    public void setMatriculaId(Long matriculaId) {
        this.matriculaId = matriculaId;
    }

    public Long getAlumnoId() {
        return alumnoId;
    }

    public void setAlumnoId(Long alumnoId) {
        this.alumnoId = alumnoId;
    }

    public String getCodigoAlumno() {
        return codigoAlumno;
    }

    public void setCodigoAlumno(String codigoAlumno) {
        this.codigoAlumno = codigoAlumno;
    }

    public String getAlumnoNombreCompleto() {
        return alumnoNombreCompleto;
    }

    public void setAlumnoNombreCompleto(String alumnoNombreCompleto) {
        this.alumnoNombreCompleto = alumnoNombreCompleto;
    }

    public Long getPeriodoEvaluacionId() {
        return periodoEvaluacionId;
    }

    public void setPeriodoEvaluacionId(Long periodoEvaluacionId) {
        this.periodoEvaluacionId = periodoEvaluacionId;
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

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}
