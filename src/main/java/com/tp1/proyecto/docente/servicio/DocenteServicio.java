package com.tp1.proyecto.docente.servicio;

import com.tp1.proyecto.docente.dto.DocenteRegistroSolicitudDto;
import com.tp1.proyecto.docente.dto.DocenteRespuestaDto;
import java.util.List;

public interface DocenteServicio {

    List<DocenteRespuestaDto> listar();

    DocenteRespuestaDto crear(DocenteRegistroSolicitudDto solicitud);
}
