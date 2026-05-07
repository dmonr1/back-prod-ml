package com.tp1.proyecto.prediccion.dto;

import java.math.BigDecimal;

public class ResumenPrediccionDto {

    private Long bimestreId;
    private Long seccionId;
    private String nivel;
    private String grado;
    private String seccion;
    private Integer totalPredicciones;
    private Integer totalRiesgoAlto;
    private Integer totalRiesgoMedio;
    private Integer totalRiesgoBajo;
    private BigDecimal promedioPuntajeRiesgo;

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

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public String getGrado() {
        return grado;
    }

    public void setGrado(String grado) {
        this.grado = grado;
    }

    public String getSeccion() {
        return seccion;
    }

    public void setSeccion(String seccion) {
        this.seccion = seccion;
    }

    public Integer getTotalPredicciones() {
        return totalPredicciones;
    }

    public void setTotalPredicciones(Integer totalPredicciones) {
        this.totalPredicciones = totalPredicciones;
    }

    public Integer getTotalRiesgoAlto() {
        return totalRiesgoAlto;
    }

    public void setTotalRiesgoAlto(Integer totalRiesgoAlto) {
        this.totalRiesgoAlto = totalRiesgoAlto;
    }

    public Integer getTotalRiesgoMedio() {
        return totalRiesgoMedio;
    }

    public void setTotalRiesgoMedio(Integer totalRiesgoMedio) {
        this.totalRiesgoMedio = totalRiesgoMedio;
    }

    public Integer getTotalRiesgoBajo() {
        return totalRiesgoBajo;
    }

    public void setTotalRiesgoBajo(Integer totalRiesgoBajo) {
        this.totalRiesgoBajo = totalRiesgoBajo;
    }

    public BigDecimal getPromedioPuntajeRiesgo() {
        return promedioPuntajeRiesgo;
    }

    public void setPromedioPuntajeRiesgo(BigDecimal promedioPuntajeRiesgo) {
        this.promedioPuntajeRiesgo = promedioPuntajeRiesgo;
    }
}
