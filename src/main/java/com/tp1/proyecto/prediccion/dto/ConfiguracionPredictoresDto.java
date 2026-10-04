package com.tp1.proyecto.prediccion.dto;

import java.util.List;

public class ConfiguracionPredictoresDto {
    private List<PredictorFeatureDto> globalFeatures;
    private List<PredictorFeatureDto> courseFeatures;
    private Integer totalActivos;
    private String ultimaActualizacion;

    public ConfiguracionPredictoresDto() {}

    public ConfiguracionPredictoresDto(
        List<PredictorFeatureDto> globalFeatures,
        List<PredictorFeatureDto> courseFeatures,
        Integer totalActivos,
        String ultimaActualizacion
    ) {
        this.globalFeatures = globalFeatures;
        this.courseFeatures = courseFeatures;
        this.totalActivos = totalActivos;
        this.ultimaActualizacion = ultimaActualizacion;
    }

    public List<PredictorFeatureDto> getGlobalFeatures() {
        return globalFeatures;
    }

    public void setGlobalFeatures(List<PredictorFeatureDto> globalFeatures) {
        this.globalFeatures = globalFeatures;
    }

    public List<PredictorFeatureDto> getCourseFeatures() {
        return courseFeatures;
    }

    public void setCourseFeatures(List<PredictorFeatureDto> courseFeatures) {
        this.courseFeatures = courseFeatures;
    }

    public Integer getTotalActivos() {
        return totalActivos;
    }

    public void setTotalActivos(Integer totalActivos) {
        this.totalActivos = totalActivos;
    }

    public String getUltimaActualizacion() {
        return ultimaActualizacion;
    }

    public void setUltimaActualizacion(String ultimaActualizacion) {
        this.ultimaActualizacion = ultimaActualizacion;
    }
}
