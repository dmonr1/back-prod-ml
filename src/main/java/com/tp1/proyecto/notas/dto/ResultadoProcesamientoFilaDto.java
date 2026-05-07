package com.tp1.proyecto.notas.dto;

public class ResultadoProcesamientoFilaDto {

    private int numeroFila;
    private boolean valida;
    private String mensaje;

    public ResultadoProcesamientoFilaDto(int numeroFila, boolean valida, String mensaje) {
        this.numeroFila = numeroFila;
        this.valida = valida;
        this.mensaje = mensaje;
    }

    public int getNumeroFila() {
        return numeroFila;
    }

    public boolean isValida() {
        return valida;
    }

    public String getMensaje() {
        return mensaje;
    }
}
