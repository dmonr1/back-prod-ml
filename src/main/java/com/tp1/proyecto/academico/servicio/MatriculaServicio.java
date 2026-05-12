package com.tp1.proyecto.academico.servicio;

import com.tp1.proyecto.academico.dto.MatriculaRespuestaDto;
import com.tp1.proyecto.academico.dto.MatriculaSolicitudDto;
import java.util.List;

public interface MatriculaServicio {

    List<MatriculaRespuestaDto> listar(Long periodoAcademicoId, Long seccionId);

    MatriculaRespuestaDto crear(MatriculaSolicitudDto solicitud);
}
