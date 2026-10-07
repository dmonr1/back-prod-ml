package com.tp1.proyecto.prediccion.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

public class PlanificadorReentrenamientoDto {
    private String cadencia;
    @JsonAlias("proxima_ejecucion_programada")
    private String proximaEjecucionProgramada;
    @JsonAlias("ultimo_reentrenamiento")
    private String ultimoReentrenamiento;
    @JsonAlias("estado_ultimo_reentrenamiento")
    private String estadoUltimoReentrenamiento;
    @JsonAlias("registros_entrenamiento")
    private Integer registrosEntrenamiento;
    @JsonAlias("modelo_actual_version")
    private String modeloActualVersion;
    @JsonAlias("modo_reentrenamiento")
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
