package com.tp1.proyecto.academico.servicio;

import com.tp1.proyecto.academico.dto.SeccionPeriodoAnteriorSolicitudDto;
import com.tp1.proyecto.academico.dto.SeccionRespuestaDto;
import com.tp1.proyecto.academico.dto.SeccionSolicitudDto;
import java.util.List;

public interface SeccionServicio {

    List<SeccionRespuestaDto> listar(Long periodoAcademicoId);

    SeccionRespuestaDto crear(SeccionSolicitudDto solicitud);

    List<SeccionRespuestaDto> copiarDesdePeriodoAnterior(SeccionPeriodoAnteriorSolicitudDto solicitud);

    SeccionRespuestaDto actualizarEstado(Long seccionId, boolean activa);
}
