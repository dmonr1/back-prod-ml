package com.tp1.proyecto.evaluacion.servicio.impl;

import com.tp1.proyecto.evaluacion.dto.TipoEvaluacionRespuestaDto;
import com.tp1.proyecto.evaluacion.dto.TipoEvaluacionSolicitudDto;
import com.tp1.proyecto.evaluacion.entidad.TipoEvaluacion;
import com.tp1.proyecto.evaluacion.repositorio.TipoEvaluacionRepositorio;
import com.tp1.proyecto.evaluacion.servicio.TipoEvaluacionServicio;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TipoEvaluacionServicioImpl implements TipoEvaluacionServicio {

    private final TipoEvaluacionRepositorio tipoEvaluacionRepositorio;

    public TipoEvaluacionServicioImpl(TipoEvaluacionRepositorio tipoEvaluacionRepositorio) {
        this.tipoEvaluacionRepositorio = tipoEvaluacionRepositorio;
    }

    @Override
    public TipoEvaluacionRespuestaDto crear(TipoEvaluacionSolicitudDto solicitud) {
        TipoEvaluacion tipoEvaluacion = new TipoEvaluacion();
        tipoEvaluacion.setNombre(solicitud.getNombre().trim().toUpperCase());
        tipoEvaluacion.setDescripcion(solicitud.getDescripcion());
        tipoEvaluacion.setOrden(solicitud.getOrden());

        return mapear(tipoEvaluacionRepositorio.save(tipoEvaluacion));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TipoEvaluacionRespuestaDto> listar() {
        return tipoEvaluacionRepositorio.findAllByOrderByOrdenAscNombreAsc()
            .stream()
            .map(this::mapear)
            .toList();
    }

    private TipoEvaluacionRespuestaDto mapear(TipoEvaluacion entidad) {
        TipoEvaluacionRespuestaDto dto = new TipoEvaluacionRespuestaDto();
        dto.setId(entidad.getId());
        dto.setNombre(entidad.getNombre());
        dto.setDescripcion(entidad.getDescripcion());
        dto.setOrden(entidad.getOrden());
        dto.setEstado(entidad.getEstado().name());
        return dto;
    }
}
