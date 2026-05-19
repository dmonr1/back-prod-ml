package com.tp1.proyecto.alerta.servicio.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tp1.proyecto.academico.entidad.PeriodoEvaluacion;
import com.tp1.proyecto.academico.entidad.Seccion;
import com.tp1.proyecto.academico.repositorio.PeriodoEvaluacionRepositorio;
import com.tp1.proyecto.academico.repositorio.SeccionRepositorio;
import com.tp1.proyecto.alerta.dto.HallazgoDataMiningRespuestaDto;
import com.tp1.proyecto.alerta.entidad.Alerta;
import com.tp1.proyecto.alerta.entidad.HallazgoDataMining;
import com.tp1.proyecto.alerta.repositorio.AlertaRepositorio;
import com.tp1.proyecto.alerta.repositorio.HallazgoDataMiningRepositorio;
import com.tp1.proyecto.alerta.servicio.HallazgoDataMiningServicio;
import com.tp1.proyecto.comun.enumeracion.EstadoRegistro;
import com.tp1.proyecto.evaluacion.entidad.AsistenciaPeriodoEvaluacion;
import com.tp1.proyecto.evaluacion.repositorio.AsistenciaPeriodoEvaluacionRepositorio;
import com.tp1.proyecto.excepcion.RecursoNoEncontradoException;
import com.tp1.proyecto.excepcion.ReglaNegocioException;
import com.tp1.proyecto.prediccion.entidad.PrediccionRiesgo;
import com.tp1.proyecto.prediccion.entidad.PrediccionRiesgoCurso;
import com.tp1.proyecto.prediccion.repositorio.PrediccionRiesgoCursoRepositorio;
import com.tp1.proyecto.prediccion.repositorio.PrediccionRiesgoRepositorio;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class HallazgoDataMiningServicioImpl implements HallazgoDataMiningServicio {

    private static final double UMBRAL_ASISTENCIA_CRITICA = 60.0;

    private final HallazgoDataMiningRepositorio hallazgoDataMiningRepositorio;
    private final PeriodoEvaluacionRepositorio periodoEvaluacionRepositorio;
    private final SeccionRepositorio seccionRepositorio;
    private final PrediccionRiesgoRepositorio prediccionRiesgoRepositorio;
    private final PrediccionRiesgoCursoRepositorio prediccionRiesgoCursoRepositorio;
    private final AlertaRepositorio alertaRepositorio;
    private final AsistenciaPeriodoEvaluacionRepositorio asistenciaPeriodoEvaluacionRepositorio;
    private final ObjectMapper objectMapper;

    public HallazgoDataMiningServicioImpl(
        HallazgoDataMiningRepositorio hallazgoDataMiningRepositorio,
        PeriodoEvaluacionRepositorio periodoEvaluacionRepositorio,
        SeccionRepositorio seccionRepositorio,
        PrediccionRiesgoRepositorio prediccionRiesgoRepositorio,
        PrediccionRiesgoCursoRepositorio prediccionRiesgoCursoRepositorio,
        AlertaRepositorio alertaRepositorio,
        AsistenciaPeriodoEvaluacionRepositorio asistenciaPeriodoEvaluacionRepositorio,
        ObjectMapper objectMapper
    ) {
        this.hallazgoDataMiningRepositorio = hallazgoDataMiningRepositorio;
        this.periodoEvaluacionRepositorio = periodoEvaluacionRepositorio;
        this.seccionRepositorio = seccionRepositorio;
        this.prediccionRiesgoRepositorio = prediccionRiesgoRepositorio;
        this.prediccionRiesgoCursoRepositorio = prediccionRiesgoCursoRepositorio;
        this.alertaRepositorio = alertaRepositorio;
        this.asistenciaPeriodoEvaluacionRepositorio = asistenciaPeriodoEvaluacionRepositorio;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<HallazgoDataMiningRespuestaDto> listarHallazgos(Long periodoEvaluacionId, Long seccionId) {
        return hallazgoDataMiningRepositorio
            .findByPeriodoEvaluacionIdAndSeccionIdOrderByFechaGeneracionDescIdDesc(periodoEvaluacionId, seccionId)
            .stream()
            .map(this::mapearRespuesta)
            .toList();
    }

    @Override
    public List<HallazgoDataMiningRespuestaDto> generarHallazgos(Long periodoEvaluacionId, Long seccionId) {
        PeriodoEvaluacion periodoEvaluacion = periodoEvaluacionRepositorio.findById(periodoEvaluacionId)
            .orElseThrow(() -> new RecursoNoEncontradoException(
                "Periodo de evaluacion no encontrado con id: " + periodoEvaluacionId
            ));

        Seccion seccion = seccionRepositorio.findById(seccionId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Seccion no encontrada con id: " + seccionId));

        if (!seccion.getPeriodoAcademico().getId().equals(periodoEvaluacion.getPeriodoAcademico().getId())) {
            throw new ReglaNegocioException(
                "La seccion no pertenece al mismo periodo academico del periodo de evaluacion seleccionado."
            );
        }

        List<PrediccionRiesgo> prediccionesGlobales = prediccionRiesgoRepositorio
            .findByPeriodoEvaluacionIdAndMatriculaSeccionId(periodoEvaluacionId, seccionId)
            .stream()
            .filter(prediccion -> prediccion.getEstado() == EstadoRegistro.ACTIVO)
            .toList();

        List<PrediccionRiesgoCurso> prediccionesCurso = prediccionRiesgoCursoRepositorio
            .findByPeriodoEvaluacionIdAndMatriculaSeccionId(periodoEvaluacionId, seccionId)
            .stream()
            .filter(prediccion -> prediccion.getEstado() == EstadoRegistro.ACTIVO)
            .toList();

        List<Alerta> alertas = alertaRepositorio.findByPeriodoEvaluacionIdAndSeccionId(periodoEvaluacionId, seccionId)
            .stream()
            .filter(alerta -> alerta.getEstado() == EstadoRegistro.ACTIVO)
            .toList();

        hallazgoDataMiningRepositorio.deleteByPeriodoEvaluacionIdAndSeccionId(periodoEvaluacionId, seccionId);

        List<HallazgoDataMining> hallazgos = List.of(
            construirHallazgoCursoCritico(periodoEvaluacion, seccion, prediccionesCurso, prediccionesGlobales.size()),
            construirHallazgoAsistenciaCritica(periodoEvaluacion, seccion, prediccionesGlobales),
            construirHallazgoFactorPredominante(periodoEvaluacion, seccion, prediccionesGlobales),
            construirHallazgoMultiplesAlertas(periodoEvaluacion, seccion, alertas),
            construirHallazgoRiesgoGlobalSeccion(periodoEvaluacion, seccion, prediccionesGlobales),
            construirHallazgoCursosRecurrentes(periodoEvaluacion, seccion, alertas, prediccionesCurso),
            construirHallazgoBajoRendimientoGeneral(periodoEvaluacion, seccion, prediccionesGlobales),
            construirHallazgoPrediccionInestable(periodoEvaluacion, seccion, prediccionesGlobales)
        );

        return hallazgoDataMiningRepositorio.saveAll(hallazgos)
            .stream()
            .map(this::mapearRespuesta)
            .toList();
    }

    private HallazgoDataMining construirHallazgoCursoCritico(
        PeriodoEvaluacion periodoEvaluacion,
        Seccion seccion,
        List<PrediccionRiesgoCurso> prediccionesCurso,
        int totalAlumnosSeccion
    ) {
        Map<Long, CursoResumen> resumenPorCurso = new LinkedHashMap<>();

        for (PrediccionRiesgoCurso prediccion : prediccionesCurso) {
            CursoResumen resumen = resumenPorCurso.computeIfAbsent(
                prediccion.getCurso().getId(),
                ignored -> new CursoResumen(prediccion.getCurso().getId(), prediccion.getCurso().getNombre())
            );
            resumen.total++;
            if ("ALTO".equalsIgnoreCase(prediccion.getNivelRiesgo())) {
                resumen.alto++;
            } else if ("MEDIO".equalsIgnoreCase(prediccion.getNivelRiesgo())) {
                resumen.medio++;
            }
        }

        Optional<CursoResumen> cursoCritico = resumenPorCurso.values().stream()
            .max(Comparator
                .comparingInt(CursoResumen::pesoCriticidad)
                .thenComparingInt(resumen -> resumen.alto)
                .thenComparingInt(resumen -> resumen.medio)
                .thenComparingInt(resumen -> resumen.total));

        String descripcion;
        String nivelRelevancia;
        Map<String, Object> resultado = new LinkedHashMap<>();
        HallazgoDataMining hallazgo = crearBaseHallazgo(periodoEvaluacion, seccion);

        hallazgo.setCodigo("CURSO_CRITICO");
        hallazgo.setTipo("RENDIMIENTO");
        hallazgo.setTitulo("Curso critico detectado");
        hallazgo.setFuente("AGREGACION");

        if (cursoCritico.isPresent()) {
            CursoResumen resumen = cursoCritico.get();
            double porcentaje = totalAlumnosSeccion > 0
                ? redondear((double) resumen.alto * 100.0 / totalAlumnosSeccion)
                : 0.0;

            descripcion = resumen.alto > 0
                ? "El curso " + resumen.nombre + " concentra la mayor cantidad de casos de riesgo alto en la seccion."
                : "No se detectaron riesgos altos por curso; " + resumen.nombre + " concentra el mayor monitoreo relativo.";

            nivelRelevancia = resumen.alto >= 3 || porcentaje >= 30.0
                ? "ALTO"
                : (resumen.alto > 0 || resumen.medio > 0 ? "MEDIO" : "BAJO");

            resultado.put("curso_id", resumen.cursoId);
            resultado.put("curso_nombre", resumen.nombre);
            resultado.put("casos_riesgo_alto", resumen.alto);
            resultado.put("casos_riesgo_medio", resumen.medio);
            resultado.put("total_casos", resumen.total);
            resultado.put("porcentaje_seccion", porcentaje);

            hallazgo.setCurso(prediccionesCurso.stream()
                .filter(prediccion -> prediccion.getCurso().getId().equals(resumen.cursoId))
                .findFirst()
                .map(PrediccionRiesgoCurso::getCurso)
                .orElse(null));
        } else {
            descripcion = "No hay predicciones por curso registradas para analizar criticidad en la seccion.";
            nivelRelevancia = "BAJO";
            resultado.put("curso_id", null);
            resultado.put("curso_nombre", null);
            resultado.put("casos_riesgo_alto", 0);
            resultado.put("casos_riesgo_medio", 0);
            resultado.put("total_casos", 0);
            resultado.put("porcentaje_seccion", 0.0);
        }

        hallazgo.setDescripcion(descripcion);
        hallazgo.setNivelRelevancia(nivelRelevancia);
        hallazgo.setResultado(serializarResultado(resultado));
        return hallazgo;
    }

    private HallazgoDataMining construirHallazgoAsistenciaCritica(
        PeriodoEvaluacion periodoEvaluacion,
        Seccion seccion,
        List<PrediccionRiesgo> prediccionesGlobales
    ) {
        int totalConDatos = 0;
        int cantidadCritica = 0;
        double sumaAsistencia = 0.0;

        for (PrediccionRiesgo prediccion : prediccionesGlobales) {
            Optional<AsistenciaPeriodoEvaluacion> asistenciaOpt = asistenciaPeriodoEvaluacionRepositorio
                .findByMatriculaIdAndPeriodoEvaluacionId(prediccion.getMatricula().getId(), periodoEvaluacion.getId());

            if (asistenciaOpt.isEmpty()) {
                continue;
            }

            AsistenciaPeriodoEvaluacion asistencia = asistenciaOpt.get();
            double porcentaje = porcentajeAsistencia(asistencia.getClasesAsistidas(), asistencia.getClasesProgramadas());
            sumaAsistencia += porcentaje;
            totalConDatos++;

            if (porcentaje < UMBRAL_ASISTENCIA_CRITICA) {
                cantidadCritica++;
            }
        }

        double promedioAsistencia = totalConDatos > 0 ? redondear(sumaAsistencia / totalConDatos) : 0.0;
        double porcentajeCritico = totalConDatos > 0 ? redondear((double) cantidadCritica * 100.0 / totalConDatos) : 0.0;

        HallazgoDataMining hallazgo = crearBaseHallazgo(periodoEvaluacion, seccion);
        hallazgo.setCodigo("ASISTENCIA_CRITICA");
        hallazgo.setTipo("ASISTENCIA");
        hallazgo.setTitulo("Asistencia critica en la seccion");
        hallazgo.setFuente("AGREGACION");
        hallazgo.setDescripcion(
            cantidadCritica > 0
                ? "Se detectaron " + cantidadCritica + " alumnos con asistencia menor al umbral esperado en el periodo."
                : "No se detectaron alumnos con asistencia critica en el periodo evaluado."
        );
        hallazgo.setNivelRelevancia(
            porcentajeCritico >= 30.0 || cantidadCritica >= 3
                ? "ALTO"
                : (cantidadCritica > 0 ? "MEDIO" : "BAJO")
        );
        hallazgo.setResultado(serializarResultado(Map.of(
            "umbral_asistencia", UMBRAL_ASISTENCIA_CRITICA,
            "cantidad_alumnos", cantidadCritica,
            "porcentaje_alumnos", porcentajeCritico,
            "promedio_asistencia_seccion", promedioAsistencia,
            "alumnos_con_datos", totalConDatos
        )));
        return hallazgo;
    }

    private HallazgoDataMining construirHallazgoFactorPredominante(
        PeriodoEvaluacion periodoEvaluacion,
        Seccion seccion,
        List<PrediccionRiesgo> prediccionesGlobales
    ) {
        int asistencia = 0;
        int rendimiento = 0;
        int mixto = 0;
        int controlado = 0;

        List<PrediccionRiesgo> priorizadas = prediccionesGlobales.stream()
            .filter(prediccion ->
                "ALTO".equalsIgnoreCase(prediccion.getNivelRiesgo()) ||
                "MEDIO".equalsIgnoreCase(prediccion.getNivelRiesgo()))
            .toList();

        List<PrediccionRiesgo> baseAnalisis = priorizadas.isEmpty() ? prediccionesGlobales : priorizadas;

        for (PrediccionRiesgo prediccion : baseAnalisis) {
            String factor = detectarFactor(prediccion.getVariablesEntrada());
            switch (factor) {
                case "ASISTENCIA":
                    asistencia++;
                    break;
                case "RENDIMIENTO":
                    rendimiento++;
                    break;
                case "MIXTO":
                    mixto++;
                    break;
                default:
                    controlado++;
                    break;
            }
        }

        Map<String, Integer> conteos = new LinkedHashMap<>();
        conteos.put("ASISTENCIA", asistencia);
        conteos.put("RENDIMIENTO", rendimiento);
        conteos.put("MIXTO", mixto);
        conteos.put("CONTROLADO", controlado);

        Map.Entry<String, Integer> dominante = conteos.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .orElse(Map.entry("CONTROLADO", 0));

        int total = Math.max(baseAnalisis.size(), 1);
        double porcentaje = redondear((double) dominante.getValue() * 100.0 / total);

        HallazgoDataMining hallazgo = crearBaseHallazgo(periodoEvaluacion, seccion);
        hallazgo.setCodigo("FACTOR_PREDOMINANTE");
        hallazgo.setTipo("RIESGO");
        hallazgo.setTitulo("Factor de riesgo predominante");
        hallazgo.setFuente("PATRON");
        hallazgo.setDescripcion(
            dominante.getValue() > 0
                ? "El factor que aparece con mayor frecuencia en los casos priorizados es " +
                    humanizarFactor(dominante.getKey()) + "."
                : "No se detectaron factores predominantes en los casos analizados."
        );
        hallazgo.setNivelRelevancia(
            "CONTROLADO".equals(dominante.getKey())
                ? "BAJO"
                : (porcentaje >= 60.0 ? "ALTO" : "MEDIO")
        );
        hallazgo.setResultado(serializarResultado(Map.of(
            "factor", dominante.getKey(),
            "casos_asistencia", asistencia,
            "casos_rendimiento", rendimiento,
            "casos_mixtos", mixto,
            "casos_controlados", controlado,
            "porcentaje_factor", porcentaje
        )));
        return hallazgo;
    }

    private HallazgoDataMining construirHallazgoMultiplesAlertas(
        PeriodoEvaluacion periodoEvaluacion,
        Seccion seccion,
        List<Alerta> alertas
    ) {
        Map<Long, List<Alerta>> alertasPorAlumno = alertas.stream()
            .collect(Collectors.groupingBy(alerta -> alerta.getMatricula().getAlumno().getId(), LinkedHashMap::new, Collectors.toList()));

        List<Map<String, Object>> alumnos = new ArrayList<>();
        int totalAlertas = 0;

        for (List<Alerta> alertasAlumno : alertasPorAlumno.values()) {
            if (alertasAlumno.size() <= 1) {
                continue;
            }

            Alerta referencia = alertasAlumno.get(0);
            alumnos.add(Map.of(
                "alumno_id", referencia.getMatricula().getAlumno().getId(),
                "nombre", referencia.getMatricula().getAlumno().getNombres() + " " +
                    referencia.getMatricula().getAlumno().getApellidos(),
                "alertas_activas", alertasAlumno.size()
            ));
            totalAlertas += alertasAlumno.size();
        }

        HallazgoDataMining hallazgo = crearBaseHallazgo(periodoEvaluacion, seccion);
        hallazgo.setCodigo("MULTIPLES_ALERTAS");
        hallazgo.setTipo("SEGUIMIENTO");
        hallazgo.setTitulo("Alumnos con multiples alertas");
        hallazgo.setFuente("AGREGACION");
        hallazgo.setDescripcion(
            alumnos.isEmpty()
                ? "No se detectaron alumnos con multiples alertas activas en el periodo."
                : "Se identificaron alumnos que acumulan mas de una alerta activa en el periodo y requieren seguimiento prioritario."
        );
        hallazgo.setNivelRelevancia(
            alumnos.size() >= 3 || totalAlertas >= 6
                ? "ALTO"
                : (alumnos.isEmpty() ? "BAJO" : "MEDIO")
        );

        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("cantidad_alumnos", alumnos.size());
        resultado.put("total_alertas", totalAlertas);
        resultado.put("alumnos", alumnos);
        hallazgo.setResultado(serializarResultado(resultado));
        return hallazgo;
    }

    private HallazgoDataMining construirHallazgoRiesgoGlobalSeccion(
        PeriodoEvaluacion periodoEvaluacion,
        Seccion seccion,
        List<PrediccionRiesgo> prediccionesGlobales
    ) {
        int alto = 0;
        int medio = 0;
        int bajo = 0;

        for (PrediccionRiesgo prediccion : prediccionesGlobales) {
            if ("ALTO".equalsIgnoreCase(prediccion.getNivelRiesgo())) {
                alto++;
            } else if ("MEDIO".equalsIgnoreCase(prediccion.getNivelRiesgo())) {
                medio++;
            } else {
                bajo++;
            }
        }

        int total = prediccionesGlobales.size();
        int prioritarios = alto + medio;
        double porcentajePrioritarios = total > 0 ? redondear((double) prioritarios * 100.0 / total) : 0.0;

        HallazgoDataMining hallazgo = crearBaseHallazgo(periodoEvaluacion, seccion);
        hallazgo.setCodigo("RIESGO_GLOBAL_SECCION");
        hallazgo.setTipo("RIESGO");
        hallazgo.setTitulo("Panorama global de riesgo de la seccion");
        hallazgo.setFuente("AGREGACION");
        hallazgo.setDescripcion(
            total == 0
                ? "Aun no hay predicciones globales registradas para la seccion en este periodo."
                : "La seccion presenta " + prioritarios + " casos prioritarios de riesgo alto o medio en el periodo."
        );
        hallazgo.setNivelRelevancia(
            porcentajePrioritarios >= 40.0 || alto >= 3
                ? "ALTO"
                : (prioritarios > 0 ? "MEDIO" : "BAJO")
        );
        hallazgo.setResultado(serializarResultado(Map.of(
            "total_evaluados", total,
            "riesgo_alto", alto,
            "riesgo_medio", medio,
            "riesgo_bajo", bajo,
            "casos_prioritarios", prioritarios,
            "porcentaje_prioritarios", porcentajePrioritarios
        )));
        return hallazgo;
    }

    private HallazgoDataMining construirHallazgoCursosRecurrentes(
        PeriodoEvaluacion periodoEvaluacion,
        Seccion seccion,
        List<Alerta> alertas,
        List<PrediccionRiesgoCurso> prediccionesCurso
    ) {
        Map<Long, CursoResumen> resumenPorCurso = new LinkedHashMap<>();

        for (Alerta alerta : alertas) {
            if (alerta.getCurso() == null) {
                continue;
            }
            CursoResumen resumen = resumenPorCurso.computeIfAbsent(
                alerta.getCurso().getId(),
                ignored -> new CursoResumen(alerta.getCurso().getId(), alerta.getCurso().getNombre())
            );
            resumen.total++;
            if ("ALTO".equalsIgnoreCase(alerta.getNivelRiesgo())) {
                resumen.alto++;
            } else if ("MEDIO".equalsIgnoreCase(alerta.getNivelRiesgo())) {
                resumen.medio++;
            }
        }

        if (resumenPorCurso.isEmpty()) {
            for (PrediccionRiesgoCurso prediccion : prediccionesCurso) {
                if (!"ALTO".equalsIgnoreCase(prediccion.getNivelRiesgo()) &&
                    !"MEDIO".equalsIgnoreCase(prediccion.getNivelRiesgo())) {
                    continue;
                }
                CursoResumen resumen = resumenPorCurso.computeIfAbsent(
                    prediccion.getCurso().getId(),
                    ignored -> new CursoResumen(prediccion.getCurso().getId(), prediccion.getCurso().getNombre())
                );
                resumen.total++;
                if ("ALTO".equalsIgnoreCase(prediccion.getNivelRiesgo())) {
                    resumen.alto++;
                } else {
                    resumen.medio++;
                }
            }
        }

        List<CursoResumen> topCursos = resumenPorCurso.values().stream()
            .sorted(Comparator.comparingInt(CursoResumen::pesoCriticidad).reversed())
            .limit(3)
            .toList();

        HallazgoDataMining hallazgo = crearBaseHallazgo(periodoEvaluacion, seccion);
        hallazgo.setCodigo("CURSOS_RECURRENTES");
        hallazgo.setTipo("RENDIMIENTO");
        hallazgo.setTitulo("Cursos con recurrencia de alertas");
        hallazgo.setFuente("AGREGACION");

        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put(
            "top_cursos",
            topCursos.stream()
                .map(curso -> Map.of(
                    "curso_id", curso.cursoId,
                    "curso_nombre", curso.nombre,
                    "casos_riesgo_alto", curso.alto,
                    "casos_riesgo_medio", curso.medio,
                    "total_casos", curso.total
                ))
                .toList()
        );
        resultado.put("cantidad_cursos", topCursos.size());

        if (topCursos.isEmpty()) {
            hallazgo.setDescripcion("No se detectaron cursos con recurrencia de alertas o riesgo priorizado.");
            hallazgo.setNivelRelevancia("BAJO");
        } else {
            String nombres = topCursos.stream().map(curso -> curso.nombre).collect(Collectors.joining(", "));
            hallazgo.setDescripcion("Los cursos con mayor recurrencia de alertas o riesgo priorizado son: " + nombres + ".");
            hallazgo.setNivelRelevancia(topCursos.get(0).pesoCriticidad() >= 4 ? "ALTO" : "MEDIO");
        }

        hallazgo.setResultado(serializarResultado(resultado));
        return hallazgo;
    }

    private HallazgoDataMining construirHallazgoBajoRendimientoGeneral(
        PeriodoEvaluacion periodoEvaluacion,
        Seccion seccion,
        List<PrediccionRiesgo> prediccionesGlobales
    ) {
        int cantidad = 0;
        int totalConPromedio = 0;
        double sumaPromedios = 0.0;

        for (PrediccionRiesgo prediccion : prediccionesGlobales) {
            Map<String, Object> variables = parsearVariables(prediccion.getVariablesEntrada());
            double promedio = obtenerNumero(variables.get("promedio_general"));

            if (promedio <= 0) {
                continue;
            }

            totalConPromedio++;
            sumaPromedios += promedio;

            if (promedio < 14.0) {
                cantidad++;
            }
        }

        double promedioSeccion = totalConPromedio > 0 ? redondear(sumaPromedios / totalConPromedio) : 0.0;
        double porcentaje = totalConPromedio > 0 ? redondear((double) cantidad * 100.0 / totalConPromedio) : 0.0;

        HallazgoDataMining hallazgo = crearBaseHallazgo(periodoEvaluacion, seccion);
        hallazgo.setCodigo("BAJO_RENDIMIENTO_GENERAL");
        hallazgo.setTipo("RENDIMIENTO");
        hallazgo.setTitulo("Bajo rendimiento general detectado");
        hallazgo.setFuente("PATRON");
        hallazgo.setDescripcion(
            cantidad > 0
                ? "Se identificaron " + cantidad + " alumnos con rendimiento general por debajo del umbral esperado."
                : "No se detectaron alumnos con bajo rendimiento general en la seccion."
        );
        hallazgo.setNivelRelevancia(
            porcentaje >= 40.0 || cantidad >= 4
                ? "ALTO"
                : (cantidad > 0 ? "MEDIO" : "BAJO")
        );
        hallazgo.setResultado(serializarResultado(Map.of(
            "umbral_promedio", 14.0,
            "cantidad_alumnos", cantidad,
            "porcentaje_alumnos", porcentaje,
            "promedio_grupal", promedioSeccion,
            "alumnos_con_datos", totalConPromedio
        )));
        return hallazgo;
    }

    private HallazgoDataMining construirHallazgoPrediccionInestable(
        PeriodoEvaluacion periodoEvaluacion,
        Seccion seccion,
        List<PrediccionRiesgo> prediccionesGlobales
    ) {
        int cantidad = 0;
        int totalConDatos = 0;
        int sumaEvaluaciones = 0;

        for (PrediccionRiesgo prediccion : prediccionesGlobales) {
            Map<String, Object> variables = parsearVariables(prediccion.getVariablesEntrada());
            int evaluaciones = (int) obtenerNumero(variables.get("cantidad_evaluaciones_registradas"));

            totalConDatos++;
            sumaEvaluaciones += evaluaciones;

            if (evaluaciones <= 1) {
                cantidad++;
            }
        }

        double promedioEvaluaciones = totalConDatos > 0 ? redondear((double) sumaEvaluaciones / totalConDatos) : 0.0;
        double porcentaje = totalConDatos > 0 ? redondear((double) cantidad * 100.0 / totalConDatos) : 0.0;

        HallazgoDataMining hallazgo = crearBaseHallazgo(periodoEvaluacion, seccion);
        hallazgo.setCodigo("PREDICCION_INESTABLE");
        hallazgo.setTipo("SEGUIMIENTO");
        hallazgo.setTitulo("Prediccion con datos aun inestables");
        hallazgo.setFuente("REGLA");
        hallazgo.setDescripcion(
            cantidad > 0
                ? "Aun existen alumnos con pocas evaluaciones registradas para una lectura mas estable del riesgo."
                : "La seccion ya cuenta con una base de evaluaciones suficiente para lecturas mas estables."
        );
        hallazgo.setNivelRelevancia(
            porcentaje >= 40.0 || cantidad >= 4
                ? "ALTO"
                : (cantidad > 0 ? "MEDIO" : "BAJO")
        );
        hallazgo.setResultado(serializarResultado(Map.of(
            "cantidad_alumnos", cantidad,
            "porcentaje_alumnos", porcentaje,
            "promedio_evaluaciones_registradas", promedioEvaluaciones,
            "alumnos_con_datos", totalConDatos
        )));
        return hallazgo;
    }

    private HallazgoDataMining crearBaseHallazgo(PeriodoEvaluacion periodoEvaluacion, Seccion seccion) {
        HallazgoDataMining hallazgo = new HallazgoDataMining();
        hallazgo.setPeriodoAcademico(periodoEvaluacion.getPeriodoAcademico());
        hallazgo.setPeriodoEvaluacion(periodoEvaluacion);
        hallazgo.setSeccion(seccion);
        hallazgo.setFechaGeneracion(LocalDateTime.now());
        hallazgo.setEstado(EstadoRegistro.ACTIVO);
        return hallazgo;
    }

    private HallazgoDataMiningRespuestaDto mapearRespuesta(HallazgoDataMining hallazgo) {
        HallazgoDataMiningRespuestaDto dto = new HallazgoDataMiningRespuestaDto();
        dto.setId(hallazgo.getId());
        dto.setPeriodoAcademicoId(hallazgo.getPeriodoAcademico().getId());
        if (hallazgo.getPeriodoEvaluacion() != null) {
            dto.setPeriodoEvaluacionId(hallazgo.getPeriodoEvaluacion().getId());
            dto.setPeriodoEvaluacion(hallazgo.getPeriodoEvaluacion().getNombre());
        }
        if (hallazgo.getSeccion() != null) {
            dto.setSeccionId(hallazgo.getSeccion().getId());
            dto.setSeccion(hallazgo.getSeccion().getNombre());
        }
        if (hallazgo.getCurso() != null) {
            dto.setCursoId(hallazgo.getCurso().getId());
            dto.setCurso(hallazgo.getCurso().getNombre());
        }
        dto.setCodigo(hallazgo.getCodigo());
        dto.setTipo(hallazgo.getTipo());
        dto.setTitulo(hallazgo.getTitulo());
        dto.setDescripcion(hallazgo.getDescripcion());
        dto.setNivelRelevancia(hallazgo.getNivelRelevancia());
        dto.setFuente(hallazgo.getFuente());
        dto.setResultado(hallazgo.getResultado());
        dto.setFechaGeneracion(hallazgo.getFechaGeneracion());
        return dto;
    }

    private String detectarFactor(String variablesEntrada) {
        Map<String, Object> variables = parsearVariables(variablesEntrada);
        double asistencia = obtenerNumero(variables.get("porcentaje_asistencia"));
        double promedio = obtenerNumero(variables.get("promedio_general"));
        double notaMinima = obtenerNumero(variables.get("nota_minima"));
        double desaprobados = obtenerNumero(variables.get("cantidad_cursos_desaprobados"));

        boolean asistenciaCritica = asistencia > 0 && asistencia < 60;
        boolean rendimientoBajo =
            (promedio > 0 && promedio <= 10.5) ||
            (notaMinima > 0 && notaMinima <= 10.5) ||
            desaprobados > 0;
        boolean combinado = asistencia > 0 && asistencia < 80 && promedio > 0 && promedio < 14;

        if (asistenciaCritica && (rendimientoBajo || combinado)) {
            return "MIXTO";
        }
        if (asistenciaCritica) {
            return "ASISTENCIA";
        }
        if (rendimientoBajo || combinado) {
            return "RENDIMIENTO";
        }
        return "CONTROLADO";
    }

    private Map<String, Object> parsearVariables(String variablesEntrada) {
        if (variablesEntrada == null || variablesEntrada.isBlank()) {
            return Map.of();
        }

        try {
            return objectMapper.readValue(variablesEntrada, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException ex) {
            return Map.of();
        }
    }

    private double obtenerNumero(Object valor) {
        if (valor == null) {
            return 0.0;
        }

        try {
            return Double.parseDouble(String.valueOf(valor));
        } catch (NumberFormatException ex) {
            return 0.0;
        }
    }

    private double porcentajeAsistencia(Integer asistidas, Integer programadas) {
        if (asistidas == null || programadas == null || programadas <= 0) {
            return 0.0;
        }
        return redondear((double) asistidas * 100.0 / programadas);
    }

    private double redondear(double valor) {
        return Math.round(valor * 10.0) / 10.0;
    }

    private String serializarResultado(Map<String, Object> resultado) {
        try {
            return objectMapper.writeValueAsString(resultado);
        } catch (JsonProcessingException ex) {
            return "{}";
        }
    }

    private String humanizarFactor(String factor) {
        switch (factor) {
            case "ASISTENCIA":
                return "la asistencia";
            case "RENDIMIENTO":
                return "el rendimiento academico";
            case "MIXTO":
                return "la combinacion de asistencia y rendimiento";
            default:
                return "indicadores actualmente controlados";
        }
    }

    private static final class CursoResumen {
        private final Long cursoId;
        private final String nombre;
        private int alto;
        private int medio;
        private int total;

        private CursoResumen(Long cursoId, String nombre) {
            this.cursoId = cursoId;
            this.nombre = nombre;
        }

        private int pesoCriticidad() {
            return (alto * 2) + medio;
        }
    }
}
