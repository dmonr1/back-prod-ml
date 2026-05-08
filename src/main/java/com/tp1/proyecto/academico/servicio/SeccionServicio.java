package com.tp1.proyecto.academico.servicio;

import com.tp1.proyecto.academico.dto.SeccionRespuestaDto;
import com.tp1.proyecto.academico.dto.SeccionSolicitudDto;
import java.util.List;

public interface SeccionServicio {

    List<SeccionRespuestaDto> listar();

    SeccionRespuestaDto crear(SeccionSolicitudDto solicitud);
}
