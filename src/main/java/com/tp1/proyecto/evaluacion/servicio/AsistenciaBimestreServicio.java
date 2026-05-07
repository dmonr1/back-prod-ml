package com.tp1.proyecto.evaluacion.servicio;

import com.tp1.proyecto.evaluacion.dto.AsistenciaBimestreRespuestaDto;
import com.tp1.proyecto.evaluacion.dto.RegistroAsistenciasBimestreSolicitudDto;
import java.util.List;

public interface AsistenciaBimestreServicio {

    List<AsistenciaBimestreRespuestaDto> registrarAsistencias(Long bimestreId, RegistroAsistenciasBimestreSolicitudDto solicitud);

    List<AsistenciaBimestreRespuestaDto> listarPorSeccionYBimestre(Long seccionId, Long periodoAcademicoId, Long bimestreId);
}
