package com.tp1.proyecto.academico.servicio;

import com.tp1.proyecto.academico.dto.CursoRespuestaDto;
import com.tp1.proyecto.academico.dto.CursoSolicitudDto;
import java.util.List;

public interface CursoServicio {

    List<CursoRespuestaDto> listar();

    CursoRespuestaDto obtenerPorId(Long id);

    CursoRespuestaDto crear(CursoSolicitudDto solicitud);

    CursoRespuestaDto actualizar(Long id, CursoSolicitudDto solicitud);

    CursoRespuestaDto actualizarEstado(Long cursoId, boolean activo);
}
