package com.tp1.proyecto.alerta.servicio.impl;

import com.tp1.proyecto.alerta.dto.AlertaRespuestaDto;
import com.tp1.proyecto.alerta.dto.RecomendacionRespuestaDto;
import com.tp1.proyecto.alerta.entidad.Alerta;
import com.tp1.proyecto.alerta.entidad.Recomendacion;
import com.tp1.proyecto.alerta.repositorio.AlertaRepositorio;
import com.tp1.proyecto.alerta.repositorio.RecomendacionRepositorio;
import com.tp1.proyecto.alerta.servicio.AlertaSeguimientoServicio;
import com.tp1.proyecto.excepcion.RecursoNoEncontradoException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AlertaSeguimientoServicioImpl implements AlertaSeguimientoServicio {

    private final AlertaRepositorio alertaRepositorio;
    private final RecomendacionRepositorio recomendacionRepositorio;

    public AlertaSeguimientoServicioImpl(
        AlertaRepositorio alertaRepositorio,
        RecomendacionRepositorio recomendacionRepositorio
    ) {
        this.alertaRepositorio = alertaRepositorio;
        this.recomendacionRepositorio = recomendacionRepositorio;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertaRespuestaDto> listarAlertas(Long periodoEvaluacionId, Long seccionId) {
        return alertaRepositorio.findByPeriodoEvaluacionIdAndSeccionId(periodoEvaluacionId, seccionId)
            .stream()
            .map(this::mapearAlerta)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecomendacionRespuestaDto> listarRecomendaciones(Long periodoEvaluacionId, Long seccionId) {
        return recomendacionRepositorio.findByPeriodoEvaluacionIdAndSeccionId(periodoEvaluacionId, seccionId)
            .stream()
            .map(this::mapearRecomendacion)
            .toList();
    }

    @Override
    public AlertaRespuestaDto marcarAtendida(Long alertaId) {
        Alerta alerta = alertaRepositorio.findById(alertaId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Alerta no encontrada con id: " + alertaId));

        alerta.setAtendida(Boolean.TRUE);
        return mapearAlerta(alertaRepositorio.save(alerta));
    }

    private AlertaRespuestaDto mapearAlerta(Alerta alerta) {
        AlertaRespuestaDto dto = new AlertaRespuestaDto();
        dto.setId(alerta.getId());
        dto.setMatriculaId(alerta.getMatricula().getId());
        dto.setAlumnoId(alerta.getMatricula().getAlumno().getId());
        dto.setCodigoAlumno(alerta.getMatricula().getAlumno().getCodigo());
        dto.setAlumnoNombreCompleto(
            alerta.getMatricula().getAlumno().getNombres() + " " +
            alerta.getMatricula().getAlumno().getApellidos()
        );
        if (alerta.getCurso() != null) {
            dto.setCursoId(alerta.getCurso().getId());
            dto.setCurso(alerta.getCurso().getNombre());
        }
        dto.setTipoAlerta(alerta.getTipoAlerta());
        dto.setNivelRiesgo(alerta.getNivelRiesgo());
        dto.setMensaje(alerta.getMensaje());
        dto.setAtendida(alerta.getAtendida());
        dto.setFechaRegistro(alerta.getFechaRegistro());
        return dto;
    }

    private RecomendacionRespuestaDto mapearRecomendacion(Recomendacion recomendacion) {
        RecomendacionRespuestaDto dto = new RecomendacionRespuestaDto();
        dto.setId(recomendacion.getId());
        dto.setMatriculaId(recomendacion.getMatricula().getId());
        dto.setAlumnoId(recomendacion.getMatricula().getAlumno().getId());
        dto.setCodigoAlumno(recomendacion.getMatricula().getAlumno().getCodigo());
        dto.setAlumnoNombreCompleto(
            recomendacion.getMatricula().getAlumno().getNombres() + " " +
            recomendacion.getMatricula().getAlumno().getApellidos()
        );
        if (recomendacion.getCurso() != null) {
            dto.setCursoId(recomendacion.getCurso().getId());
            dto.setCurso(recomendacion.getCurso().getNombre());
        }
        dto.setTitulo(recomendacion.getTitulo());
        dto.setDescripcion(recomendacion.getDescripcion());
        dto.setFuente(recomendacion.getFuente());
        dto.setFechaRegistro(recomendacion.getFechaRegistro());
        return dto;
    }
}
