package com.tp1.proyecto.prediccion.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class PrediccionMlResponseDto {

    @JsonProperty("global_prediction")
    private PrediccionGlobalMlResponseDto globalPrediction;

    @JsonProperty("course_predictions")
    private List<PrediccionCursoMlResponseDto> coursePredictions;

    @JsonProperty("pattern_summary")
    private String patternSummary;

    public PrediccionGlobalMlResponseDto getGlobalPrediction() {
        return globalPrediction;
    }

    public void setGlobalPrediction(PrediccionGlobalMlResponseDto globalPrediction) {
        this.globalPrediction = globalPrediction;
    }

    public List<PrediccionCursoMlResponseDto> getCoursePredictions() {
        return coursePredictions;
    }

    public void setCoursePredictions(List<PrediccionCursoMlResponseDto> coursePredictions) {
        this.coursePredictions = coursePredictions;
    }

    public String getPatternSummary() {
        return patternSummary;
    }

    public void setPatternSummary(String patternSummary) {
        this.patternSummary = patternSummary;
    }
}
