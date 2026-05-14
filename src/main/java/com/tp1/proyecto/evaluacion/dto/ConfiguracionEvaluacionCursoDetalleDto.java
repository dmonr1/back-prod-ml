package com.tp1.proyecto.evaluacion.dto;

import java.util.List;

public class ConfiguracionEvaluacionCursoDetalleDto {

    private Long periodoAcademicoId;
    private Long cursoId;
    private String nombreCurso;
    private String descripcionCurso;
    private String nivelNombre;
    private Boolean usaConfiguracionPersonalizada;
    private List<ConfiguracionEvaluacionCursoItemDto> configuraciones;

    public Long getPeriodoAcademicoId() {
        return periodoAcademicoId;
    }

    public void setPeriodoAcademicoId(Long periodoAcademicoId) {
        this.periodoAcademicoId = periodoAcademicoId;
    }

    public Long getCursoId() {
        return cursoId;
    }

    public void setCursoId(Long cursoId) {
        this.cursoId = cursoId;
    }

    public String getNombreCurso() {
        return nombreCurso;
    }

    public void setNombreCurso(String nombreCurso) {
        this.nombreCurso = nombreCurso;
    }

    public String getDescripcionCurso() {
        return descripcionCurso;
    }

    public void setDescripcionCurso(String descripcionCurso) {
        this.descripcionCurso = descripcionCurso;
    }

    public String getNivelNombre() {
        return nivelNombre;
    }

    public void setNivelNombre(String nivelNombre) {
        this.nivelNombre = nivelNombre;
    }

    public Boolean getUsaConfiguracionPersonalizada() {
        return usaConfiguracionPersonalizada;
    }

    public void setUsaConfiguracionPersonalizada(Boolean usaConfiguracionPersonalizada) {
        this.usaConfiguracionPersonalizada = usaConfiguracionPersonalizada;
    }

    public List<ConfiguracionEvaluacionCursoItemDto> getConfiguraciones() {
        return configuraciones;
    }

    public void setConfiguraciones(List<ConfiguracionEvaluacionCursoItemDto> configuraciones) {
        this.configuraciones = configuraciones;
    }
}
