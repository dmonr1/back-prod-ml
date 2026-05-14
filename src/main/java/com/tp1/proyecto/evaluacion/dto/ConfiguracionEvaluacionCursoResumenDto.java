package com.tp1.proyecto.evaluacion.dto;

public class ConfiguracionEvaluacionCursoResumenDto {

    private Long cursoId;
    private String nombreCurso;
    private String descripcionCurso;
    private String nivelNombre;
    private Boolean usaConfiguracionPersonalizada;
    private Integer totalTiposConfigurados;

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

    public Integer getTotalTiposConfigurados() {
        return totalTiposConfigurados;
    }

    public void setTotalTiposConfigurados(Integer totalTiposConfigurados) {
        this.totalTiposConfigurados = totalTiposConfigurados;
    }
}
