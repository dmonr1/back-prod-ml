package com.tp1.proyecto.evaluacion.servicio.impl;

import com.tp1.proyecto.academico.entidad.Matricula;
import com.tp1.proyecto.academico.repositorio.BimestreRepositorio;
import com.tp1.proyecto.academico.repositorio.MatriculaRepositorio;
import com.tp1.proyecto.comun.enumeracion.EstadoRegistro;
import com.tp1.proyecto.evaluacion.dto.AsistenciaBimestreRespuestaDto;
import com.tp1.proyecto.evaluacion.dto.AsistenciaBimestreSolicitudDto;
import com.tp1.proyecto.evaluacion.dto.RegistroAsistenciasBimestreSolicitudDto;
import com.tp1.proyecto.evaluacion.entidad.AsistenciaBimestre;
import com.tp1.proyecto.evaluacion.repositorio.AsistenciaBimestreRepositorio;
import com.tp1.proyecto.evaluacion.servicio.AsistenciaBimestreServicio;
import com.tp1.proyecto.excepcion.RecursoNoEncontradoException;
import com.tp1.proyecto.excepcion.ReglaNegocioException;
import com.tp1.proyecto.prediccion.servicio.PrediccionRiesgoServicio;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AsistenciaBimestreServicioImpl implements AsistenciaBimestreServicio {

    private final AsistenciaBimestreRepositorio asistenciaBimestreRepositorio;
    private final MatriculaRepositorio matriculaRepositorio;
    private final BimestreRepositorio bimestreRepositorio;
    private final PrediccionRiesgoServicio prediccionRiesgoServicio;

    public AsistenciaBimestreServicioImpl(
        AsistenciaBimestreRepositorio asistenciaBimestreRepositorio,
        MatriculaRepositorio matriculaRepositorio,
        BimestreRepositorio bimestreRepositorio,
        PrediccionRiesgoServicio prediccionRiesgoServicio
    ) {
        this.asistenciaBimestreRepositorio = asistenciaBimestreRepositorio;
        this.matriculaRepositorio = matriculaRepositorio;
        this.bimestreRepositorio = bimestreRepositorio;
        this.prediccionRiesgoServicio = prediccionRiesgoServicio;
    }

    @Override
    public List<AsistenciaBimestreRespuestaDto> registrarAsistencias(Long bimestreId, RegistroAsistenciasBimestreSolicitudDto solicitud) {
        var bimestre = bimestreRepositorio.findById(bimestreId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Bimestre no encontrado con id: " + bimestreId));

        List<AsistenciaBimestreRespuestaDto> respuestas = new ArrayList<>();
        for (AsistenciaBimestreSolicitudDto item : solicitud.getAsistencias()) {
            if (item.getClasesAsistidas() > item.getClasesProgramadas()) {
                throw new ReglaNegocioException("Las clases asistidas no pueden ser mayores que las programadas");
            }

            Matricula matricula = matriculaRepositorio.findById(item.getMatriculaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Matricula no encontrada con id: " + item.getMatriculaId()));

            AsistenciaBimestre asistencia = asistenciaBimestreRepositorio
                .findByMatriculaIdAndBimestreId(item.getMatriculaId(), bimestreId)
                .orElseGet(AsistenciaBimestre::new);

            asistencia.setMatricula(matricula);
            asistencia.setBimestre(bimestre);
            asistencia.setClasesProgramadas(item.getClasesProgramadas());
            asistencia.setClasesAsistidas(item.getClasesAsistidas());
            asistencia.setObservacion(item.getObservacion());
            asistencia.setEstado(EstadoRegistro.ACTIVO);

            AsistenciaBimestre guardada = asistenciaBimestreRepositorio.save(asistencia);
            prediccionRiesgoServicio.generarPrediccionGlobalPorMatricula(matricula.getId(), bimestreId);
            respuestas.add(mapear(guardada));
        }

        return respuestas;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AsistenciaBimestreRespuestaDto> listarPorSeccionYBimestre(Long seccionId, Long periodoAcademicoId, Long bimestreId) {
        bimestreRepositorio.findById(bimestreId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Bimestre no encontrado con id: " + bimestreId));

        Map<Long, AsistenciaBimestre> asistenciaPorMatricula = new LinkedHashMap<>();
        for (Matricula matricula : matriculaRepositorio.findBySeccionIdAndPeriodoAcademicoId(seccionId, periodoAcademicoId)) {
            asistenciaBimestreRepositorio.findByMatriculaIdAndBimestreId(matricula.getId(), bimestreId)
                .ifPresent(asistencia -> asistenciaPorMatricula.put(matricula.getId(), asistencia));
        }

        return asistenciaPorMatricula.values().stream().map(this::mapear).toList();
    }

    private AsistenciaBimestreRespuestaDto mapear(AsistenciaBimestre entidad) {
        AsistenciaBimestreRespuestaDto dto = new AsistenciaBimestreRespuestaDto();
        dto.setId(entidad.getId());
        dto.setMatriculaId(entidad.getMatricula().getId());
        dto.setAlumnoId(entidad.getMatricula().getAlumno().getId());
        dto.setCodigoAlumno(entidad.getMatricula().getAlumno().getCodigo());
        dto.setAlumnoNombreCompleto(
            entidad.getMatricula().getAlumno().getNombres() + " " + entidad.getMatricula().getAlumno().getApellidos()
        );
        dto.setBimestreId(entidad.getBimestre().getId());
        dto.setClasesProgramadas(entidad.getClasesProgramadas());
        dto.setClasesAsistidas(entidad.getClasesAsistidas());
        dto.setPorcentajeAsistencia(calcularPorcentaje(entidad.getClasesProgramadas(), entidad.getClasesAsistidas()));
        dto.setObservacion(entidad.getObservacion());
        return dto;
    }

    private Double calcularPorcentaje(Integer clasesProgramadas, Integer clasesAsistidas) {
        if (clasesProgramadas == null || clasesProgramadas == 0) {
            return 0.0;
        }
        return BigDecimal.valueOf(clasesAsistidas)
            .multiply(BigDecimal.valueOf(100))
            .divide(BigDecimal.valueOf(clasesProgramadas), 2, RoundingMode.HALF_UP)
            .doubleValue();
    }
}
