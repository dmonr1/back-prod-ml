package com.tp1.proyecto.prediccion.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.List;

public class ConfiguracionPredictoresDto {
    @JsonAlias("global_features")
    private List<PredictorFeatureDto> globalFeatures;
    @JsonAlias("course_features")
    private List<PredictorFeatureDto> courseFeatures;
    @JsonAlias("total_activos")
    private Integer totalActivos;
    @JsonAlias("ultima_actualizacion")
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
