package com.tp1.proyecto.academico.servicio;

import com.tp1.proyecto.academico.dto.GradoRespuestaDto;
import com.tp1.proyecto.academico.dto.GradoSolicitudDto;
import java.util.List;

public interface GradoServicio {

    List<GradoRespuestaDto> listar();

    GradoRespuestaDto crear(GradoSolicitudDto solicitud);
}
