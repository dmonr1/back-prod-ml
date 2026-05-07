package com.tp1.proyecto.prediccion.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public class PrediccionMlRequestDto {

    @JsonProperty("modelo_version")
    private String modeloVersion;

    @JsonProperty("global_features")
    private PrediccionGlobalMlRequestDto globalFeatures;

    @JsonProperty("course_features")
    private List<PrediccionCursoMlDto> courseFeatures = new ArrayList<>();

    public String getModeloVersion() {
        return modeloVersion;
    }

    public void setModeloVersion(String modeloVersion) {
        this.modeloVersion = modeloVersion;
    }

    public PrediccionGlobalMlRequestDto getGlobalFeatures() {
        return globalFeatures;
    }

    public void setGlobalFeatures(PrediccionGlobalMlRequestDto globalFeatures) {
        this.globalFeatures = globalFeatures;
    }

    public List<PrediccionCursoMlDto> getCourseFeatures() {
        return courseFeatures;
    }

    public void setCourseFeatures(List<PrediccionCursoMlDto> courseFeatures) {
        this.courseFeatures = courseFeatures;
    }
}
