package com.tp1.proyecto.prediccion.servicio.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tp1.proyecto.academico.entidad.Matricula;
import com.tp1.proyecto.academico.repositorio.PeriodoEvaluacionRepositorio;
import com.tp1.proyecto.academico.repositorio.MatriculaRepositorio;
import com.tp1.proyecto.alerta.entidad.Alerta;
import com.tp1.proyecto.alerta.entidad.Recomendacion;
import com.tp1.proyecto.alerta.repositorio.AlertaRepositorio;
import com.tp1.proyecto.alerta.repositorio.RecomendacionRepositorio;
import com.tp1.proyecto.evaluacion.entidad.AsistenciaPeriodoEvaluacion;
import com.tp1.proyecto.evaluacion.entidad.NotaCursoPeriodoEvaluacion;
import com.tp1.proyecto.evaluacion.repositorio.AsistenciaPeriodoEvaluacionRepositorio;
import com.tp1.proyecto.evaluacion.repositorio.NotaCursoPeriodoEvaluacionRepositorio;
import com.tp1.proyecto.notas.entidad.Asistencia;
import com.tp1.proyecto.notas.entidad.CargaExcel;
import com.tp1.proyecto.notas.entidad.Nota;
import com.tp1.proyecto.notas.repositorio.AsistenciaRepositorio;
import com.tp1.proyecto.notas.repositorio.NotaRepositorio;
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
import com.tp1.proyecto.prediccion.servicio.PrediccionRiesgoServicio;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PrediccionRiesgoServicioImpl implements PrediccionRiesgoServicio {

    private final MatriculaRepositorio matriculaRepositorio;
    private final PeriodoEvaluacionRepositorio periodoEvaluacionRepositorio;
    private final NotaCursoPeriodoEvaluacionRepositorio notaCursoPeriodoEvaluacionRepositorio;
    private final AsistenciaPeriodoEvaluacionRepositorio asistenciaPeriodoEvaluacionRepositorio;
    private final NotaRepositorio notaRepositorio;
    private final AsistenciaRepositorio asistenciaRepositorio;
    private final PrediccionRiesgoRepositorio prediccionRiesgoRepositorio;
    private final PrediccionRiesgoCursoRepositorio prediccionRiesgoCursoRepositorio;
    private final AlertaRepositorio alertaRepositorio;
    private final RecomendacionRepositorio recomendacionRepositorio;
    private final ClientePrediccionPython clientePrediccionPython;
    private final ObjectMapper objectMapper;

    public PrediccionRiesgoServicioImpl(
        MatriculaRepositorio matriculaRepositorio,
        PeriodoEvaluacionRepositorio periodoEvaluacionRepositorio,
        NotaCursoPeriodoEvaluacionRepositorio notaCursoPeriodoEvaluacionRepositorio,
        AsistenciaPeriodoEvaluacionRepositorio asistenciaPeriodoEvaluacionRepositorio,
        NotaRepositorio notaRepositorio,
        AsistenciaRepositorio asistenciaRepositorio,
        PrediccionRiesgoRepositorio prediccionRiesgoRepositorio,
        PrediccionRiesgoCursoRepositorio prediccionRiesgoCursoRepositorio,
        AlertaRepositorio alertaRepositorio,
        RecomendacionRepositorio recomendacionRepositorio,
        ClientePrediccionPython clientePrediccionPython,
        ObjectMapper objectMapper
    ) {
        this.matriculaRepositorio = matriculaRepositorio;
        this.periodoEvaluacionRepositorio = periodoEvaluacionRepositorio;
        this.notaCursoPeriodoEvaluacionRepositorio = notaCursoPeriodoEvaluacionRepositorio;
        this.asistenciaPeriodoEvaluacionRepositorio = asistenciaPeriodoEvaluacionRepositorio;
        this.notaRepositorio = notaRepositorio;
        this.asistenciaRepositorio = asistenciaRepositorio;
        this.prediccionRiesgoRepositorio = prediccionRiesgoRepositorio;
        this.prediccionRiesgoCursoRepositorio = prediccionRiesgoCursoRepositorio;
        this.alertaRepositorio = alertaRepositorio;
        this.recomendacionRepositorio = recomendacionRepositorio;
        this.clientePrediccionPython = clientePrediccionPython;
        this.objectMapper = objectMapper;
    }

    @Override
    public void generarPrediccionesGlobales(CargaExcel cargaExcel) {
        List<Matricula> matriculas = matriculaRepositorio.findBySeccionIdAndPeriodoAcademicoId(
            cargaExcel.getSeccion().getId(),
            cargaExcel.getPeriodoAcademico().getId()
        );

        for (Matricula matricula : matriculas) {
            List<NotaCursoPeriodoEvaluacion> notasConsolidadas = notaCursoPeriodoEvaluacionRepositorio.findByMatriculaIdAndPeriodoEvaluacionId(
                matricula.getId(),
                cargaExcel.getPeriodoEvaluacion().getId()
            );

            if (!notasConsolidadas.isEmpty()) {
                AsistenciaPeriodoEvaluacion asistenciaConsolidada = asistenciaPeriodoEvaluacionRepositorio
                    .findByMatriculaIdAndPeriodoEvaluacionId(matricula.getId(), cargaExcel.getPeriodoEvaluacion().getId())
                    .orElse(null);

                PrediccionMlRequestDto request = construirRequestConsolidado(
                    matricula,
                    cargaExcel.getPeriodoEvaluacion().getId(),
                    notasConsolidadas,
                    asistenciaConsolidada
                );

                procesarRespuestaPrediccion(matricula, cargaExcel.getPeriodoEvaluacion().getId(), cargaExcel.getId(), request);
                continue;
            }

            // Compatibilidad temporal con el flujo antiguo de notas/asistencias por Excel.
            // El flujo principal del sistema debe llegar aqui ya consolidado desde
            // evaluaciones parciales y asistencias bimestrales.
            List<Nota> notas = notaRepositorio.findByMatriculaIdAndPeriodoEvaluacionId(
                matricula.getId(),
                cargaExcel.getPeriodoEvaluacion().getId()
            );

            if (notas.isEmpty()) {
                continue;
            }

            Optional<Asistencia> asistenciaOpt = asistenciaRepositorio.findByMatriculaIdAndPeriodoEvaluacionId(
                matricula.getId(),
                cargaExcel.getPeriodoEvaluacion().getId()
            );

            PrediccionMlRequestDto request = construirRequestLegado(matricula, cargaExcel, notas, asistenciaOpt.orElse(null));
            procesarRespuestaPrediccion(matricula, cargaExcel.getPeriodoEvaluacion().getId(), cargaExcel.getId(), request);
        }
    }

    @Override
    public void generarPrediccionGlobalPorMatricula(Long matriculaId, Long periodoEvaluacionId) {
        Matricula matricula = matriculaRepositorio.findById(matriculaId)
            .orElseThrow(() -> new IllegalArgumentException("Matricula no encontrada: " + matriculaId));

        List<NotaCursoPeriodoEvaluacion> notasConsolidadas = notaCursoPeriodoEvaluacionRepositorio.findByMatriculaIdAndPeriodoEvaluacionId(matriculaId, periodoEvaluacionId);
        if (notasConsolidadas.isEmpty()) {
            return;
        }

        AsistenciaPeriodoEvaluacion asistenciaConsolidada = asistenciaPeriodoEvaluacionRepositorio
            .findByMatriculaIdAndPeriodoEvaluacionId(matriculaId, periodoEvaluacionId)
            .orElse(null);

        PrediccionMlRequestDto request = construirRequestConsolidado(matricula, periodoEvaluacionId, notasConsolidadas, asistenciaConsolidada);
        procesarRespuestaPrediccion(matricula, periodoEvaluacionId, null, request);
    }

    @Override
    public int recalcularPrediccionesPorSeccionYPeriodo(Long seccionId, Long periodoEvaluacionId) {
        var periodoEvaluacion = periodoEvaluacionRepositorio.findById(periodoEvaluacionId)
            .orElseThrow(() -> new IllegalArgumentException("Periodo de evaluacion no encontrado: " + periodoEvaluacionId));

        Long periodoAcademicoId = periodoEvaluacion.getPeriodoAcademico().getId();
        List<Matricula> matriculas = matriculaRepositorio.findBySeccionIdAndPeriodoAcademicoId(seccionId, periodoAcademicoId);

        int procesadas = 0;
        for (Matricula matricula : matriculas) {
            List<NotaCursoPeriodoEvaluacion> notasConsolidadas = notaCursoPeriodoEvaluacionRepositorio
                .findByMatriculaIdAndPeriodoEvaluacionId(matricula.getId(), periodoEvaluacionId);

            if (notasConsolidadas.isEmpty()) {
                continue;
            }

            AsistenciaPeriodoEvaluacion asistenciaConsolidada = asistenciaPeriodoEvaluacionRepositorio
                .findByMatriculaIdAndPeriodoEvaluacionId(matricula.getId(), periodoEvaluacionId)
                .orElse(null);

            PrediccionMlRequestDto request = construirRequestConsolidado(
                matricula,
                periodoEvaluacionId,
                notasConsolidadas,
                asistenciaConsolidada
            );

            procesarRespuestaPrediccion(matricula, periodoEvaluacionId, null, request);
            procesadas++;
        }

        return procesadas;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrediccionRiesgoRespuestaDto> listarPrediccionesGlobales(Long periodoEvaluacionId, Long seccionId) {
        return prediccionRiesgoRepositorio.findByPeriodoEvaluacionIdAndMatriculaSeccionId(periodoEvaluacionId, seccionId)
            .stream()
            .map(this::mapearRespuestaGlobal)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrediccionRiesgoRespuestaDto> listarPrediccionesCurso(Long periodoEvaluacionId, Long seccionId) {
        return prediccionRiesgoCursoRepositorio.findByPeriodoEvaluacionIdAndMatriculaSeccionId(periodoEvaluacionId, seccionId)
            .stream()
            .map(this::mapearRespuestaCurso)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrediccionRiesgoRespuestaDto> listarPrediccionesPorAlumno(Long alumnoId) {
        return prediccionRiesgoRepositorio.findByMatriculaAlumnoIdOrderByFechaPrediccionDesc(alumnoId)
            .stream()
            .map(this::mapearRespuestaGlobal)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ResumenPrediccionDto obtenerResumenPredicciones(Long periodoEvaluacionId, Long seccionId) {
        List<PrediccionRiesgo> predicciones = prediccionRiesgoRepositorio.findByPeriodoEvaluacionIdAndMatriculaSeccionId(
            periodoEvaluacionId,
            seccionId
        );

        ResumenPrediccionDto resumen = new ResumenPrediccionDto();
        resumen.setPeriodoEvaluacionId(periodoEvaluacionId);
        resumen.setSeccionId(seccionId);
        resumen.setTotalPredicciones(predicciones.size());
        resumen.setTotalRiesgoAlto((int) predicciones.stream().filter(p -> "ALTO".equalsIgnoreCase(p.getNivelRiesgo())).count());
        resumen.setTotalRiesgoMedio((int) predicciones.stream().filter(p -> "MEDIO".equalsIgnoreCase(p.getNivelRiesgo())).count());
        resumen.setTotalRiesgoBajo((int) predicciones.stream().filter(p -> "BAJO".equalsIgnoreCase(p.getNivelRiesgo())).count());

        BigDecimal promedio = predicciones.isEmpty()
            ? BigDecimal.ZERO
            : predicciones.stream()
                .map(PrediccionRiesgo::getPuntajeRiesgo)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(predicciones.size()), 2, RoundingMode.HALF_UP);
        resumen.setPromedioPuntajeRiesgo(promedio);

        if (!predicciones.isEmpty()) {
            PrediccionRiesgo referencia = predicciones.get(0);
            resumen.setNivel(referencia.getMatricula().getGrado().getNivel().getNombre());
            resumen.setGrado(referencia.getMatricula().getGrado().getNombre());
            resumen.setSeccion(referencia.getMatricula().getSeccion().getNombre());
        }

        return resumen;
    }

    private void procesarRespuestaPrediccion(
        Matricula matricula,
        Long periodoEvaluacionId,
        Long cargaArchivoId,
        PrediccionMlRequestDto request
    ) {
        PrediccionMlResponseDto response = clientePrediccionPython.predecir(request);
        if (response == null || response.getGlobalPrediction() == null) {
            return;
        }

        PrediccionRiesgo prediccionGlobal = guardarPrediccionGlobal(
            periodoEvaluacionId,
            cargaArchivoId,
            matricula,
            response.getGlobalPrediction()
        );
        guardarPrediccionesCurso(cargaArchivoId, matricula, response.getCoursePredictions());
        generarAlertasYRecomendacionesGlobales(prediccionGlobal);
    }

    private PrediccionMlRequestDto construirRequestLegado(
        Matricula matricula,
        CargaExcel cargaExcel,
        List<Nota> notas,
        Asistencia asistencia
    ) {
        BigDecimal suma = notas.stream()
            .map(Nota::getNota)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal promedio = suma.divide(BigDecimal.valueOf(notas.size()), 2, RoundingMode.HALF_UP);
        BigDecimal notaMaxima = notas.stream().map(Nota::getNota).max(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
        BigDecimal notaMinima = notas.stream().map(Nota::getNota).min(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
        int cursosDesaprobados = (int) notas.stream().filter(nota -> nota.getNota().compareTo(BigDecimal.valueOf(11)) < 0).count();

        int clasesProgramadas = asistencia != null ? asistencia.getClasesProgramadas() : 0;
        int clasesAsistidas = asistencia != null ? asistencia.getClasesAsistidas() : 0;
        double porcentajeAsistencia = calcularPorcentajeAsistencia(clasesProgramadas, clasesAsistidas);

        PrediccionGlobalMlRequestDto global = new PrediccionGlobalMlRequestDto();
        global.setMatriculaId(matricula.getId());
        global.setPeriodoEvaluacionId(cargaExcel.getPeriodoEvaluacion().getId());
        global.setPromedioGeneral(promedio.doubleValue());
        global.setCantidadCursos(notas.size());
        global.setCantidadCursosDesaprobados(cursosDesaprobados);
        global.setNotaMaxima(notaMaxima.doubleValue());
        global.setNotaMinima(notaMinima.doubleValue());
        global.setClasesProgramadas(clasesProgramadas);
        global.setClasesAsistidas(clasesAsistidas);
        global.setPorcentajeAsistencia(porcentajeAsistencia);

        PrediccionMlRequestDto request = new PrediccionMlRequestDto();
        request.setModeloVersion("v2-fracaso");
        request.setGlobalFeatures(global);
        request.setCourseFeatures(new ArrayList<>());
        return request;
    }

    private PrediccionMlRequestDto construirRequestConsolidado(
        Matricula matricula,
        Long periodoEvaluacionId,
        List<NotaCursoPeriodoEvaluacion> notas,
        AsistenciaPeriodoEvaluacion asistencia
    ) {
        BigDecimal suma = notas.stream()
            .map(NotaCursoPeriodoEvaluacion::getPromedioCurso)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal promedio = suma.divide(BigDecimal.valueOf(notas.size()), 2, RoundingMode.HALF_UP);
        BigDecimal notaMaxima = notas.stream().map(NotaCursoPeriodoEvaluacion::getPromedioCurso).max(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
        BigDecimal notaMinima = notas.stream().map(NotaCursoPeriodoEvaluacion::getPromedioCurso).min(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
        int cursosDesaprobados = (int) notas.stream().filter(nota -> nota.getPromedioCurso().compareTo(BigDecimal.valueOf(11)) < 0).count();

        int clasesProgramadas = asistencia != null ? asistencia.getClasesProgramadas() : 0;
        int clasesAsistidas = asistencia != null ? asistencia.getClasesAsistidas() : 0;
        double porcentajeAsistencia = calcularPorcentajeAsistencia(clasesProgramadas, clasesAsistidas);

        PrediccionGlobalMlRequestDto global = new PrediccionGlobalMlRequestDto();
        global.setMatriculaId(matricula.getId());
        global.setPeriodoEvaluacionId(periodoEvaluacionId);
        global.setPromedioGeneral(promedio.doubleValue());
        global.setCantidadCursos(notas.size());
        global.setCantidadCursosDesaprobados(cursosDesaprobados);
        global.setNotaMaxima(notaMaxima.doubleValue());
        global.setNotaMinima(notaMinima.doubleValue());
        global.setClasesProgramadas(clasesProgramadas);
        global.setClasesAsistidas(clasesAsistidas);
        global.setPorcentajeAsistencia(porcentajeAsistencia);

        PrediccionMlRequestDto request = new PrediccionMlRequestDto();
        request.setModeloVersion("v2-fracaso");
        request.setGlobalFeatures(global);
        request.setCourseFeatures(construirFeaturesCurso(matricula, periodoEvaluacionId, notas, promedio.doubleValue(), cursosDesaprobados, porcentajeAsistencia));
        return request;
    }

    private List<PrediccionCursoMlDto> construirFeaturesCurso(
        Matricula matricula,
        Long periodoEvaluacionId,
        List<NotaCursoPeriodoEvaluacion> notas,
        Double promedioGeneral,
        Integer cursosDesaprobados,
        Double porcentajeAsistencia
    ) {
        List<PrediccionCursoMlDto> courseFeatures = new ArrayList<>();
        for (NotaCursoPeriodoEvaluacion nota : notas) {
            PrediccionCursoMlDto dto = new PrediccionCursoMlDto();
            dto.setMatriculaId(matricula.getId());
            dto.setCursoId(nota.getCurso().getId());
            dto.setCursoNombre(nota.getCurso().getNombre());
            dto.setPeriodoEvaluacionId(periodoEvaluacionId);
            dto.setNotaCurso(nota.getPromedioCurso().doubleValue());
            dto.setPromedioGeneral(promedioGeneral);
            dto.setCantidadCursosDesaprobados(cursosDesaprobados);
            dto.setPorcentajeAsistencia(porcentajeAsistencia);
            courseFeatures.add(dto);
        }
        return courseFeatures;
    }

    private PrediccionRiesgo guardarPrediccionGlobal(
        Long periodoEvaluacionId,
        Long cargaArchivoId,
        Matricula matricula,
        PrediccionGlobalMlResponseDto response
    ) {
        PrediccionRiesgo prediccion = prediccionRiesgoRepositorio
            .findByMatriculaIdAndPeriodoEvaluacionId(matricula.getId(), periodoEvaluacionId)
            .orElseGet(PrediccionRiesgo::new);

        prediccion.setMatricula(matricula);
        prediccion.setPeriodoEvaluacion(periodoEvaluacionRepositorio.findById(periodoEvaluacionId).orElseThrow());
        prediccion.setCargaArchivoId(cargaArchivoId);
        prediccion.setPuntajeRiesgo(BigDecimal.valueOf(response.getPuntajeRiesgo()));
        prediccion.setNivelRiesgo(response.getNivelRiesgo());
        prediccion.setModeloVersion(response.getModeloVersion());
        prediccion.setVariablesEntrada(serializarVariables(response.getVariablesEntrada()));
        prediccion.setFechaPrediccion(LocalDateTime.now());

        return prediccionRiesgoRepositorio.save(prediccion);
    }

    private void guardarPrediccionesCurso(
        Long cargaArchivoId,
        Matricula matricula,
        List<PrediccionCursoMlResponseDto> coursePredictions
    ) {
        if (coursePredictions == null || coursePredictions.isEmpty()) {
            return;
        }

        for (PrediccionCursoMlResponseDto response : coursePredictions) {
            NotaCursoPeriodoEvaluacion notaCursoPeriodoEvaluacion = notaCursoPeriodoEvaluacionRepositorio
                .findByMatriculaIdAndCursoIdAndPeriodoEvaluacionId(matricula.getId(), response.getCursoId(), response.getPeriodoEvaluacionId())
                .orElse(null);

            if (notaCursoPeriodoEvaluacion == null) {
                continue;
            }

            PrediccionRiesgoCurso prediccionCurso = prediccionRiesgoCursoRepositorio
                .findByMatriculaIdAndCursoIdAndPeriodoEvaluacionId(matricula.getId(), response.getCursoId(), response.getPeriodoEvaluacionId())
                .orElseGet(PrediccionRiesgoCurso::new);

            prediccionCurso.setMatricula(matricula);
            prediccionCurso.setCurso(notaCursoPeriodoEvaluacion.getCurso());
            prediccionCurso.setPeriodoEvaluacion(periodoEvaluacionRepositorio.findById(response.getPeriodoEvaluacionId()).orElseThrow());
            prediccionCurso.setCargaArchivoId(cargaArchivoId);
            prediccionCurso.setPuntajeRiesgo(BigDecimal.valueOf(response.getPuntajeRiesgo()));
            prediccionCurso.setNivelRiesgo(response.getNivelRiesgo());
            prediccionCurso.setModeloVersion(response.getModeloVersion());
            prediccionCurso.setVariablesEntrada(serializarVariables(response.getVariablesEntrada()));
            prediccionCurso.setFechaPrediccion(LocalDateTime.now());

            PrediccionRiesgoCurso guardada = prediccionRiesgoCursoRepositorio.save(prediccionCurso);
            generarAlertasYRecomendacionesCurso(guardada);
        }
    }

    private void generarAlertasYRecomendacionesGlobales(PrediccionRiesgo prediccionGlobal) {
        alertaRepositorio.deleteByPrediccionGlobalId(prediccionGlobal.getId());
        recomendacionRepositorio.deleteByPrediccionGlobalId(prediccionGlobal.getId());

        if ("ALTO".equalsIgnoreCase(prediccionGlobal.getNivelRiesgo()) || "MEDIO".equalsIgnoreCase(prediccionGlobal.getNivelRiesgo())) {
            Alerta alerta = new Alerta();
            alerta.setMatricula(prediccionGlobal.getMatricula());
            alerta.setPrediccionGlobal(prediccionGlobal);
            alerta.setTipoAlerta("RIESGO_GLOBAL");
            alerta.setNivelRiesgo(prediccionGlobal.getNivelRiesgo());
            alerta.setMensaje("El alumno presenta riesgo de fracaso academico global " + prediccionGlobal.getNivelRiesgo());
            alerta.setAtendida(Boolean.FALSE);
            alertaRepositorio.save(alerta);

            Recomendacion recomendacion = new Recomendacion();
            recomendacion.setMatricula(prediccionGlobal.getMatricula());
            recomendacion.setPrediccionGlobal(prediccionGlobal);
            recomendacion.setTitulo("Seguimiento de riesgo academico global");
            recomendacion.setDescripcion("Revisar la probabilidad de fracaso academico del alumno y coordinar acciones de acompanamiento con el docente tutor.");
            recomendacion.setFuente("MODELO_ML");
            recomendacionRepositorio.save(recomendacion);
        }
    }

    private void generarAlertasYRecomendacionesCurso(PrediccionRiesgoCurso prediccionCurso) {
        alertaRepositorio.deleteByPrediccionCursoId(prediccionCurso.getId());
        recomendacionRepositorio.deleteByPrediccionCursoId(prediccionCurso.getId());

        if ("ALTO".equalsIgnoreCase(prediccionCurso.getNivelRiesgo()) || "MEDIO".equalsIgnoreCase(prediccionCurso.getNivelRiesgo())) {
            Alerta alerta = new Alerta();
            alerta.setMatricula(prediccionCurso.getMatricula());
            alerta.setCurso(prediccionCurso.getCurso());
            alerta.setPrediccionCurso(prediccionCurso);
            alerta.setTipoAlerta("RIESGO_CURSO");
            alerta.setNivelRiesgo(prediccionCurso.getNivelRiesgo());
            alerta.setMensaje("El alumno presenta riesgo de fracaso " + prediccionCurso.getNivelRiesgo() + " en el curso " + prediccionCurso.getCurso().getNombre());
            alerta.setAtendida(Boolean.FALSE);
            alertaRepositorio.save(alerta);

            Recomendacion recomendacion = new Recomendacion();
            recomendacion.setMatricula(prediccionCurso.getMatricula());
            recomendacion.setCurso(prediccionCurso.getCurso());
            recomendacion.setPrediccionCurso(prediccionCurso);
            recomendacion.setTitulo("Refuerzo en " + prediccionCurso.getCurso().getNombre());
            recomendacion.setDescripcion("Aplicar seguimiento y refuerzo academico focalizado en el curso con riesgo de fracaso detectado.");
            recomendacion.setFuente("MODELO_ML");
            recomendacionRepositorio.save(recomendacion);
        }
    }

    private String serializarVariables(Object variablesEntrada) {
        try {
            return objectMapper.writeValueAsString(variablesEntrada);
        } catch (JsonProcessingException ex) {
            return "{}";
        }
    }

    private double calcularPorcentajeAsistencia(int clasesProgramadas, int clasesAsistidas) {
        if (clasesProgramadas == 0) {
            return 0.0;
        }
        return BigDecimal.valueOf(clasesAsistidas)
            .multiply(BigDecimal.valueOf(100))
            .divide(BigDecimal.valueOf(clasesProgramadas), 2, RoundingMode.HALF_UP)
            .doubleValue();
    }

    private PrediccionRiesgoRespuestaDto mapearRespuestaGlobal(PrediccionRiesgo prediccion) {
        PrediccionRiesgoRespuestaDto dto = new PrediccionRiesgoRespuestaDto();
        dto.setId(prediccion.getId());
        dto.setPuntajeRiesgo(prediccion.getPuntajeRiesgo());
        dto.setNivelRiesgo(prediccion.getNivelRiesgo());
        dto.setModeloVersion(prediccion.getModeloVersion());
        dto.setVariablesEntrada(prediccion.getVariablesEntrada());
        dto.setFechaPrediccion(prediccion.getFechaPrediccion());

        if (prediccion.getPeriodoEvaluacion() != null) {
            dto.setPeriodoEvaluacionId(prediccion.getPeriodoEvaluacion().getId());
            dto.setNumeroPeriodoEvaluacion(prediccion.getPeriodoEvaluacion().getNumero());
            dto.setNombrePeriodoEvaluacion(prediccion.getPeriodoEvaluacion().getNombre());
        }

        if (prediccion.getMatricula() != null) {
            dto.setMatriculaId(prediccion.getMatricula().getId());
            if (prediccion.getMatricula().getAlumno() != null) {
                dto.setAlumnoId(prediccion.getMatricula().getAlumno().getId());
                dto.setCodigoAlumno(prediccion.getMatricula().getAlumno().getCodigo());
                dto.setAlumnoNombreCompleto(
                    prediccion.getMatricula().getAlumno().getNombres() + " " +
                    prediccion.getMatricula().getAlumno().getApellidos()
                );
            }
            if (prediccion.getMatricula().getGrado() != null) {
                dto.setGrado(prediccion.getMatricula().getGrado().getNombre());
                if (prediccion.getMatricula().getGrado().getNivel() != null) {
                    dto.setNivel(prediccion.getMatricula().getGrado().getNivel().getNombre());
                }
            }
            if (prediccion.getMatricula().getSeccion() != null) {
                dto.setSeccion(prediccion.getMatricula().getSeccion().getNombre());
            }
            if (prediccion.getMatricula().getPeriodoAcademico() != null) {
                dto.setPeriodoAcademicoId(prediccion.getMatricula().getPeriodoAcademico().getId());
                dto.setAnioAcademico(prediccion.getMatricula().getPeriodoAcademico().getAnio());
            }
        }

        return dto;
    }

    private PrediccionRiesgoRespuestaDto mapearRespuestaCurso(PrediccionRiesgoCurso prediccion) {
        PrediccionRiesgoRespuestaDto dto = new PrediccionRiesgoRespuestaDto();
        dto.setId(prediccion.getId());
        dto.setPuntajeRiesgo(prediccion.getPuntajeRiesgo());
        dto.setNivelRiesgo(prediccion.getNivelRiesgo());
        dto.setModeloVersion(prediccion.getModeloVersion());
        dto.setVariablesEntrada(prediccion.getVariablesEntrada());
        dto.setFechaPrediccion(prediccion.getFechaPrediccion());
        dto.setCursoId(prediccion.getCurso().getId());
        dto.setCurso(prediccion.getCurso().getNombre());

        if (prediccion.getPeriodoEvaluacion() != null) {
            dto.setPeriodoEvaluacionId(prediccion.getPeriodoEvaluacion().getId());
            dto.setNumeroPeriodoEvaluacion(prediccion.getPeriodoEvaluacion().getNumero());
            dto.setNombrePeriodoEvaluacion(prediccion.getPeriodoEvaluacion().getNombre());
        }

        if (prediccion.getMatricula() != null) {
            dto.setMatriculaId(prediccion.getMatricula().getId());
            if (prediccion.getMatricula().getAlumno() != null) {
                dto.setAlumnoId(prediccion.getMatricula().getAlumno().getId());
                dto.setCodigoAlumno(prediccion.getMatricula().getAlumno().getCodigo());
                dto.setAlumnoNombreCompleto(
                    prediccion.getMatricula().getAlumno().getNombres() + " " +
                    prediccion.getMatricula().getAlumno().getApellidos()
                );
            }
            if (prediccion.getMatricula().getGrado() != null) {
                dto.setGrado(prediccion.getMatricula().getGrado().getNombre());
                if (prediccion.getMatricula().getGrado().getNivel() != null) {
                    dto.setNivel(prediccion.getMatricula().getGrado().getNivel().getNombre());
                }
            }
            if (prediccion.getMatricula().getSeccion() != null) {
                dto.setSeccion(prediccion.getMatricula().getSeccion().getNombre());
            }
            if (prediccion.getMatricula().getPeriodoAcademico() != null) {
                dto.setPeriodoAcademicoId(prediccion.getMatricula().getPeriodoAcademico().getId());
                dto.setAnioAcademico(prediccion.getMatricula().getPeriodoAcademico().getAnio());
            }
        }

        return dto;
    }
}
