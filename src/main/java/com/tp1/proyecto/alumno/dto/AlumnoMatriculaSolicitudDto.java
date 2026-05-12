package com.tp1.proyecto.alumno.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class AlumnoMatriculaSolicitudDto {

    @Valid
    @NotNull(message = "Los datos del alumno son obligatorios")
    private AlumnoSolicitudDto alumno;

    @NotNull(message = "La seccion es obligatoria")
    private Long seccionId;

    @NotNull(message = "El periodo academico es obligatorio")
    private Long periodoAcademicoId;

    public AlumnoSolicitudDto getAlumno() {
        return alumno;
    }

    public void setAlumno(AlumnoSolicitudDto alumno) {
        this.alumno = alumno;
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
