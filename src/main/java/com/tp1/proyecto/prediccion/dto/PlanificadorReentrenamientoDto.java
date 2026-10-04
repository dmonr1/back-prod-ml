package com.tp1.proyecto.prediccion.dto;

public class PlanificadorReentrenamientoDto {
    private String cadencia;
    private String proximaEjecucionProgramada;
    private String ultimoReentrenamiento;
    private String estadoUltimoReentrenamiento;
    private Integer registrosEntrenamiento;
    private String modeloActualVersion;
    private String modoReentrenamiento;
    private String mensaje;

    public PlanificadorReentrenamientoDto() {}

    public PlanificadorReentrenamientoDto(
        String cadencia,
        String proximaEjecucionProgramada,
        String ultimoReentrenamiento,
        String estadoUltimoReentrenamiento,
        Integer registrosEntrenamiento,
        String modeloActualVersion,
        String modoReentrenamiento,
        String mensaje
    ) {
        this.cadencia = cadencia;
        this.proximaEjecucionProgramada = proximaEjecucionProgramada;
        this.ultimoReentrenamiento = ultimoReentrenamiento;
        this.estadoUltimoReentrenamiento = estadoUltimoReentrenamiento;
        this.registrosEntrenamiento = registrosEntrenamiento;
        this.modeloActualVersion = modeloActualVersion;
        this.modoReentrenamiento = modoReentrenamiento;
        this.mensaje = mensaje;
    }

    public String getCadencia() {
        return cadencia;
    }

    public void setCadencia(String cadencia) {
        this.cadencia = cadencia;
    }

    public String getProximaEjecucionProgramada() {
        return proximaEjecucionProgramada;
    }

    public void setProximaEjecucionProgramada(String proximaEjecucionProgramada) {
        this.proximaEjecucionProgramada = proximaEjecucionProgramada;
    }

    public String getUltimoReentrenamiento() {
        return ultimoReentrenamiento;
    }

    public void setUltimoReentrenamiento(String ultimoReentrenamiento) {
        this.ultimoReentrenamiento = ultimoReentrenamiento;
    }

    public String getEstadoUltimoReentrenamiento() {
        return estadoUltimoReentrenamiento;
    }

    public void setEstadoUltimoReentrenamiento(String estadoUltimoReentrenamiento) {
        this.estadoUltimoReentrenamiento = estadoUltimoReentrenamiento;
    }

    public Integer getRegistrosEntrenamiento() {
        return registrosEntrenamiento;
    }

    public void setRegistrosEntrenamiento(Integer registrosEntrenamiento) {
        this.registrosEntrenamiento = registrosEntrenamiento;
    }

    public String getModeloActualVersion() {
        return modeloActualVersion;
    }

    public void setModeloActualVersion(String modeloActualVersion) {
        this.modeloActualVersion = modeloActualVersion;
    }

    public String getModoReentrenamiento() {
        return modoReentrenamiento;
    }

    public void setModoReentrenamiento(String modoReentrenamiento) {
        this.modoReentrenamiento = modoReentrenamiento;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
