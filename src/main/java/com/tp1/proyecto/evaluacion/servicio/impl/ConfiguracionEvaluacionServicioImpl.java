package com.tp1.proyecto.evaluacion.servicio.impl;

import com.tp1.proyecto.academico.entidad.CursoPeriodoAcademico;
import com.tp1.proyecto.academico.entidad.DocenteCursoSeccion;
import com.tp1.proyecto.academico.entidad.Curso;
import com.tp1.proyecto.academico.entidad.Grado;
import com.tp1.proyecto.academico.entidad.Matricula;
import com.tp1.proyecto.academico.entidad.PeriodoAcademico;
import com.tp1.proyecto.academico.entidad.PeriodoEvaluacion;
import com.tp1.proyecto.academico.repositorio.CursoPeriodoAcademicoRepositorio;
import com.tp1.proyecto.academico.repositorio.DocenteCursoSeccionRepositorio;
import com.tp1.proyecto.academico.repositorio.MatriculaRepositorio;
import com.tp1.proyecto.academico.repositorio.PeriodoEvaluacionRepositorio;
import com.tp1.proyecto.academico.repositorio.CursoRepositorio;
import com.tp1.proyecto.academico.repositorio.GradoRepositorio;
import com.tp1.proyecto.academico.repositorio.PeriodoAcademicoRepositorio;
import com.tp1.proyecto.comun.enumeracion.EstadoRegistro;
import com.tp1.proyecto.evaluacion.dto.ConfiguracionEvaluacionCursoDetalleDto;
import com.tp1.proyecto.evaluacion.dto.ConfiguracionEvaluacionCursoGuardarSolicitudDto;
import com.tp1.proyecto.evaluacion.dto.ConfiguracionEvaluacionCursoItemDto;
import com.tp1.proyecto.evaluacion.dto.ConfiguracionEvaluacionCursoResumenDto;
import com.tp1.proyecto.academico.dto.ConfiguracionEvaluacionDefaultSolicitudDto;
import com.tp1.proyecto.evaluacion.dto.ConfiguracionEvaluacionRespuestaDto;
import com.tp1.proyecto.evaluacion.dto.ConfiguracionEvaluacionSolicitudDto;
import com.tp1.proyecto.evaluacion.entidad.ConfiguracionEvaluacion;
import com.tp1.proyecto.evaluacion.entidad.ConfiguracionEvaluacionCurso;
import com.tp1.proyecto.evaluacion.entidad.ConfiguracionEvaluacionPeriodo;
import com.tp1.proyecto.evaluacion.entidad.DetalleNotaEvaluacion;
import com.tp1.proyecto.evaluacion.entidad.Evaluacion;
import com.tp1.proyecto.evaluacion.entidad.NotaCursoPeriodoEvaluacion;
import com.tp1.proyecto.evaluacion.entidad.TipoEvaluacion;
import com.tp1.proyecto.evaluacion.repositorio.ConfiguracionEvaluacionCursoRepositorio;
import com.tp1.proyecto.evaluacion.repositorio.ConfiguracionEvaluacionPeriodoRepositorio;
import com.tp1.proyecto.evaluacion.repositorio.ConfiguracionEvaluacionRepositorio;
import com.tp1.proyecto.evaluacion.repositorio.DetalleNotaEvaluacionRepositorio;
import com.tp1.proyecto.evaluacion.repositorio.EvaluacionRepositorio;
import com.tp1.proyecto.evaluacion.repositorio.NotaCursoPeriodoEvaluacionRepositorio;
import com.tp1.proyecto.evaluacion.repositorio.TipoEvaluacionRepositorio;
import com.tp1.proyecto.evaluacion.servicio.ConfiguracionEvaluacionServicio;
import com.tp1.proyecto.excepcion.RecursoNoEncontradoException;
import com.tp1.proyecto.excepcion.ReglaNegocioException;
import com.tp1.proyecto.prediccion.servicio.PrediccionRiesgoServicio;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ConfiguracionEvaluacionServicioImpl implements ConfiguracionEvaluacionServicio {

    private final ConfiguracionEvaluacionRepositorio configuracionEvaluacionRepositorio;
    private final PeriodoAcademicoRepositorio periodoAcademicoRepositorio;
    private final PeriodoEvaluacionRepositorio periodoEvaluacionRepositorio;
    private final CursoPeriodoAcademicoRepositorio cursoPeriodoAcademicoRepositorio;
    private final CursoRepositorio cursoRepositorio;
    private final DocenteCursoSeccionRepositorio docenteCursoSeccionRepositorio;
    private final GradoRepositorio gradoRepositorio;
    private final TipoEvaluacionRepositorio tipoEvaluacionRepositorio;
    private final ConfiguracionEvaluacionPeriodoRepositorio configuracionEvaluacionPeriodoRepositorio;
    private final ConfiguracionEvaluacionCursoRepositorio configuracionEvaluacionCursoRepositorio;
    private final EvaluacionRepositorio evaluacionRepositorio;
    private final DetalleNotaEvaluacionRepositorio detalleNotaEvaluacionRepositorio;
    private final NotaCursoPeriodoEvaluacionRepositorio notaCursoPeriodoEvaluacionRepositorio;
    private final MatriculaRepositorio matriculaRepositorio;
    private final PrediccionRiesgoServicio prediccionRiesgoServicio;

    public ConfiguracionEvaluacionServicioImpl(
        ConfiguracionEvaluacionRepositorio configuracionEvaluacionRepositorio,
        PeriodoAcademicoRepositorio periodoAcademicoRepositorio,
        PeriodoEvaluacionRepositorio periodoEvaluacionRepositorio,
        CursoPeriodoAcademicoRepositorio cursoPeriodoAcademicoRepositorio,
        CursoRepositorio cursoRepositorio,
        DocenteCursoSeccionRepositorio docenteCursoSeccionRepositorio,
        GradoRepositorio gradoRepositorio,
        TipoEvaluacionRepositorio tipoEvaluacionRepositorio,
        ConfiguracionEvaluacionPeriodoRepositorio configuracionEvaluacionPeriodoRepositorio,
        ConfiguracionEvaluacionCursoRepositorio configuracionEvaluacionCursoRepositorio,
        EvaluacionRepositorio evaluacionRepositorio,
        DetalleNotaEvaluacionRepositorio detalleNotaEvaluacionRepositorio,
        NotaCursoPeriodoEvaluacionRepositorio notaCursoPeriodoEvaluacionRepositorio,
        MatriculaRepositorio matriculaRepositorio,
        PrediccionRiesgoServicio prediccionRiesgoServicio
    ) {
        this.configuracionEvaluacionRepositorio = configuracionEvaluacionRepositorio;
        this.periodoAcademicoRepositorio = periodoAcademicoRepositorio;
        this.periodoEvaluacionRepositorio = periodoEvaluacionRepositorio;
        this.cursoPeriodoAcademicoRepositorio = cursoPeriodoAcademicoRepositorio;
        this.cursoRepositorio = cursoRepositorio;
        this.docenteCursoSeccionRepositorio = docenteCursoSeccionRepositorio;
        this.gradoRepositorio = gradoRepositorio;
        this.tipoEvaluacionRepositorio = tipoEvaluacionRepositorio;
        this.configuracionEvaluacionPeriodoRepositorio = configuracionEvaluacionPeriodoRepositorio;
        this.configuracionEvaluacionCursoRepositorio = configuracionEvaluacionCursoRepositorio;
        this.evaluacionRepositorio = evaluacionRepositorio;
        this.detalleNotaEvaluacionRepositorio = detalleNotaEvaluacionRepositorio;
        this.notaCursoPeriodoEvaluacionRepositorio = notaCursoPeriodoEvaluacionRepositorio;
        this.matriculaRepositorio = matriculaRepositorio;
        this.prediccionRiesgoServicio = prediccionRiesgoServicio;
    }

    @Override
    public ConfiguracionEvaluacionRespuestaDto crear(ConfiguracionEvaluacionSolicitudDto solicitud) {
        PeriodoAcademico periodoAcademico = periodoAcademicoRepositorio.findById(solicitud.getPeriodoAcademicoId())
            .orElseThrow(() -> new RecursoNoEncontradoException("Periodo academico no encontrado con id: " + solicitud.getPeriodoAcademicoId()));

        PeriodoEvaluacion periodoEvaluacion = periodoEvaluacionRepositorio.findById(solicitud.getPeriodoEvaluacionId())
            .orElseThrow(() -> new RecursoNoEncontradoException("PeriodoEvaluacion no encontrado con id: " + solicitud.getPeriodoEvaluacionId()));

        Curso curso = cursoRepositorio.findById(solicitud.getCursoId())
            .orElseThrow(() -> new RecursoNoEncontradoException("Curso no encontrado con id: " + solicitud.getCursoId()));

        TipoEvaluacion tipoEvaluacion = tipoEvaluacionRepositorio.findById(solicitud.getTipoEvaluacionId())
            .orElseThrow(() -> new RecursoNoEncontradoException("Tipo de evaluacion no encontrado con id: " + solicitud.getTipoEvaluacionId()));

        if (!periodoEvaluacion.getPeriodoAcademico().getId().equals(periodoAcademico.getId())) {
            throw new ReglaNegocioException("El periodoEvaluacion no pertenece al periodo academico seleccionado");
        }

        Grado grado = null;
        if (solicitud.getGradoId() != null) {
            grado = gradoRepositorio.findById(solicitud.getGradoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Grado no encontrado con id: " + solicitud.getGradoId()));
        }

        ConfiguracionEvaluacion configuracion = new ConfiguracionEvaluacion();
        configuracion.setPeriodoAcademico(periodoAcademico);
        configuracion.setPeriodoEvaluacion(periodoEvaluacion);
        configuracion.setCurso(curso);
        configuracion.setGrado(grado);
        configuracion.setTipoEvaluacion(tipoEvaluacion);
        configuracion.setCantidadEvaluaciones(solicitud.getCantidadEvaluaciones());
        configuracion.setCalcularEnPromedio(solicitud.getCalcularEnPromedio());

        return mapear(configuracionEvaluacionRepositorio.save(configuracion));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConfiguracionEvaluacionRespuestaDto> listarPorPeriodoEvaluacionYCurso(Long periodoEvaluacionId, Long cursoId) {
        return configuracionEvaluacionRepositorio.findByPeriodoEvaluacionIdAndCursoIdOrderByTipoEvaluacionOrdenAsc(periodoEvaluacionId, cursoId)
            .stream()
            .map(this::mapear)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConfiguracionEvaluacionCursoResumenDto> listarCursosPorPeriodo(Long periodoAcademicoId) {
        obtenerPeriodoAcademico(periodoAcademicoId);
        Map<Long, Boolean> personalizados = new LinkedHashMap<>();
        configuracionEvaluacionCursoRepositorio.findByPeriodoAcademicoId(periodoAcademicoId)
            .stream()
            .filter(configuracion -> configuracion.getEstado() == EstadoRegistro.ACTIVO)
            .forEach(configuracion -> personalizados.put(configuracion.getCurso().getId(), true));

        return cursoPeriodoAcademicoRepositorio.findByPeriodoAcademicoId(periodoAcademicoId)
            .stream()
            .filter(cursoPeriodo -> cursoPeriodo.getEstado() == EstadoRegistro.ACTIVO)
            .sorted(Comparator.comparing(cursoPeriodo -> cursoPeriodo.getCurso().getNombre()))
            .map(cursoPeriodo -> {
                ConfiguracionEvaluacionCursoResumenDto dto = new ConfiguracionEvaluacionCursoResumenDto();
                dto.setCursoId(cursoPeriodo.getCurso().getId());
                dto.setNombreCurso(cursoPeriodo.getCurso().getNombre());
                dto.setDescripcionCurso(cursoPeriodo.getCurso().getDescripcion());
                dto.setNivelNombre(cursoPeriodo.getCurso().getNivel().getNombre());
                dto.setUsaConfiguracionPersonalizada(Boolean.TRUE.equals(personalizados.get(cursoPeriodo.getCurso().getId())));
                dto.setTotalTiposConfigurados(construirConfiguracionesEfectivas(periodoAcademicoId, cursoPeriodo.getCurso().getId()).size());
                return dto;
            })
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ConfiguracionEvaluacionCursoDetalleDto obtenerDetalleCurso(Long periodoAcademicoId, Long cursoId) {
        PeriodoAcademico periodoAcademico = obtenerPeriodoAcademico(periodoAcademicoId);
        Curso curso = obtenerCursoHabilitado(periodoAcademicoId, cursoId);

        ConfiguracionEvaluacionCursoDetalleDto dto = new ConfiguracionEvaluacionCursoDetalleDto();
        dto.setPeriodoAcademicoId(periodoAcademico.getId());
        dto.setCursoId(curso.getId());
        dto.setNombreCurso(curso.getNombre());
        dto.setDescripcionCurso(curso.getDescripcion());
        dto.setNivelNombre(curso.getNivel().getNombre());
        dto.setUsaConfiguracionPersonalizada(
            configuracionEvaluacionCursoRepositorio.findByPeriodoAcademicoIdAndCursoId(periodoAcademicoId, cursoId)
                .stream()
                .anyMatch(configuracion -> configuracion.getEstado() == EstadoRegistro.ACTIVO)
        );
        Map<Long, Integer> cantidadesBase = configuracionEvaluacionPeriodoRepositorio
            .findByPeriodoAcademicoIdOrderByTipoEvaluacionOrdenAsc(periodoAcademicoId)
            .stream()
            .filter(configuracion -> configuracion.getEstado() == EstadoRegistro.ACTIVO)
            .collect(
                java.util.stream.Collectors.toMap(
                    configuracion -> configuracion.getTipoEvaluacion().getId(),
                    configuracion -> configuracion.getCantidadEvaluaciones() != null ? configuracion.getCantidadEvaluaciones() : 0,
                    (a, b) -> a
                )
            );
        dto.setConfiguraciones(
            construirConfiguracionesEfectivas(periodoAcademicoId, cursoId).values()
                .stream()
                .map(item -> mapearItem(item, cantidadesBase.getOrDefault(item.tipoEvaluacion().getId(), 0)))
                .toList()
        );
        return dto;
    }

    @Override
    public ConfiguracionEvaluacionCursoDetalleDto guardarConfiguracionCurso(
        ConfiguracionEvaluacionCursoGuardarSolicitudDto solicitud
    ) {
        PeriodoAcademico periodoAcademico = obtenerPeriodoAcademico(solicitud.getPeriodoAcademicoId());
        Curso curso = obtenerCursoHabilitado(solicitud.getPeriodoAcademicoId(), solicitud.getCursoId());
        Map<Long, ConfiguracionEvaluacionPeriodo> basePeriodo = configuracionEvaluacionPeriodoRepositorio
            .findByPeriodoAcademicoIdOrderByTipoEvaluacionOrdenAsc(periodoAcademico.getId())
            .stream()
            .filter(configuracion -> configuracion.getEstado() == EstadoRegistro.ACTIVO)
            .collect(java.util.stream.Collectors.toMap(config -> config.getTipoEvaluacion().getId(), config -> config));

        Map<Long, ConfiguracionEvaluacionCurso> overridesActuales = configuracionEvaluacionCursoRepositorio
            .findByPeriodoAcademicoIdAndCursoId(periodoAcademico.getId(), curso.getId())
            .stream()
            .collect(java.util.stream.Collectors.toMap(config -> config.getTipoEvaluacion().getId(), config -> config, (a, b) -> a));

        List<ConfiguracionEvaluacionCurso> cambios = new ArrayList<>();

        for (ConfiguracionEvaluacionDefaultSolicitudDto configuracionSolicitud : solicitud.getConfiguraciones()) {
            Long tipoId = configuracionSolicitud.getTipoEvaluacionId();
            int cantidad = configuracionSolicitud.getCantidadEvaluaciones() != null ? configuracionSolicitud.getCantidadEvaluaciones() : 0;
            ConfiguracionEvaluacionPeriodo base = basePeriodo.get(tipoId);
            ConfiguracionEvaluacionCurso existente = overridesActuales.get(tipoId);
            boolean igualABase =
                base != null &&
                base.getCantidadEvaluaciones() != null &&
                base.getCantidadEvaluaciones().intValue() == cantidad;

            if (cantidad <= 0 && base == null) {
                if (existente != null) {
                    existente.setEstado(EstadoRegistro.INACTIVO);
                    cambios.add(existente);
                }
                continue;
            }

            if (igualABase) {
                if (existente != null) {
                    existente.setEstado(EstadoRegistro.INACTIVO);
                    cambios.add(existente);
                }
                continue;
            }

            TipoEvaluacion tipoEvaluacion = tipoEvaluacionRepositorio.findById(tipoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Tipo de evaluacion no encontrado con id: " + tipoId));

            ConfiguracionEvaluacionCurso override = existente != null ? existente : new ConfiguracionEvaluacionCurso();
            override.setPeriodoAcademico(periodoAcademico);
            override.setCurso(curso);
            override.setTipoEvaluacion(tipoEvaluacion);
            override.setCantidadEvaluaciones(cantidad);
            override.setCalcularEnPromedio(true);
            override.setEstado(EstadoRegistro.ACTIVO);
            cambios.add(override);
        }

        if (!cambios.isEmpty()) {
            configuracionEvaluacionCursoRepositorio.saveAll(cambios);
        }

        resincronizarConfiguracionesDerivadas(periodoAcademico, curso);
        return obtenerDetalleCurso(periodoAcademico.getId(), curso.getId());
    }

    private ConfiguracionEvaluacionRespuestaDto mapear(ConfiguracionEvaluacion entidad) {
        ConfiguracionEvaluacionRespuestaDto dto = new ConfiguracionEvaluacionRespuestaDto();
        dto.setId(entidad.getId());
        dto.setPeriodoAcademicoId(entidad.getPeriodoAcademico().getId());
        dto.setPeriodoEvaluacionId(entidad.getPeriodoEvaluacion().getId());
        dto.setNombrePeriodoEvaluacion(entidad.getPeriodoEvaluacion().getNombre());
        dto.setCursoId(entidad.getCurso().getId());
        dto.setNombreCurso(entidad.getCurso().getNombre());
        if (entidad.getGrado() != null) {
            dto.setGradoId(entidad.getGrado().getId());
            dto.setNombreGrado(entidad.getGrado().getNombre());
        }
        dto.setTipoEvaluacionId(entidad.getTipoEvaluacion().getId());
        dto.setNombreTipoEvaluacion(entidad.getTipoEvaluacion().getNombre());
        dto.setCantidadEvaluaciones(entidad.getCantidadEvaluaciones());
        dto.setCalcularEnPromedio(entidad.getCalcularEnPromedio());
        dto.setEstado(entidad.getEstado().name());
        return dto;
    }

    private PeriodoAcademico obtenerPeriodoAcademico(Long periodoAcademicoId) {
        return periodoAcademicoRepositorio.findById(periodoAcademicoId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Periodo academico no encontrado con id: " + periodoAcademicoId));
    }

    private Curso obtenerCursoHabilitado(Long periodoAcademicoId, Long cursoId) {
        Curso curso = cursoRepositorio.findById(cursoId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Curso no encontrado con id: " + cursoId));

        CursoPeriodoAcademico cursoPeriodo = cursoPeriodoAcademicoRepositorio
            .findByPeriodoAcademicoIdAndCursoId(periodoAcademicoId, cursoId)
            .orElseThrow(() -> new ReglaNegocioException("El curso no esta habilitado para este periodo academico."));

        if (cursoPeriodo.getEstado() != EstadoRegistro.ACTIVO) {
            throw new ReglaNegocioException("El curso esta deshabilitado para este periodo academico.");
        }

        return curso;
    }

    private Map<Long, ConfiguracionFuente> construirConfiguracionesEfectivas(Long periodoAcademicoId, Long cursoId) {
        Map<Long, ConfiguracionFuente> configuraciones = new LinkedHashMap<>();

        configuracionEvaluacionPeriodoRepositorio.findByPeriodoAcademicoIdOrderByTipoEvaluacionOrdenAsc(periodoAcademicoId)
            .stream()
            .filter(configuracion -> configuracion.getEstado() == EstadoRegistro.ACTIVO)
            .filter(configuracion -> configuracion.getCantidadEvaluaciones() != null && configuracion.getCantidadEvaluaciones() > 0)
            .forEach(configuracion -> configuraciones.put(
                configuracion.getTipoEvaluacion().getId(),
                new ConfiguracionFuente(
                    configuracion.getTipoEvaluacion(),
                    configuracion.getCantidadEvaluaciones(),
                    Boolean.TRUE.equals(configuracion.getCalcularEnPromedio())
                )
            ));

        configuracionEvaluacionCursoRepositorio.findByPeriodoAcademicoIdAndCursoIdOrderByTipoEvaluacionOrdenAsc(periodoAcademicoId, cursoId)
            .stream()
            .filter(configuracion -> configuracion.getEstado() == EstadoRegistro.ACTIVO)
            .forEach(configuracion -> {
                Long tipoId = configuracion.getTipoEvaluacion().getId();
                if (configuracion.getCantidadEvaluaciones() == null || configuracion.getCantidadEvaluaciones() <= 0) {
                    configuraciones.remove(tipoId);
                } else {
                    configuraciones.put(
                        tipoId,
                        new ConfiguracionFuente(
                            configuracion.getTipoEvaluacion(),
                            configuracion.getCantidadEvaluaciones(),
                            Boolean.TRUE.equals(configuracion.getCalcularEnPromedio())
                        )
                    );
                }
            });

        return configuraciones.entrySet()
            .stream()
            .sorted(Comparator.comparingInt(entry -> entry.getValue().tipoEvaluacion().getOrden()))
            .collect(
                LinkedHashMap::new,
                (map, entry) -> map.put(entry.getKey(), entry.getValue()),
                LinkedHashMap::putAll
            );
    }

    private ConfiguracionEvaluacionCursoItemDto mapearItem(ConfiguracionFuente fuente, Integer cantidadBasePeriodo) {
        ConfiguracionEvaluacionCursoItemDto dto = new ConfiguracionEvaluacionCursoItemDto();
        dto.setTipoEvaluacionId(fuente.tipoEvaluacion().getId());
        dto.setNombreTipoEvaluacion(fuente.tipoEvaluacion().getNombre());
        dto.setDescripcionTipoEvaluacion(fuente.tipoEvaluacion().getDescripcion());
        dto.setCantidadBasePeriodo(cantidadBasePeriodo);
        dto.setCantidadEvaluaciones(fuente.cantidadEvaluaciones());
        dto.setCalcularEnPromedio(fuente.calcularEnPromedio());
        return dto;
    }

    private void resincronizarConfiguracionesDerivadas(PeriodoAcademico periodoAcademico, Curso curso) {
        Map<Long, ConfiguracionFuente> configuracionesEfectivas = construirConfiguracionesEfectivas(periodoAcademico.getId(), curso.getId());
        List<ConfiguracionEvaluacion> existentes = configuracionEvaluacionRepositorio.findByPeriodoAcademicoIdAndCursoId(
            periodoAcademico.getId(),
            curso.getId()
        );
        Map<String, ConfiguracionEvaluacion> existentesPorClave = new LinkedHashMap<>();
        for (ConfiguracionEvaluacion configuracion : existentes) {
            existentesPorClave.put(construirClave(configuracion.getPeriodoEvaluacion().getId(), configuracion.getTipoEvaluacion().getId()), configuracion);
        }

        List<ConfiguracionEvaluacion> cambios = new ArrayList<>();
        List<ConfiguracionEvaluacion> activas = new ArrayList<>();

        periodoEvaluacionRepositorio.findByPeriodoAcademicoId(periodoAcademico.getId())
            .stream()
            .filter(periodo -> periodo.getEstado() == EstadoRegistro.ACTIVO)
            .sorted(Comparator.comparingInt(periodo -> periodo.getNumero() != null ? periodo.getNumero() : 0))
            .forEach(periodoEvaluacion -> {
                for (ConfiguracionFuente fuente : configuracionesEfectivas.values()) {
                    String clave = construirClave(periodoEvaluacion.getId(), fuente.tipoEvaluacion().getId());
                    ConfiguracionEvaluacion configuracion = existentesPorClave.get(clave);
                    if (configuracion == null) {
                        configuracion = new ConfiguracionEvaluacion();
                        configuracion.setPeriodoAcademico(periodoAcademico);
                        configuracion.setPeriodoEvaluacion(periodoEvaluacion);
                        configuracion.setCurso(curso);
                    }
                    configuracion.setTipoEvaluacion(fuente.tipoEvaluacion());
                    configuracion.setCantidadEvaluaciones(fuente.cantidadEvaluaciones());
                    configuracion.setCalcularEnPromedio(fuente.calcularEnPromedio());
                    configuracion.setEstado(EstadoRegistro.ACTIVO);
                    cambios.add(configuracion);
                    activas.add(configuracion);
                }
            });

        for (ConfiguracionEvaluacion configuracion : existentes) {
            String clave = construirClave(configuracion.getPeriodoEvaluacion().getId(), configuracion.getTipoEvaluacion().getId());
            if (activas.stream().noneMatch(item ->
                construirClave(item.getPeriodoEvaluacion().getId(), item.getTipoEvaluacion().getId()).equals(clave)
            )) {
                configuracion.setEstado(EstadoRegistro.INACTIVO);
                cambios.add(configuracion);
            }
        }

        if (!cambios.isEmpty()) {
            configuracionEvaluacionRepositorio.saveAll(cambios);
        }

        List<DocenteCursoSeccion> asignaciones = docenteCursoSeccionRepositorio.findByPeriodoAcademicoId(periodoAcademico.getId())
            .stream()
            .filter(asignacion -> asignacion.getCurso().getId().equals(curso.getId()))
            .filter(asignacion -> asignacion.getEstado() == EstadoRegistro.ACTIVO)
            .toList();

        for (DocenteCursoSeccion asignacion : asignaciones) {
            resincronizarEvaluacionesAsignacion(asignacion, activas);
            recalcularConsolidadosYPredicciones(asignacion, activas);
        }
    }

    private void resincronizarEvaluacionesAsignacion(
        DocenteCursoSeccion asignacion,
        List<ConfiguracionEvaluacion> configuraciones
    ) {
        List<Evaluacion> cambios = new ArrayList<>();
        List<String> clavesActivas = new ArrayList<>();

        for (ConfiguracionEvaluacion configuracion : configuraciones) {
            int cantidad = configuracion.getCantidadEvaluaciones() != null ? configuracion.getCantidadEvaluaciones() : 0;
            List<Evaluacion> existentes = evaluacionRepositorio
                .findByDocenteCursoSeccionIdAndPeriodoEvaluacionIdAndTipoEvaluacionIdOrderByNumeroEvaluacionAsc(
                    asignacion.getId(),
                    configuracion.getPeriodoEvaluacion().getId(),
                    configuracion.getTipoEvaluacion().getId()
                );
            Map<Integer, Evaluacion> porNumero = new LinkedHashMap<>();
            existentes.forEach(evaluacion -> porNumero.put(evaluacion.getNumeroEvaluacion(), evaluacion));

            for (int numero = 1; numero <= cantidad; numero++) {
                Evaluacion evaluacion = porNumero.get(numero);
                if (evaluacion == null) {
                    evaluacion = new Evaluacion();
                    evaluacion.setConfiguracionEvaluacion(configuracion);
                    evaluacion.setDocenteCursoSeccion(asignacion);
                    evaluacion.setPeriodoEvaluacion(configuracion.getPeriodoEvaluacion());
                    evaluacion.setTipoEvaluacion(configuracion.getTipoEvaluacion());
                    evaluacion.setNumeroEvaluacion(numero);
                    evaluacion.setNombre(configuracion.getTipoEvaluacion().getNombre() + " " + numero);
                }
                evaluacion.setConfiguracionEvaluacion(configuracion);
                evaluacion.setEstado(EstadoRegistro.ACTIVO);
                cambios.add(evaluacion);
                clavesActivas.add(
                    construirClaveEvaluacion(
                        configuracion.getPeriodoEvaluacion().getId(),
                        configuracion.getTipoEvaluacion().getId(),
                        numero
                    )
                );
            }

            existentes.stream()
                .filter(evaluacion -> evaluacion.getNumeroEvaluacion() > cantidad)
                .forEach(evaluacion -> {
                    evaluacion.setEstado(EstadoRegistro.INACTIVO);
                    cambios.add(evaluacion);
                });
        }

        evaluacionRepositorio.findByDocenteCursoSeccionId(asignacion.getId())
            .stream()
            .filter(evaluacion ->
                !clavesActivas.contains(
                    construirClaveEvaluacion(
                        evaluacion.getPeriodoEvaluacion().getId(),
                        evaluacion.getTipoEvaluacion().getId(),
                        evaluacion.getNumeroEvaluacion()
                    )
                )
            )
            .forEach(evaluacion -> {
                evaluacion.setEstado(EstadoRegistro.INACTIVO);
                cambios.add(evaluacion);
            });

        if (!cambios.isEmpty()) {
            evaluacionRepositorio.saveAll(cambios);
        }
    }

    private void recalcularConsolidadosYPredicciones(
        DocenteCursoSeccion asignacion,
        List<ConfiguracionEvaluacion> configuraciones
    ) {
        List<Matricula> matriculas = matriculaRepositorio.findBySeccionIdAndPeriodoAcademicoId(
            asignacion.getSeccion().getId(),
            asignacion.getPeriodoAcademico().getId()
        );

        List<Long> periodosEvaluacionIds = configuraciones.stream()
            .map(configuracion -> configuracion.getPeriodoEvaluacion().getId())
            .distinct()
            .toList();

        for (Long periodoEvaluacionId : periodosEvaluacionIds) {
            List<Evaluacion> evaluacionesActivas = evaluacionRepositorio
                .findByDocenteCursoSeccionIdAndPeriodoEvaluacionIdAndEstadoOrderByTipoEvaluacionOrdenAscNumeroEvaluacionAsc(
                    asignacion.getId(),
                    periodoEvaluacionId,
                    EstadoRegistro.ACTIVO
                );

            for (Matricula matricula : matriculas) {
                recalcularConsolidadoCurso(asignacion, matricula, periodoEvaluacionId, evaluacionesActivas);
                prediccionRiesgoServicio.generarPrediccionGlobalPorMatricula(matricula.getId(), periodoEvaluacionId);
            }
        }
    }

    private void recalcularConsolidadoCurso(
        DocenteCursoSeccion asignacion,
        Matricula matricula,
        Long periodoEvaluacionId,
        List<Evaluacion> evaluacionesActivas
    ) {
        List<BigDecimal> notasConsideradas = new ArrayList<>();
        for (Evaluacion evaluacion : evaluacionesActivas) {
            if (!Boolean.TRUE.equals(evaluacion.getConfiguracionEvaluacion().getCalcularEnPromedio())) {
                continue;
            }

            detalleNotaEvaluacionRepositorio.findByEvaluacionIdAndMatriculaId(evaluacion.getId(), matricula.getId())
                .map(DetalleNotaEvaluacion::getNota)
                .ifPresent(notasConsideradas::add);
        }

        NotaCursoPeriodoEvaluacion consolidado = notaCursoPeriodoEvaluacionRepositorio
            .findByMatriculaIdAndCursoIdAndPeriodoEvaluacionId(
                matricula.getId(),
                asignacion.getCurso().getId(),
                periodoEvaluacionId
            )
            .orElseGet(NotaCursoPeriodoEvaluacion::new);

        consolidado.setMatricula(matricula);
        consolidado.setCurso(asignacion.getCurso());
        consolidado.setPeriodoEvaluacion(
            periodoEvaluacionRepositorio.findById(periodoEvaluacionId).orElseThrow()
        );

        if (notasConsideradas.isEmpty()) {
            consolidado.setPromedioCurso(BigDecimal.ZERO);
            consolidado.setCantidadEvaluacionesRegistradas(0);
            consolidado.setObservacion("Sin evaluaciones activas registradas para el curso.");
            consolidado.setEstado(EstadoRegistro.INACTIVO);
            notaCursoPeriodoEvaluacionRepositorio.save(consolidado);
            return;
        }

        BigDecimal suma = notasConsideradas.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal promedio = suma.divide(
            BigDecimal.valueOf(notasConsideradas.size()),
            2,
            RoundingMode.HALF_UP
        );

        consolidado.setPromedioCurso(promedio);
        consolidado.setCantidadEvaluacionesRegistradas(notasConsideradas.size());
        consolidado.setObservacion("Promedio recalculado desde evaluaciones activas.");
        consolidado.setEstado(EstadoRegistro.ACTIVO);
        notaCursoPeriodoEvaluacionRepositorio.save(consolidado);
    }

    private String construirClave(Long periodoEvaluacionId, Long tipoEvaluacionId) {
        return periodoEvaluacionId + "-" + tipoEvaluacionId;
    }

    private String construirClaveEvaluacion(Long periodoEvaluacionId, Long tipoEvaluacionId, Integer numeroEvaluacion) {
        return periodoEvaluacionId + "-" + tipoEvaluacionId + "-" + numeroEvaluacion;
    }

    private record ConfiguracionFuente(
        TipoEvaluacion tipoEvaluacion,
        Integer cantidadEvaluaciones,
        Boolean calcularEnPromedio
    ) {}
}
