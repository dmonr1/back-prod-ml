package com.tp1.proyecto.auditoria.dto;

public class EstadisticasAuditoriaDto {
    private long totalEventos;
    private long totalCriticos;
    private long totalAdvertencias;
    private long totalInformativos;
    private long totalEdicionesAsistencia;
    private long totalCargasArchivos;
    private long totalModificacionesEvaluacion;

    public EstadisticasAuditoriaDto() {}

    public EstadisticasAuditoriaDto(
        long totalEventos,
        long totalCriticos,
        long totalAdvertencias,
        long totalInformativos,
        long totalEdicionesAsistencia,
        long totalCargasArchivos,
        long totalModificacionesEvaluacion
    ) {
        this.totalEventos = totalEventos;
        this.totalCriticos = totalCriticos;
        this.totalAdvertencias = totalAdvertencias;
        this.totalInformativos = totalInformativos;
        this.totalEdicionesAsistencia = totalEdicionesAsistencia;
        this.totalCargasArchivos = totalCargasArchivos;
        this.totalModificacionesEvaluacion = totalModificacionesEvaluacion;
    }

    public long getTotalEventos() {
        return totalEventos;
    }

    public void setTotalEventos(long totalEventos) {
        this.totalEventos = totalEventos;
    }

    public long getTotalCriticos() {
        return totalCriticos;
    }

    public void setTotalCriticos(long totalCriticos) {
        this.totalCriticos = totalCriticos;
    }

    public long getTotalAdvertencias() {
        return totalAdvertencias;
    }

    public void setTotalAdvertencias(long totalAdvertencias) {
        this.totalAdvertencias = totalAdvertencias;
    }

    public long getTotalInformativos() {
        return totalInformativos;
    }

    public void setTotalInformativos(long totalInformativos) {
        this.totalInformativos = totalInformativos;
    }

    public long getTotalEdicionesAsistencia() {
        return totalEdicionesAsistencia;
    }

    public void setTotalEdicionesAsistencia(long totalEdicionesAsistencia) {
        this.totalEdicionesAsistencia = totalEdicionesAsistencia;
    }

    public long getTotalCargasArchivos() {
        return totalCargasArchivos;
    }

    public void setTotalCargasArchivos(long totalCargasArchivos) {
        this.totalCargasArchivos = totalCargasArchivos;
    }

    public long getTotalModificacionesEvaluacion() {
        return totalModificacionesEvaluacion;
    }

    public void setTotalModificacionesEvaluacion(long totalModificacionesEvaluacion) {
        this.totalModificacionesEvaluacion = totalModificacionesEvaluacion;
    }
}
