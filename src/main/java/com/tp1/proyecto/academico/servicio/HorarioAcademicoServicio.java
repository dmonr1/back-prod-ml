package com.tp1.proyecto.academico.servicio;

import com.tp1.proyecto.academico.dto.BloqueHorarioRespuestaDto;
import com.tp1.proyecto.academico.dto.BloqueHorarioSolicitudDto;
import com.tp1.proyecto.academico.dto.HorarioSemanalRespuestaDto;
import com.tp1.proyecto.academico.dto.HorarioSemanalSolicitudDto;
import java.util.List;

public interface HorarioAcademicoServicio {
    List<BloqueHorarioRespuestaDto> listarBloques(Long periodoAcademicoId, Long nivelId);
    BloqueHorarioRespuestaDto crearBloque(BloqueHorarioSolicitudDto solicitud);
    BloqueHorarioRespuestaDto actualizarBloque(Long id, BloqueHorarioSolicitudDto solicitud);
    void actualizarEstadoBloque(Long id, boolean activo);
    List<HorarioSemanalRespuestaDto> listarPorPeriodo(Long periodoAcademicoId);
    List<HorarioSemanalRespuestaDto> listarMios(Long periodoAcademicoId, Long usuarioId);
    HorarioSemanalRespuestaDto crearHorario(HorarioSemanalSolicitudDto solicitud);
    void actualizarEstadoHorario(Long id, boolean activo);
}
