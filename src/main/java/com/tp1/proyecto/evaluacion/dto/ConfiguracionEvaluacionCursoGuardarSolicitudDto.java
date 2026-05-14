package com.tp1.proyecto.evaluacion.dto;

import com.tp1.proyecto.academico.dto.ConfiguracionEvaluacionDefaultSolicitudDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class ConfiguracionEvaluacionCursoGuardarSolicitudDto {

    @NotNull
    private Long periodoAcademicoId;

    @NotNull
    private Long cursoId;

    @Valid
    @NotEmpty
    private List<ConfiguracionEvaluacionDefaultSolicitudDto> configuraciones;

    public Long getPeriodoAcademicoId() {
        return periodoAcademicoId;
    }

    public void setPeriodoAcademicoId(Long periodoAcademicoId) {
        this.periodoAcademicoId = periodoAcademicoId;
    }

    public Long getCursoId() {
        return cursoId;
    }

    public void setCursoId(Long cursoId) {
        this.cursoId = cursoId;
    }

    public List<ConfiguracionEvaluacionDefaultSolicitudDto> getConfiguraciones() {
        return configuraciones;
    }

    public void setConfiguraciones(List<ConfiguracionEvaluacionDefaultSolicitudDto> configuraciones) {
        this.configuraciones = configuraciones;
    }
}
