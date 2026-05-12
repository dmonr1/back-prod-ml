package com.tp1.proyecto.academico.dto;

public class AsignacionDocenteSolicitudDto {

    private Long docenteId;
    private Long cursoId;
    private Long seccionId;
    private Long periodoAcademicoId;

    public Long getDocenteId() {
        return docenteId;
    }

    public void setDocenteId(Long docenteId) {
        this.docenteId = docenteId;
    }

    public Long getCursoId() {
        return cursoId;
    }

    public void setCursoId(Long cursoId) {
        this.cursoId = cursoId;
    }

    public Long getSeccionId() {
        return seccionId;
    }

    public void setSeccionId(Long seccionId) {
        this.seccionId = seccionId;
    }

    public Long getPeriodoAcademicoId() {
        return periodoAcademicoId;
    }

    public void setPeriodoAcademicoId(Long periodoAcademicoId) {
        this.periodoAcademicoId = periodoAcademicoId;
    }
}
