package com.tp1.proyecto.academico.servicio;

import com.tp1.proyecto.academico.dto.BimestreRespuestaDto;
import com.tp1.proyecto.academico.dto.BimestreSolicitudDto;
import java.util.List;

public interface BimestreServicio {

    List<BimestreRespuestaDto> listar();

    BimestreRespuestaDto crear(BimestreSolicitudDto solicitud);
}
