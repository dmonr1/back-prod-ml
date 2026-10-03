package com.tp1.proyecto.evaluacion.servicio;

import com.tp1.proyecto.evaluacion.dto.DetalleNotaEvaluacionRespuestaDto;
import com.tp1.proyecto.evaluacion.dto.ActualizarFechaEvaluacionSolicitudDto;
import com.tp1.proyecto.evaluacion.dto.EvaluacionRespuestaDto;
import com.tp1.proyecto.evaluacion.dto.EvaluacionSolicitudDto;
import com.tp1.proyecto.evaluacion.dto.RegistroNotasEvaluacionSolicitudDto;
import com.tp1.proyecto.seguridad.servicio.UsuarioAutenticado;
import java.util.List;

public interface EvaluacionServicio {

    EvaluacionRespuestaDto crear(EvaluacionSolicitudDto solicitud, UsuarioAutenticado actor);

    EvaluacionRespuestaDto actualizarFecha(Long evaluacionId, ActualizarFechaEvaluacionSolicitudDto solicitud, UsuarioAutenticado actor);

    List<EvaluacionRespuestaDto> listarPorAsignacionYPeriodoEvaluacion(Long docenteCursoSeccionId, Long periodoEvaluacionId, UsuarioAutenticado actor);

    List<DetalleNotaEvaluacionRespuestaDto> registrarNotas(Long evaluacionId, RegistroNotasEvaluacionSolicitudDto solicitud, UsuarioAutenticado actor);

    List<DetalleNotaEvaluacionRespuestaDto> listarNotasPorEvaluacion(Long evaluacionId, UsuarioAutenticado actor);
}
