package com.tp1.proyecto.academico.servicio;

import com.tp1.proyecto.academico.dto.AlumnoSeccionRespuestaDto;
import com.tp1.proyecto.academico.dto.AsignacionDocenteSolicitudDto;
import com.tp1.proyecto.academico.dto.AsignacionDocenteRespuestaDto;
import com.tp1.proyecto.academico.dto.TutoriaRespuestaDto;
import com.tp1.proyecto.academico.dto.TutoriaSolicitudDto;
import java.util.List;

public interface AsignacionAcademicaServicio {

    AsignacionDocenteRespuestaDto crearAsignacionDocente(AsignacionDocenteSolicitudDto solicitud);

    TutoriaRespuestaDto crearTutoria(TutoriaSolicitudDto solicitud);

    List<AsignacionDocenteRespuestaDto> listarAsignacionesPorPeriodo(Long periodoAcademicoId);

    List<AsignacionDocenteRespuestaDto> listarAsignacionesDocente(Long docenteId, Long periodoAcademicoId);

    List<TutoriaRespuestaDto> listarTutoriasPorPeriodo(Long periodoAcademicoId);

    List<AlumnoSeccionRespuestaDto> listarAlumnosPorSeccion(Long seccionId, Long periodoAcademicoId);
}
