package com.tp1.proyecto.academico.servicio;

import com.tp1.proyecto.academico.dto.PeriodoAcademicoConPeriodosRespuestaDto;
import com.tp1.proyecto.academico.dto.PeriodoAcademicoConPeriodosSolicitudDto;
import com.tp1.proyecto.academico.dto.PeriodoAcademicoRespuestaDto;
import com.tp1.proyecto.academico.dto.PeriodoAcademicoSolicitudDto;
import java.util.List;

public interface PeriodoAcademicoServicio {

    List<PeriodoAcademicoRespuestaDto> listar();

    PeriodoAcademicoRespuestaDto crear(PeriodoAcademicoSolicitudDto solicitud);

    PeriodoAcademicoConPeriodosRespuestaDto crearConPeriodosEvaluacion(PeriodoAcademicoConPeriodosSolicitudDto solicitud);
}
