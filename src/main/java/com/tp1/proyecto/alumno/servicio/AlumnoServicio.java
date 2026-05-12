package com.tp1.proyecto.alumno.servicio;

import com.tp1.proyecto.academico.dto.MatriculaRespuestaDto;
import com.tp1.proyecto.alumno.dto.AlumnoMatriculaSolicitudDto;
import com.tp1.proyecto.alumno.dto.AlumnoRespuestaDto;
import com.tp1.proyecto.alumno.dto.AlumnoSolicitudDto;
import java.util.List;

public interface AlumnoServicio {

    List<AlumnoRespuestaDto> listar();

    AlumnoRespuestaDto obtenerPorId(Long id);

    AlumnoRespuestaDto crear(AlumnoSolicitudDto solicitud);

    AlumnoRespuestaDto actualizar(Long id, AlumnoSolicitudDto solicitud);

    MatriculaRespuestaDto crearYMatricular(AlumnoMatriculaSolicitudDto solicitud);
}
