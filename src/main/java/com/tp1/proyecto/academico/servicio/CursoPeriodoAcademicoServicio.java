package com.tp1.proyecto.academico.servicio;

import com.tp1.proyecto.academico.dto.CursoPeriodoAcademicoRespuestaDto;
import com.tp1.proyecto.academico.dto.CursoPeriodoAcademicoSolicitudDto;
import com.tp1.proyecto.academico.dto.CursoPeriodoAnteriorSolicitudDto;
import java.util.List;

public interface CursoPeriodoAcademicoServicio {

    List<CursoPeriodoAcademicoRespuestaDto> listar(Long periodoAcademicoId);

    CursoPeriodoAcademicoRespuestaDto crear(CursoPeriodoAcademicoSolicitudDto solicitud);

    List<CursoPeriodoAcademicoRespuestaDto> copiarDesdePeriodoAnterior(CursoPeriodoAnteriorSolicitudDto solicitud);

    CursoPeriodoAcademicoRespuestaDto actualizarEstado(Long cursoPeriodoAcademicoId, boolean activo);
}
