package com.tp1.proyecto.academico.servicio.impl;

import com.tp1.proyecto.academico.dto.PeriodoAcademicoConPeriodosRespuestaDto;
import com.tp1.proyecto.academico.dto.PeriodoAcademicoConPeriodosSolicitudDto;
import com.tp1.proyecto.academico.dto.PeriodoAcademicoRespuestaDto;
import com.tp1.proyecto.academico.dto.PeriodoAcademicoSolicitudDto;
import com.tp1.proyecto.academico.dto.PeriodoEvaluacionInicialSolicitudDto;
import com.tp1.proyecto.academico.dto.PeriodoEvaluacionRespuestaDto;
import com.tp1.proyecto.academico.dto.ConfiguracionEvaluacionDefaultSolicitudDto;
import com.tp1.proyecto.academico.dto.CursoPeriodoAcademicoRespuestaDto;
import com.tp1.proyecto.academico.entidad.Curso;
import com.tp1.proyecto.academico.entidad.CursoPeriodoAcademico;
import com.tp1.proyecto.academico.entidad.PeriodoAcademico;
import com.tp1.proyecto.academico.entidad.PeriodoEvaluacion;
import com.tp1.proyecto.academico.repositorio.CursoPeriodoAcademicoRepositorio;
import com.tp1.proyecto.academico.repositorio.CursoRepositorio;
import com.tp1.proyecto.academico.repositorio.PeriodoAcademicoRepositorio;
import com.tp1.proyecto.academico.repositorio.PeriodoEvaluacionRepositorio;
import com.tp1.proyecto.comun.enumeracion.EstadoRegistro;
import com.tp1.proyecto.academico.servicio.PeriodoAcademicoServicio;
import com.tp1.proyecto.evaluacion.entidad.ConfiguracionEvaluacionPeriodo;
import com.tp1.proyecto.evaluacion.entidad.TipoEvaluacion;
import com.tp1.proyecto.evaluacion.repositorio.ConfiguracionEvaluacionPeriodoRepositorio;
import com.tp1.proyecto.evaluacion.repositorio.TipoEvaluacionRepositorio;
import com.tp1.proyecto.excepcion.RecursoNoEncontradoException;
import com.tp1.proyecto.excepcion.ReglaNegocioException;
import java.util.Comparator;
import java.util.List;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PeriodoAcademicoServicioImpl implements PeriodoAcademicoServicio {

    private final PeriodoAcademicoRepositorio periodoAcademicoRepositorio;
    private final PeriodoEvaluacionRepositorio periodoEvaluacionRepositorio;
    private final TipoEvaluacionRepositorio tipoEvaluacionRepositorio;
    private final ConfiguracionEvaluacionPeriodoRepositorio configuracionEvaluacionPeriodoRepositorio;
    private final CursoRepositorio cursoRepositorio;
    private final CursoPeriodoAcademicoRepositorio cursoPeriodoAcademicoRepositorio;

    public PeriodoAcademicoServicioImpl(
        PeriodoAcademicoRepositorio periodoAcademicoRepositorio,
        PeriodoEvaluacionRepositorio periodoEvaluacionRepositorio,
        TipoEvaluacionRepositorio tipoEvaluacionRepositorio,
        ConfiguracionEvaluacionPeriodoRepositorio configuracionEvaluacionPeriodoRepositorio,
        CursoRepositorio cursoRepositorio,
        CursoPeriodoAcademicoRepositorio cursoPeriodoAcademicoRepositorio
    ) {
        this.periodoAcademicoRepositorio = periodoAcademicoRepositorio;
        this.periodoEvaluacionRepositorio = periodoEvaluacionRepositorio;
        this.tipoEvaluacionRepositorio = tipoEvaluacionRepositorio;
        this.configuracionEvaluacionPeriodoRepositorio = configuracionEvaluacionPeriodoRepositorio;
        this.cursoRepositorio = cursoRepositorio;
        this.cursoPeriodoAcademicoRepositorio = cursoPeriodoAcademicoRepositorio;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PeriodoAcademicoRespuestaDto> listar() {
        return periodoAcademicoRepositorio.findAll()
            .stream()
            .sorted(Comparator.comparing(PeriodoAcademico::getAnio, Comparator.nullsLast(Integer::compareTo)).reversed())
            .map(this::mapearRespuesta)
            .toList();
    }

    @Override
    public PeriodoAcademicoRespuestaDto crear(PeriodoAcademicoSolicitudDto solicitud) {
        validarPeriodoAcademico(solicitud, null);

        PeriodoAcademico periodoAcademico = construirPeriodoAcademico(solicitud);

        return mapearRespuesta(periodoAcademicoRepositorio.save(periodoAcademico));
    }

    @Override
    @Transactional(readOnly = true)
    public PeriodoAcademicoConPeriodosRespuestaDto obtenerDetalle(Long periodoAcademicoId) {
        PeriodoAcademico periodoAcademico = obtenerPeriodoAcademico(periodoAcademicoId);

        return construirRespuestaDetalle(periodoAcademico);
    }

    @Override
    public PeriodoAcademicoConPeriodosRespuestaDto crearConPeriodosEvaluacion(PeriodoAcademicoConPeriodosSolicitudDto solicitud) {
        validarPeriodoAcademico(solicitud, null);
        validarPeriodosEvaluacion(solicitud);
        validarConfiguracionesEvaluacionDefault(solicitud);

        PeriodoAcademico periodoAcademico = periodoAcademicoRepositorio.save(construirPeriodoAcademico(solicitud));
        sincronizarPeriodosEvaluacion(periodoAcademico, solicitud);
        sincronizarConfiguracionesEvaluacionPeriodo(periodoAcademico, solicitud.getConfiguracionesEvaluacionDefault());
        sincronizarCursosPeriodoAcademico(periodoAcademico, solicitud);

        return construirRespuestaDetalle(periodoAcademico);
    }

    @Override
    public PeriodoAcademicoConPeriodosRespuestaDto actualizarConPeriodosEvaluacion(
        Long periodoAcademicoId,
        PeriodoAcademicoConPeriodosSolicitudDto solicitud
    ) {
        PeriodoAcademico periodoAcademico = obtenerPeriodoAcademico(periodoAcademicoId);
        validarPeriodoAcademico(solicitud, periodoAcademicoId);
        validarPeriodosEvaluacion(solicitud);
        validarConfiguracionesEvaluacionDefault(solicitud);

        actualizarPeriodoAcademico(periodoAcademico, solicitud);
        periodoAcademicoRepositorio.save(periodoAcademico);

        sincronizarPeriodosEvaluacion(periodoAcademico, solicitud);
        sincronizarConfiguracionesEvaluacionPeriodo(periodoAcademico, solicitud.getConfiguracionesEvaluacionDefault());
        sincronizarCursosPeriodoAcademico(periodoAcademico, solicitud);

        return construirRespuestaDetalle(periodoAcademico);
    }

    private void validarPeriodoAcademico(PeriodoAcademicoSolicitudDto solicitud, Long periodoAcademicoActualId) {
        if (solicitud.getFechaFin().isBefore(solicitud.getFechaInicio())) {
            throw new ReglaNegocioException("La fecha de fin no puede ser menor que la fecha de inicio");
        }

        periodoAcademicoRepositorio.findByAnio(solicitud.getAnio())
            .ifPresent(periodoExistente -> {
                if (periodoAcademicoActualId != null && periodoExistente.getId().equals(periodoAcademicoActualId)) {
                    return;
                }
                throw new ReglaNegocioException("Ya existe un periodo academico para ese anio");
            });
    }

    private PeriodoAcademico construirPeriodoAcademico(PeriodoAcademicoSolicitudDto solicitud) {
        PeriodoAcademico periodoAcademico = new PeriodoAcademico();
        periodoAcademico.setNombre(normalizarTexto(solicitud.getNombre()));
        periodoAcademico.setAnio(solicitud.getAnio());
        periodoAcademico.setFechaInicio(solicitud.getFechaInicio());
        periodoAcademico.setFechaFin(solicitud.getFechaFin());
        periodoAcademico.setTipoPeriodoEvaluacion(solicitud.getTipoPeriodoEvaluacion());
        return periodoAcademico;
    }

    private void actualizarPeriodoAcademico(PeriodoAcademico periodoAcademico, PeriodoAcademicoSolicitudDto solicitud) {
        periodoAcademico.setNombre(normalizarTexto(solicitud.getNombre()));
        periodoAcademico.setAnio(solicitud.getAnio());
        periodoAcademico.setFechaInicio(solicitud.getFechaInicio());
        periodoAcademico.setFechaFin(solicitud.getFechaFin());
        periodoAcademico.setTipoPeriodoEvaluacion(solicitud.getTipoPeriodoEvaluacion());
        periodoAcademico.setEstado(EstadoRegistro.ACTIVO);
    }

    private void validarPeriodosEvaluacion(PeriodoAcademicoConPeriodosSolicitudDto solicitud) {
        List<PeriodoEvaluacionInicialSolicitudDto> periodos = solicitud.getPeriodosEvaluacion();

        for (PeriodoEvaluacionInicialSolicitudDto periodo : periodos) {
            if (periodo.getFechaFin().isBefore(periodo.getFechaInicio())) {
                throw new ReglaNegocioException("La fecha de fin del periodo de evaluacion no puede ser menor que su fecha de inicio");
            }

            if (periodo.getFechaInicio().isBefore(solicitud.getFechaInicio()) || periodo.getFechaFin().isAfter(solicitud.getFechaFin())) {
                throw new ReglaNegocioException("Los periodos de evaluacion deben estar dentro del periodo academico");
            }
        }
    }

    private void validarConfiguracionesEvaluacionDefault(PeriodoAcademicoConPeriodosSolicitudDto solicitud) {
        Set<Long> tiposRegistrados = new HashSet<>();
        boolean existeCantidadValida = false;

        for (ConfiguracionEvaluacionDefaultSolicitudDto configuracion : solicitud.getConfiguracionesEvaluacionDefault()) {
            if (!tiposRegistrados.add(configuracion.getTipoEvaluacionId())) {
                throw new ReglaNegocioException("No puedes repetir el mismo tipo de evaluacion en la plantilla anual.");
            }

            if (configuracion.getCantidadEvaluaciones() != null && configuracion.getCantidadEvaluaciones() > 0) {
                existeCantidadValida = true;
            }
        }

        if (!existeCantidadValida) {
            throw new ReglaNegocioException("Debes configurar al menos un tipo de evaluacion con cantidad mayor a cero.");
        }
    }

    private Set<Long> resolverCursosPeriodoAcademicoIds(
        PeriodoAcademico periodoAcademico,
        PeriodoAcademicoConPeriodosSolicitudDto solicitud
    ) {
        java.util.LinkedHashSet<Long> cursosIds = new java.util.LinkedHashSet<>();

        if (solicitud.getCursosIds() != null) {
            cursosIds.addAll(solicitud.getCursosIds());
        }

        if (Boolean.TRUE.equals(solicitud.getCopiarCursosPeriodoAnterior())) {
            PeriodoAcademico periodoAnterior = periodoAcademicoRepositorio.findFirstByAnioLessThanOrderByAnioDesc(periodoAcademico.getAnio())
                .orElseThrow(() -> new ReglaNegocioException("No existe un periodo academico anterior para copiar cursos."));

            cursoPeriodoAcademicoRepositorio.findByPeriodoAcademicoId(periodoAnterior.getId())
                .stream()
                .filter(cursoPeriodo -> cursoPeriodo.getEstado() != null && cursoPeriodo.getEstado().name().equals("ACTIVO"))
                .map(cursoPeriodo -> cursoPeriodo.getCurso().getId())
                .forEach(cursosIds::add);
        }

        if (cursosIds.isEmpty()) {
            throw new ReglaNegocioException("Debes registrar o copiar al menos un curso para el periodo academico.");
        }

        return cursosIds;
    }

    private List<ConfiguracionEvaluacionPeriodo> construirConfiguracionesEvaluacionPeriodo(
        PeriodoAcademico periodoAcademico,
        List<ConfiguracionEvaluacionDefaultSolicitudDto> configuracionesDefault
    ) {
        return configuracionesDefault.stream()
            .filter(configuracion -> configuracion.getCantidadEvaluaciones() != null && configuracion.getCantidadEvaluaciones() > 0)
            .map(configuracion -> {
                TipoEvaluacion tipoEvaluacion = tipoEvaluacionRepositorio.findById(configuracion.getTipoEvaluacionId())
                    .orElseThrow(() ->
                        new ReglaNegocioException(
                            "Tipo de evaluacion no encontrado con id: " + configuracion.getTipoEvaluacionId()
                        )
                    );

                return construirConfiguracionEvaluacionPeriodo(periodoAcademico, tipoEvaluacion, configuracion);
            })
            .toList();
    }

    private void sincronizarPeriodosEvaluacion(
        PeriodoAcademico periodoAcademico,
        PeriodoAcademicoConPeriodosSolicitudDto solicitud
    ) {
        Map<Short, PeriodoEvaluacion> existentesPorNumero = periodoEvaluacionRepositorio.findByPeriodoAcademicoId(periodoAcademico.getId())
            .stream()
            .collect(java.util.stream.Collectors.toMap(PeriodoEvaluacion::getNumero, periodo -> periodo));

        Set<Short> numerosSolicitados = new HashSet<>();
        List<PeriodoEvaluacion> aGuardar = new java.util.ArrayList<>();

        for (PeriodoEvaluacionInicialSolicitudDto periodoSolicitud : solicitud.getPeriodosEvaluacion()) {
            Short numero = periodoSolicitud.getNumero().shortValue();
            numerosSolicitados.add(numero);

            PeriodoEvaluacion entidad = existentesPorNumero.get(numero);
            if (entidad == null) {
                entidad = construirPeriodoEvaluacion(periodoAcademico, periodoSolicitud);
            } else {
                entidad.setNombre(normalizarTexto(periodoSolicitud.getNombre()));
                entidad.setFechaInicio(periodoSolicitud.getFechaInicio());
                entidad.setFechaFin(periodoSolicitud.getFechaFin());
                entidad.setEstado(EstadoRegistro.ACTIVO);
            }
            aGuardar.add(entidad);
        }

        existentesPorNumero.values().stream()
            .filter(periodo -> !numerosSolicitados.contains(periodo.getNumero()))
            .forEach(periodo -> {
                periodo.setEstado(EstadoRegistro.INACTIVO);
                aGuardar.add(periodo);
            });

        periodoEvaluacionRepositorio.saveAll(aGuardar);
    }

    private void sincronizarConfiguracionesEvaluacionPeriodo(
        PeriodoAcademico periodoAcademico,
        List<ConfiguracionEvaluacionDefaultSolicitudDto> configuracionesDefault
    ) {
        Map<Long, ConfiguracionEvaluacionPeriodo> existentesPorTipo = configuracionEvaluacionPeriodoRepositorio
            .findByPeriodoAcademicoIdOrderByTipoEvaluacionOrdenAsc(periodoAcademico.getId())
            .stream()
            .collect(java.util.stream.Collectors.toMap(config -> config.getTipoEvaluacion().getId(), config -> config));

        Set<Long> tiposActivos = new HashSet<>();
        List<ConfiguracionEvaluacionPeriodo> aGuardar = new java.util.ArrayList<>();

        for (ConfiguracionEvaluacionDefaultSolicitudDto configuracion : configuracionesDefault) {
            if (configuracion.getCantidadEvaluaciones() == null || configuracion.getCantidadEvaluaciones() <= 0) {
                continue;
            }

            tiposActivos.add(configuracion.getTipoEvaluacionId());
            TipoEvaluacion tipoEvaluacion = tipoEvaluacionRepositorio.findById(configuracion.getTipoEvaluacionId())
                .orElseThrow(() ->
                    new ReglaNegocioException(
                        "Tipo de evaluacion no encontrado con id: " + configuracion.getTipoEvaluacionId()
                    )
                );

            ConfiguracionEvaluacionPeriodo entidad = existentesPorTipo.get(configuracion.getTipoEvaluacionId());
            if (entidad == null) {
                entidad = construirConfiguracionEvaluacionPeriodo(periodoAcademico, tipoEvaluacion, configuracion);
            } else {
                entidad.setTipoEvaluacion(tipoEvaluacion);
                entidad.setCantidadEvaluaciones(configuracion.getCantidadEvaluaciones());
                entidad.setCalcularEnPromedio(Boolean.TRUE.equals(configuracion.getCalcularEnPromedio()));
                entidad.setEstado(EstadoRegistro.ACTIVO);
            }
            aGuardar.add(entidad);
        }

        existentesPorTipo.values().stream()
            .filter(configuracion -> !tiposActivos.contains(configuracion.getTipoEvaluacion().getId()))
            .forEach(configuracion -> {
                configuracion.setEstado(EstadoRegistro.INACTIVO);
                aGuardar.add(configuracion);
            });

        configuracionEvaluacionPeriodoRepositorio.saveAll(aGuardar);
    }

    private void sincronizarCursosPeriodoAcademico(
        PeriodoAcademico periodoAcademico,
        PeriodoAcademicoConPeriodosSolicitudDto solicitud
    ) {
        Set<Long> cursosIds = resolverCursosPeriodoAcademicoIds(periodoAcademico, solicitud);
        Map<Long, CursoPeriodoAcademico> existentesPorCurso = cursoPeriodoAcademicoRepositorio.findByPeriodoAcademicoId(periodoAcademico.getId())
            .stream()
            .collect(java.util.stream.Collectors.toMap(cursoPeriodo -> cursoPeriodo.getCurso().getId(), cursoPeriodo -> cursoPeriodo));

        List<CursoPeriodoAcademico> aGuardar = new java.util.ArrayList<>();

        for (Long cursoId : cursosIds) {
            CursoPeriodoAcademico entidad = existentesPorCurso.get(cursoId);
            if (entidad == null) {
                Curso curso = cursoRepositorio.findById(cursoId)
                    .orElseThrow(() -> new ReglaNegocioException("Curso no encontrado con id: " + cursoId));
                entidad = new CursoPeriodoAcademico();
                entidad.setPeriodoAcademico(periodoAcademico);
                entidad.setCurso(curso);
            }
            entidad.setEstado(EstadoRegistro.ACTIVO);
            aGuardar.add(entidad);
        }

        existentesPorCurso.values().stream()
            .filter(cursoPeriodo -> !cursosIds.contains(cursoPeriodo.getCurso().getId()))
            .forEach(cursoPeriodo -> {
                cursoPeriodo.setEstado(EstadoRegistro.INACTIVO);
                aGuardar.add(cursoPeriodo);
            });

        cursoPeriodoAcademicoRepositorio.saveAll(aGuardar);
    }

    private ConfiguracionEvaluacionPeriodo construirConfiguracionEvaluacionPeriodo(
        PeriodoAcademico periodoAcademico,
        TipoEvaluacion tipoEvaluacion,
        ConfiguracionEvaluacionDefaultSolicitudDto configuracion
    ) {
        ConfiguracionEvaluacionPeriodo entidad = new ConfiguracionEvaluacionPeriodo();
        entidad.setPeriodoAcademico(periodoAcademico);
        entidad.setTipoEvaluacion(tipoEvaluacion);
        entidad.setCantidadEvaluaciones(configuracion.getCantidadEvaluaciones());
        entidad.setCalcularEnPromedio(Boolean.TRUE.equals(configuracion.getCalcularEnPromedio()));
        return entidad;
    }

    private PeriodoEvaluacion construirPeriodoEvaluacion(
        PeriodoAcademico periodoAcademico,
        PeriodoEvaluacionInicialSolicitudDto solicitud
    ) {
        PeriodoEvaluacion periodoEvaluacion = new PeriodoEvaluacion();
        periodoEvaluacion.setPeriodoAcademico(periodoAcademico);
        periodoEvaluacion.setNombre(normalizarTexto(solicitud.getNombre()));
        periodoEvaluacion.setNumero(solicitud.getNumero());
        periodoEvaluacion.setFechaInicio(solicitud.getFechaInicio());
        periodoEvaluacion.setFechaFin(solicitud.getFechaFin());
        return periodoEvaluacion;
    }

    private PeriodoAcademico obtenerPeriodoAcademico(Long periodoAcademicoId) {
        return periodoAcademicoRepositorio.findById(periodoAcademicoId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Periodo academico no encontrado con id: " + periodoAcademicoId));
    }

    private PeriodoAcademicoConPeriodosRespuestaDto construirRespuestaDetalle(PeriodoAcademico periodoAcademico) {
        PeriodoAcademicoConPeriodosRespuestaDto respuesta = new PeriodoAcademicoConPeriodosRespuestaDto();
        respuesta.setPeriodoAcademico(mapearRespuesta(periodoAcademico));
        respuesta.setPeriodosEvaluacion(
            periodoEvaluacionRepositorio.findByPeriodoAcademicoId(periodoAcademico.getId())
                .stream()
                .filter(periodo -> periodo.getEstado() == EstadoRegistro.ACTIVO)
                .sorted(Comparator.comparing(PeriodoEvaluacion::getNumero))
                .map(this::mapearPeriodoEvaluacion)
                .toList()
        );
        respuesta.setConfiguracionesEvaluacionDefault(
            configuracionEvaluacionPeriodoRepositorio.findByPeriodoAcademicoIdOrderByTipoEvaluacionOrdenAsc(periodoAcademico.getId())
                .stream()
                .filter(configuracion -> configuracion.getEstado() == EstadoRegistro.ACTIVO)
                .map(this::mapearConfiguracionEvaluacionDefault)
                .toList()
        );
        respuesta.setCursosPeriodoAcademico(
            cursoPeriodoAcademicoRepositorio.findByPeriodoAcademicoId(periodoAcademico.getId())
                .stream()
                .filter(cursoPeriodo -> cursoPeriodo.getEstado() == EstadoRegistro.ACTIVO)
                .map(this::mapearCursoPeriodo)
                .toList()
        );
        return respuesta;
    }

    private PeriodoAcademicoRespuestaDto mapearRespuesta(PeriodoAcademico periodoAcademico) {
        PeriodoAcademicoRespuestaDto dto = new PeriodoAcademicoRespuestaDto();
        dto.setId(periodoAcademico.getId());
        dto.setNombre(periodoAcademico.getNombre());
        dto.setAnio(periodoAcademico.getAnio());
        dto.setFechaInicio(periodoAcademico.getFechaInicio());
        dto.setFechaFin(periodoAcademico.getFechaFin());
        dto.setTipoPeriodoEvaluacion(periodoAcademico.getTipoPeriodoEvaluacion());
        dto.setEstado(periodoAcademico.getEstado() != null ? periodoAcademico.getEstado().name() : null);
        return dto;
    }

    private CursoPeriodoAcademicoRespuestaDto mapearCursoPeriodo(CursoPeriodoAcademico entidad) {
        CursoPeriodoAcademicoRespuestaDto dto = new CursoPeriodoAcademicoRespuestaDto();
        dto.setId(entidad.getId());
        dto.setPeriodoAcademicoId(entidad.getPeriodoAcademico().getId());
        dto.setPeriodoAcademicoNombre(entidad.getPeriodoAcademico().getNombre());
        dto.setAnioAcademico(entidad.getPeriodoAcademico().getAnio());
        dto.setCursoId(entidad.getCurso().getId());
        dto.setCursoNombre(entidad.getCurso().getNombre());
        dto.setCursoDescripcion(entidad.getCurso().getDescripcion());
        dto.setNivelId(entidad.getCurso().getNivel().getId());
        dto.setNivelNombre(entidad.getCurso().getNivel().getNombre());
        dto.setEstado(entidad.getEstado() != null ? entidad.getEstado().name() : null);
        return dto;
    }

    private PeriodoEvaluacionRespuestaDto mapearPeriodoEvaluacion(PeriodoEvaluacion periodoEvaluacion) {
        PeriodoEvaluacionRespuestaDto dto = new PeriodoEvaluacionRespuestaDto();
        dto.setId(periodoEvaluacion.getId());
        dto.setNombre(periodoEvaluacion.getNombre());
        dto.setNumero(periodoEvaluacion.getNumero());
        dto.setFechaInicio(periodoEvaluacion.getFechaInicio());
        dto.setFechaFin(periodoEvaluacion.getFechaFin());
        dto.setEstado(periodoEvaluacion.getEstado() != null ? periodoEvaluacion.getEstado().name() : null);
        dto.setPeriodoAcademicoId(periodoEvaluacion.getPeriodoAcademico().getId());
        dto.setPeriodoAcademicoNombre(periodoEvaluacion.getPeriodoAcademico().getNombre());
        dto.setAnioAcademico(periodoEvaluacion.getPeriodoAcademico().getAnio());
        return dto;
    }

    private ConfiguracionEvaluacionDefaultSolicitudDto mapearConfiguracionEvaluacionDefault(
        ConfiguracionEvaluacionPeriodo configuracion
    ) {
        ConfiguracionEvaluacionDefaultSolicitudDto dto = new ConfiguracionEvaluacionDefaultSolicitudDto();
        dto.setTipoEvaluacionId(configuracion.getTipoEvaluacion().getId());
        dto.setCantidadEvaluaciones(configuracion.getCantidadEvaluaciones());
        dto.setCalcularEnPromedio(Boolean.TRUE.equals(configuracion.getCalcularEnPromedio()));
        return dto;
    }

    private String normalizarTexto(String texto) {
        return texto == null ? null : texto.trim().toUpperCase();
    }
}
