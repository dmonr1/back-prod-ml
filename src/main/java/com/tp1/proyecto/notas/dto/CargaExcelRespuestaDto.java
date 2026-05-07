package com.tp1.proyecto.notas.dto;

import java.time.LocalDateTime;

public class CargaExcelRespuestaDto {

    private Long id;
    private Long docenteId;
    private String docenteNombreCompleto;
    private Long periodoAcademicoId;
    private Long bimestreId;
    private Long seccionId;
    private String nombreArchivo;
    private Integer totalFilas;
    private Integer filasValidas;
    private Integer filasError;
    private String estadoProceso;
    private String observacion;
    private LocalDateTime fechaCarga;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDocenteId() {
        return docenteId;
    }

    public void setDocenteId(Long docenteId) {
        this.docenteId = docenteId;
    }

    public String getDocenteNombreCompleto() {
        return docenteNombreCompleto;
    }

    public void setDocenteNombreCompleto(String docenteNombreCompleto) {
        this.docenteNombreCompleto = docenteNombreCompleto;
    }

    public Long getPeriodoAcademicoId() {
        return periodoAcademicoId;
    }

    public void setPeriodoAcademicoId(Long periodoAcademicoId) {
        this.periodoAcademicoId = periodoAcademicoId;
    }

    public Long getBimestreId() {
        return bimestreId;
    }

    public void setBimestreId(Long bimestreId) {
        this.bimestreId = bimestreId;
    }

    public Long getSeccionId() {
        return seccionId;
    }

    public void setSeccionId(Long seccionId) {
        this.seccionId = seccionId;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public Integer getTotalFilas() {
        return totalFilas;
    }

    public void setTotalFilas(Integer totalFilas) {
        this.totalFilas = totalFilas;
    }

    public Integer getFilasValidas() {
        return filasValidas;
    }

    public void setFilasValidas(Integer filasValidas) {
        this.filasValidas = filasValidas;
    }

    public Integer getFilasError() {
        return filasError;
    }

    public void setFilasError(Integer filasError) {
        this.filasError = filasError;
    }

    public String getEstadoProceso() {
        return estadoProceso;
    }

    public void setEstadoProceso(String estadoProceso) {
        this.estadoProceso = estadoProceso;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public LocalDateTime getFechaCarga() {
        return fechaCarga;
    }

    public void setFechaCarga(LocalDateTime fechaCarga) {
        this.fechaCarga = fechaCarga;
    }
}
