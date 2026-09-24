package com.tp1.proyecto.prediccion.servicio.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tp1.proyecto.academico.entidad.CorteSeguimiento;
import com.tp1.proyecto.academico.entidad.Matricula;
import com.tp1.proyecto.academico.repositorio.CorteSeguimientoRepositorio;
import com.tp1.proyecto.academico.repositorio.MatriculaRepositorio;
import com.tp1.proyecto.alerta.entidad.Alerta;
import com.tp1.proyecto.alerta.entidad.Recomendacion;
import com.tp1.proyecto.alerta.repositorio.AlertaRepositorio;
import com.tp1.proyecto.alerta.repositorio.RecomendacionRepositorio;
import com.tp1.proyecto.comun.enumeracion.EstadoRegistro;
import com.tp1.proyecto.evaluacion.entidad.AsistenciaSesion;
import com.tp1.proyecto.evaluacion.entidad.DetalleNotaEvaluacion;
import com.tp1.proyecto.evaluacion.entidad.Evaluacion;
import com.tp1.proyecto.evaluacion.enumeracion.EstadoAsistenciaSesion;
import com.tp1.proyecto.evaluacion.repositorio.AsistenciaSesionRepositorio;
import com.tp1.proyecto.evaluacion.repositorio.DetalleNotaEvaluacionRepositorio;
import com.tp1.proyecto.evaluacion.repositorio.EvaluacionRepositorio;
import com.tp1.proyecto.prediccion.dto.PrediccionCursoMlDto;
import com.tp1.proyecto.prediccion.dto.PrediccionCursoMlResponseDto;
import com.tp1.proyecto.prediccion.dto.PrediccionGlobalMlRequestDto;
import com.tp1.proyecto.prediccion.dto.PrediccionGlobalMlResponseDto;
import com.tp1.proyecto.prediccion.dto.PrediccionMlRequestDto;
import com.tp1.proyecto.prediccion.dto.PrediccionMlResponseDto;
import com.tp1.proyecto.prediccion.dto.PrediccionRiesgoRespuestaDto;
import com.tp1.proyecto.prediccion.dto.ResumenPrediccionDto;
import com.tp1.proyecto.prediccion.entidad.PrediccionRiesgo;
import com.tp1.proyecto.prediccion.entidad.PrediccionRiesgoCurso;
import com.tp1.proyecto.prediccion.repositorio.PrediccionRiesgoCursoRepositorio;
import com.tp1.proyecto.prediccion.repositorio.PrediccionRiesgoRepositorio;
import com.tp1.proyecto.prediccion.servicio.ClientePrediccionPython;
import com.tp1.proyecto.prediccion.servicio.CorteSeguimientoPrediccionServicio;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class CorteSeguimientoPrediccionServicioImpl implements CorteSeguimientoPrediccionServicio {
    private static final BigDecimal NOTA_APROBATORIA = BigDecimal.valueOf(11);

    private final CorteSeguimientoRepositorio corteRepositorio;
    private final MatriculaRepositorio matriculaRepositorio;
    private final EvaluacionRepositorio evaluacionRepositorio;
    private final DetalleNotaEvaluacionRepositorio detalleRepositorio;
    private final AsistenciaSesionRepositorio asistenciaRepositorio;
    private final PrediccionRiesgoRepositorio globalRepositorio;
    private final PrediccionRiesgoCursoRepositorio cursoRepositorio;
    private final ClientePrediccionPython clienteMl;
    private final AlertaRepositorio alertaRepositorio;
    private final RecomendacionRepositorio recomendacionRepositorio;
    private final ObjectMapper objectMapper;

    public CorteSeguimientoPrediccionServicioImpl(
        CorteSeguimientoRepositorio corteRepositorio,
        MatriculaRepositorio matriculaRepositorio,
        EvaluacionRepositorio evaluacionRepositorio,
        DetalleNotaEvaluacionRepositorio detalleRepositorio,
        AsistenciaSesionRepositorio asistenciaRepositorio,
        PrediccionRiesgoRepositorio globalRepositorio,
        PrediccionRiesgoCursoRepositorio cursoRepositorio,
        ClientePrediccionPython clienteMl,
        AlertaRepositorio alertaRepositorio,
        RecomendacionRepositorio recomendacionRepositorio,
        ObjectMapper objectMapper
    ) {
        this.corteRepositorio = corteRepositorio;
        this.matriculaRepositorio = matriculaRepositorio;
        this.evaluacionRepositorio = evaluacionRepositorio;
        this.detalleRepositorio = detalleRepositorio;
        this.asistenciaRepositorio = asistenciaRepositorio;
        this.globalRepositorio = globalRepositorio;
        this.cursoRepositorio = cursoRepositorio;
        this.clienteMl = clienteMl;
        this.alertaRepositorio = alertaRepositorio;
        this.recomendacionRepositorio = recomendacionRepositorio;
        this.objectMapper = objectMapper;
    }

    @Override
    public int recalcular(Long corteId, Long seccionId) {
        CorteSeguimiento corte = corteRepositorio.findById(corteId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Corte de seguimiento no encontrado."));
        return recalcularCorte(corte, seccionId, null, true);
    }

    @Override
    public int actualizarPorMatricula(Long matriculaId) {
        Matricula matricula = matriculaRepositorio.findById(matriculaId).orElse(null);
        if (matricula == null || matricula.getEstado() != EstadoRegistro.ACTIVO) return 0;
        List<CorteSeguimiento> cortes = corteRepositorio.findByPeriodoAcademicoIdAndFechaCorteLessThanEqualAndEstadoOrderByFechaCorteAsc(
            matricula.getPeriodoAcademico().getId(), java.time.LocalDate.now(), EstadoRegistro.ACTIVO);
        int actualizadas = 0;
        for (CorteSeguimiento corte : cortes) {
            actualizadas += recalcularCorte(corte, matricula.getSeccion().getId(), matriculaId, false);
        }
        return actualizadas;
    }

    private int recalcularCorte(CorteSeguimiento corte, Long seccionId, Long soloMatriculaId, boolean rechazarSinFechas) {
        if (corte.getFechaCorte().isAfter(java.time.LocalDate.now())) return 0;
        List<Evaluacion> configuradas = evaluacionRepositorio
            .findByDocenteCursoSeccionSeccionIdAndDocenteCursoSeccionPeriodoAcademicoIdAndEstadoOrderByPeriodoEvaluacionNumeroAscTipoEvaluacionOrdenAscNumeroEvaluacionAsc(
                seccionId, corte.getPeriodoAcademico().getId(), EstadoRegistro.ACTIVO
            );
        List<Evaluacion> pendientes = configuradas.stream().filter(e -> e.getFechaEvaluacion() == null).toList();
        if (!pendientes.isEmpty()) {
            if (!rechazarSinFechas) return 0;
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Completa las fechas de " + pendientes.size() + " evaluaciones antes de calcular el riesgo del corte.");
        }

        List<Evaluacion> evaluacionesAlCorte = configuradas.stream()
            .filter(e -> !e.getFechaEvaluacion().isAfter(corte.getFechaCorte())).toList();
        List<Long> evaluacionIds = evaluacionesAlCorte.stream().map(Evaluacion::getId).toList();
        List<Matricula> matriculas = matriculaRepositorio.findBySeccionIdAndPeriodoAcademicoId(
            seccionId, corte.getPeriodoAcademico().getId()
        ).stream().filter(m -> m.getEstado() == EstadoRegistro.ACTIVO)
            .filter(m -> soloMatriculaId == null || m.getId().equals(soloMatriculaId)).toList();
        List<AsistenciaSesion> sesiones = asistenciaRepositorio
            .findByAsignacionSeccionIdAndAsignacionPeriodoAcademicoIdAndFechaClaseLessThanEqualAndEstado(
                seccionId, corte.getPeriodoAcademico().getId(), corte.getFechaCorte(), EstadoRegistro.ACTIVO
            );
        Map<Long, List<AsistenciaSesion>> sesionesPorMatricula = sesiones.stream()
            .collect(Collectors.groupingBy(s -> s.getMatricula().getId()));
        int procesadas = 0;

        for (Matricula matricula : matriculas) {
            List<DetalleNotaEvaluacion> detalles = evaluacionIds.isEmpty() ? List.of() : detalleRepositorio
                .findByEvaluacionIdInAndMatriculaIdAndEstado(evaluacionIds, matricula.getId(), EstadoRegistro.ACTIVO);
            List<AsistenciaSesion> sesionesAlumno = sesionesPorMatricula.getOrDefault(matricula.getId(), List.of());
            Map<Long, List<BigDecimal>> notasPorCurso = detalles.stream().collect(Collectors.groupingBy(
                d -> d.getEvaluacion().getDocenteCursoSeccion().getCurso().getId(),
                Collectors.mapping(DetalleNotaEvaluacion::getNota, Collectors.toList())
            ));
            if (detalles.isEmpty() && sesionesAlumno.isEmpty()) continue;

            Map<Long, BigDecimal> promedioCurso = notasPorCurso.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> promedio(e.getValue())
            ));
            List<BigDecimal> promedios = new ArrayList<>(promedioCurso.values());
            BigDecimal promedioGeneral = promedios.isEmpty() ? BigDecimal.valueOf(20) : promedio(promedios);
            BigDecimal minima = promedios.stream().min(Comparator.naturalOrder()).orElse(BigDecimal.valueOf(20));
            BigDecimal maxima = promedios.stream().max(Comparator.naturalOrder()).orElse(BigDecimal.valueOf(20));
            int programadas = (int) sesionesAlumno.stream().filter(s -> s.getEstadoAsistencia() != EstadoAsistenciaSesion.JUSTIFICADO).count();
            int asistidas = (int) sesionesAlumno.stream().filter(s -> s.getEstadoAsistencia() == EstadoAsistenciaSesion.PRESENTE
                || s.getEstadoAsistencia() == EstadoAsistenciaSesion.TARDANZA).count();
            double asistencia = programadas == 0 ? 0 : BigDecimal.valueOf(asistidas * 100L)
                .divide(BigDecimal.valueOf(programadas), 2, RoundingMode.HALF_UP).doubleValue();

            PrediccionGlobalMlRequestDto global = new PrediccionGlobalMlRequestDto();
            global.setMatriculaId(matricula.getId());
            global.setCorteSeguimientoId(corte.getId());
            global.setSemanaCorte(corte.getSemana());
            global.setFechaCorte(corte.getFechaCorte());
            global.setPromedioGeneral(promedioGeneral.doubleValue());
            global.setCantidadCursos(Math.max(1, promedioCurso.size()));
            global.setCantidadCursosDesaprobados((int) promedios.stream().filter(n -> n.compareTo(NOTA_APROBATORIA) < 0).count());
            global.setNotaMaxima(maxima.doubleValue());
            global.setNotaMinima(minima.doubleValue());
            global.setClasesProgramadas(programadas);
            global.setClasesAsistidas(asistidas);
            global.setPorcentajeAsistencia(asistencia);
            global.setCantidadEvaluacionesRegistradas((int) detalles.stream().map(d -> d.getEvaluacion().getId()).distinct().count());
            global.setCantidadNotasDesaprobadasTotal(global.getCantidadCursosDesaprobados());
            global.setCantidadNotasCriticasTotal((int) promedios.stream().filter(n -> n.compareTo(BigDecimal.TEN) <= 0).count());
            global.setPeorNotaPeriodo(minima.doubleValue());
            global.setCantidadCursosC(global.getCantidadCursosDesaprobados());
            global.setCantidadCursosB((int) promedios.stream().filter(n -> n.compareTo(NOTA_APROBATORIA) >= 0 && n.compareTo(BigDecimal.valueOf(14)) < 0).count());
            global.setCantidadCursosA((int) promedios.stream().filter(n -> n.compareTo(BigDecimal.valueOf(14)) >= 0 && n.compareTo(BigDecimal.valueOf(18)) < 0).count());
            global.setCantidadCursosAd((int) promedios.stream().filter(n -> n.compareTo(BigDecimal.valueOf(18)) >= 0).count());

            List<PrediccionCursoMlDto> featuresCurso = new ArrayList<>();
            for (Map.Entry<Long, List<BigDecimal>> entry : notasPorCurso.entrySet()) {
                BigDecimal nota = promedio(entry.getValue());
                List<AsistenciaSesion> sesionesCurso = sesionesAlumno.stream()
                    .filter(s -> s.getAsignacion().getCurso().getId().equals(entry.getKey())).toList();
                int clasesCurso = (int) sesionesCurso.stream().filter(s -> s.getEstadoAsistencia() != EstadoAsistenciaSesion.JUSTIFICADO).count();
                int presentesCurso = (int) sesionesCurso.stream().filter(s -> s.getEstadoAsistencia() == EstadoAsistenciaSesion.PRESENTE
                    || s.getEstadoAsistencia() == EstadoAsistenciaSesion.TARDANZA).count();
                PrediccionCursoMlDto feature = new PrediccionCursoMlDto();
                feature.setMatriculaId(matricula.getId());
                feature.setCursoId(entry.getKey());
                feature.setCursoNombre(detalles.stream().filter(d -> d.getEvaluacion().getDocenteCursoSeccion().getCurso().getId().equals(entry.getKey()))
                    .findFirst().orElseThrow().getEvaluacion().getDocenteCursoSeccion().getCurso().getNombre());
                feature.setCorteSeguimientoId(corte.getId());
                feature.setSemanaCorte(corte.getSemana());
                feature.setFechaCorte(corte.getFechaCorte());
                feature.setNotaCurso(nota.doubleValue());
                feature.setPromedioGeneral(promedioGeneral.doubleValue());
                feature.setCantidadCursosDesaprobados(global.getCantidadCursosDesaprobados());
                feature.setPorcentajeAsistencia(clasesCurso == 0 ? asistencia : (100.0 * presentesCurso / clasesCurso));
                feature.setCantidadEvaluacionesRegistradas((int) entry.getValue().size());
                feature.setNotaMinimaCurso(entry.getValue().stream().min(Comparator.naturalOrder()).orElse(nota).doubleValue());
                feature.setNotaMaximaCurso(entry.getValue().stream().max(Comparator.naturalOrder()).orElse(nota).doubleValue());
                feature.setCantidadNotasDesaprobadas((int) entry.getValue().stream().filter(n -> n.compareTo(NOTA_APROBATORIA) < 0).count());
                feature.setCantidadNotasCriticas((int) entry.getValue().stream().filter(n -> n.compareTo(BigDecimal.TEN) <= 0).count());
                feature.setNotaExamenPrincipal(nota.doubleValue());
                featuresCurso.add(feature);
            }

            PrediccionMlRequestDto request = new PrediccionMlRequestDto();
            request.setModeloVersion("v4-corte-temprano");
            request.setGlobalFeatures(global);
            request.setCourseFeatures(featuresCurso);
            PrediccionMlResponseDto response = clienteMl.predecir(request);
            if (response == null || response.getGlobalPrediction() == null) continue;
            guardarGlobal(corte, matricula, response.getGlobalPrediction());
            guardarCursos(corte, matricula, response.getCoursePredictions());
            procesadas++;
        }
        return procesadas;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrediccionRiesgoRespuestaDto> listarGlobales(Long corteId, Long seccionId) {
        return globalRepositorio.findByCorteSeguimientoIdAndMatriculaSeccionId(corteId, seccionId).stream()
            .map(this::mapGlobal).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrediccionRiesgoRespuestaDto> listarCursos(Long corteId, Long seccionId) {
        return cursoRepositorio.findByCorteSeguimientoIdAndMatriculaSeccionId(corteId, seccionId).stream()
            .map(this::mapCurso).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ResumenPrediccionDto resumen(Long corteId, Long seccionId) {
        List<PrediccionRiesgoRespuestaDto> items = listarGlobales(corteId, seccionId);
        ResumenPrediccionDto dto = new ResumenPrediccionDto();
        CorteSeguimiento corte = corteRepositorio.findById(corteId).orElseThrow();
        dto.setCorteSeguimientoId(corte.getId());
        dto.setSemanaCorte(corte.getSemana());
        dto.setFechaCorte(corte.getFechaCorte());
        dto.setSeccionId(seccionId);
        dto.setTotalPredicciones(items.size());
        dto.setTotalRiesgoAlto((int) items.stream().filter(p -> "ALTO".equalsIgnoreCase(p.getNivelRiesgo())).count());
        dto.setTotalRiesgoMedio((int) items.stream().filter(p -> "MEDIO".equalsIgnoreCase(p.getNivelRiesgo())).count());
        dto.setTotalRiesgoBajo((int) items.stream().filter(p -> "BAJO".equalsIgnoreCase(p.getNivelRiesgo())).count());
        dto.setPromedioPuntajeRiesgo(items.isEmpty() ? BigDecimal.ZERO : items.stream().map(PrediccionRiesgoRespuestaDto::getPuntajeRiesgo)
            .reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(items.size()), 2, RoundingMode.HALF_UP));
        if (!items.isEmpty()) {
            PrediccionRiesgoRespuestaDto ref = items.get(0);
            dto.setNivel(ref.getNivel()); dto.setGrado(ref.getGrado()); dto.setSeccion(ref.getSeccion());
        }
        return dto;
    }

    private void guardarGlobal(CorteSeguimiento corte, Matricula matricula, PrediccionGlobalMlResponseDto response) {
        PrediccionRiesgo entity = globalRepositorio.findByMatriculaIdAndCorteSeguimientoId(matricula.getId(), corte.getId())
            .orElseGet(PrediccionRiesgo::new);
        entity.setMatricula(matricula); entity.setPeriodoEvaluacion(null); entity.setCorteSeguimiento(corte);
        entity.setPuntajeRiesgo(BigDecimal.valueOf(response.getPuntajeRiesgo())); entity.setNivelRiesgo(response.getNivelRiesgo());
        entity.setModeloVersion(response.getModeloVersion()); entity.setVariablesEntrada(serializar(response.getVariablesEntrada()));
        entity.setFechaPrediccion(LocalDateTime.now());
        PrediccionRiesgo saved = globalRepositorio.save(entity);
        alertaRepositorio.deleteByPrediccionGlobalId(saved.getId());
        recomendacionRepositorio.deleteByPrediccionGlobalId(saved.getId());
        if ("ALTO".equalsIgnoreCase(saved.getNivelRiesgo()) || "MEDIO".equalsIgnoreCase(saved.getNivelRiesgo())) {
            Alerta alerta = new Alerta(); alerta.setMatricula(matricula); alerta.setPrediccionGlobal(saved);
            alerta.setTipoAlerta("RIESGO_GLOBAL"); alerta.setNivelRiesgo(saved.getNivelRiesgo());
            alerta.setMensaje("Riesgo académico " + saved.getNivelRiesgo() + " detectado al corte de la semana " + corte.getSemana() + ".");
            alerta.setAtendida(false); alertaRepositorio.save(alerta);
            Recomendacion rec = new Recomendacion(); rec.setMatricula(matricula); rec.setPrediccionGlobal(saved);
            rec.setTitulo("Seguimiento académico al corte de semana " + corte.getSemana());
            rec.setDescripcion("Revisar los indicadores disponibles hasta " + corte.getFechaCorte() + " y acordar una acción de apoyo.");
            rec.setFuente("MODELO_ML"); recomendacionRepositorio.save(rec);
        }
    }

    private void guardarCursos(CorteSeguimiento corte, Matricula matricula, List<PrediccionCursoMlResponseDto> predictions) {
        if (predictions == null) return;
        for (PrediccionCursoMlResponseDto response : predictions) {
            PrediccionRiesgoCurso entity = cursoRepositorio.findByMatriculaIdAndCursoIdAndCorteSeguimientoId(
                matricula.getId(), response.getCursoId(), corte.getId()).orElseGet(PrediccionRiesgoCurso::new);
            entity.setMatricula(matricula);
            entity.setCurso(detalleCurso(response.getCursoId(), matricula, corte));
            entity.setPeriodoEvaluacion(null); entity.setCorteSeguimiento(corte);
            entity.setPuntajeRiesgo(BigDecimal.valueOf(response.getPuntajeRiesgo())); entity.setNivelRiesgo(response.getNivelRiesgo());
            entity.setModeloVersion(response.getModeloVersion()); entity.setVariablesEntrada(serializar(response.getVariablesEntrada()));
            entity.setFechaPrediccion(LocalDateTime.now());
            PrediccionRiesgoCurso saved = cursoRepositorio.save(entity);
            alertaRepositorio.deleteByPrediccionCursoId(saved.getId()); recomendacionRepositorio.deleteByPrediccionCursoId(saved.getId());
            if ("ALTO".equalsIgnoreCase(saved.getNivelRiesgo()) || "MEDIO".equalsIgnoreCase(saved.getNivelRiesgo())) {
                Alerta alerta = new Alerta(); alerta.setMatricula(matricula); alerta.setCurso(saved.getCurso());
                alerta.setPrediccionCurso(saved); alerta.setTipoAlerta("RIESGO_CURSO"); alerta.setNivelRiesgo(saved.getNivelRiesgo());
                alerta.setMensaje("Riesgo " + saved.getNivelRiesgo() + " en " + saved.getCurso().getNombre() + " al corte de semana " + corte.getSemana() + ".");
                alerta.setAtendida(false); alertaRepositorio.save(alerta);
                Recomendacion rec = new Recomendacion(); rec.setMatricula(matricula); rec.setCurso(saved.getCurso()); rec.setPrediccionCurso(saved);
                rec.setTitulo("Refuerzo en " + saved.getCurso().getNombre());
                rec.setDescripcion("Revisar las evaluaciones y asistencia disponibles hasta el corte semanal.");
                rec.setFuente("MODELO_ML"); recomendacionRepositorio.save(rec);
            }
        }
    }

    private com.tp1.proyecto.academico.entidad.Curso detalleCurso(Long cursoId, Matricula matricula, CorteSeguimiento corte) {
        return evaluacionRepositorio
            .findByDocenteCursoSeccionSeccionIdAndDocenteCursoSeccionPeriodoAcademicoIdAndEstadoOrderByPeriodoEvaluacionNumeroAscTipoEvaluacionOrdenAscNumeroEvaluacionAsc(
                matricula.getSeccion().getId(), corte.getPeriodoAcademico().getId(), EstadoRegistro.ACTIVO)
            .stream().map(e -> e.getDocenteCursoSeccion().getCurso()).filter(c -> c.getId().equals(cursoId)).findFirst()
            .orElseThrow(() -> new IllegalStateException("No se encontró el curso asociado a la predicción."));
    }

    private PrediccionRiesgoRespuestaDto mapGlobal(PrediccionRiesgo p) {
        PrediccionRiesgoRespuestaDto dto = base(p.getMatricula(), p.getPuntajeRiesgo(), p.getNivelRiesgo(), p.getModeloVersion(), p.getVariablesEntrada(), p.getFechaPrediccion());
        dto.setId(p.getId()); dto.setCorteSeguimientoId(p.getCorteSeguimiento().getId()); dto.setSemanaCorte(p.getCorteSeguimiento().getSemana());
        dto.setFechaCorte(p.getCorteSeguimiento().getFechaCorte()); return dto;
    }

    private PrediccionRiesgoRespuestaDto mapCurso(PrediccionRiesgoCurso p) {
        PrediccionRiesgoRespuestaDto dto = base(p.getMatricula(), p.getPuntajeRiesgo(), p.getNivelRiesgo(), p.getModeloVersion(), p.getVariablesEntrada(), p.getFechaPrediccion());
        dto.setId(p.getId()); dto.setCursoId(p.getCurso().getId()); dto.setCurso(p.getCurso().getNombre());
        dto.setCorteSeguimientoId(p.getCorteSeguimiento().getId()); dto.setSemanaCorte(p.getCorteSeguimiento().getSemana());
        dto.setFechaCorte(p.getCorteSeguimiento().getFechaCorte()); return dto;
    }

    private PrediccionRiesgoRespuestaDto base(Matricula m, BigDecimal score, String level, String version, String variables, LocalDateTime date) {
        PrediccionRiesgoRespuestaDto d = new PrediccionRiesgoRespuestaDto(); d.setMatriculaId(m.getId()); d.setAlumnoId(m.getAlumno().getId());
        d.setCodigoAlumno(m.getAlumno().getCodigo()); d.setAlumnoNombreCompleto(m.getAlumno().getNombres() + " " + m.getAlumno().getApellidos());
        d.setGrado(m.getGrado().getNombre()); d.setNivel(m.getGrado().getNivel().getNombre()); d.setSeccion(m.getSeccion().getNombre());
        d.setPeriodoAcademicoId(m.getPeriodoAcademico().getId()); d.setAnioAcademico(m.getPeriodoAcademico().getAnio());
        d.setPuntajeRiesgo(score); d.setNivelRiesgo(level); d.setModeloVersion(version); d.setVariablesEntrada(variables); d.setFechaPrediccion(date); return d;
    }

    private BigDecimal promedio(List<BigDecimal> notas) {
        return notas.stream().reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(notas.size()), 2, RoundingMode.HALF_UP);
    }

    private String serializar(Object value) {
        try { return objectMapper.writeValueAsString(value); }
        catch (JsonProcessingException e) { return "{}"; }
    }
}
