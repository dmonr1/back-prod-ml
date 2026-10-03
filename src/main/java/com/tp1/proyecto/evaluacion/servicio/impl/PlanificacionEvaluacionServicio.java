package com.tp1.proyecto.evaluacion.servicio.impl;

import com.tp1.proyecto.academico.entidad.DocenteCursoSeccion;
import com.tp1.proyecto.academico.entidad.PeriodoEvaluacion;
import com.tp1.proyecto.academico.repositorio.DocenteCursoSeccionRepositorio;
import com.tp1.proyecto.academico.repositorio.PeriodoEvaluacionRepositorio;
import com.tp1.proyecto.comun.enumeracion.EstadoRegistro;
import com.tp1.proyecto.docente.repositorio.DocenteRepositorio;
import com.tp1.proyecto.evaluacion.dto.PlanificacionEvaluacionSolicitudDto;
import com.tp1.proyecto.evaluacion.dto.TipoEvaluacionRespuestaDto;
import com.tp1.proyecto.evaluacion.entidad.ConfiguracionEvaluacion;
import com.tp1.proyecto.evaluacion.entidad.Evaluacion;
import com.tp1.proyecto.evaluacion.entidad.PlanEvaluacionAsignacion;
import com.tp1.proyecto.evaluacion.entidad.TipoEvaluacion;
import com.tp1.proyecto.evaluacion.repositorio.ConfiguracionEvaluacionRepositorio;
import com.tp1.proyecto.evaluacion.repositorio.DetalleNotaEvaluacionRepositorio;
import com.tp1.proyecto.evaluacion.repositorio.EvaluacionRepositorio;
import com.tp1.proyecto.evaluacion.repositorio.PlanEvaluacionAsignacionRepositorio;
import com.tp1.proyecto.evaluacion.repositorio.TipoEvaluacionRepositorio;
import com.tp1.proyecto.excepcion.RecursoNoEncontradoException;
import com.tp1.proyecto.excepcion.ReglaNegocioException;
import com.tp1.proyecto.seguridad.servicio.PermisoPeriodoServicio;
import com.tp1.proyecto.seguridad.servicio.UsuarioAutenticado;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PlanificacionEvaluacionServicio {

    private final DocenteCursoSeccionRepositorio asignaciones;
    private final PeriodoEvaluacionRepositorio periodos;
    private final DocenteRepositorio docentes;
    private final TipoEvaluacionRepositorio tipos;
    private final ConfiguracionEvaluacionRepositorio configuraciones;
    private final EvaluacionRepositorio evaluaciones;
    private final DetalleNotaEvaluacionRepositorio notas;
    private final PlanEvaluacionAsignacionRepositorio planes;
    private final PermisoPeriodoServicio permisoPeriodo;

    public PlanificacionEvaluacionServicio(
        DocenteCursoSeccionRepositorio asignaciones,
        PeriodoEvaluacionRepositorio periodos,
        DocenteRepositorio docentes,
        TipoEvaluacionRepositorio tipos,
        ConfiguracionEvaluacionRepositorio configuraciones,
        EvaluacionRepositorio evaluaciones,
        DetalleNotaEvaluacionRepositorio notas,
        PlanEvaluacionAsignacionRepositorio planes,
        PermisoPeriodoServicio permisoPeriodo
    ) {
        this.asignaciones = asignaciones;
        this.periodos = periodos;
        this.docentes = docentes;
        this.tipos = tipos;
        this.configuraciones = configuraciones;
        this.evaluaciones = evaluaciones;
        this.notas = notas;
        this.planes = planes;
        this.permisoPeriodo = permisoPeriodo;
    }

    @Transactional(readOnly = true)
    public List<TipoEvaluacionRespuestaDto> listarTipos(Long asignacionId, UsuarioAutenticado actor) {
        DocenteCursoSeccion asignacion = obtenerAsignacion(asignacionId, actor);
        return tiposDisponibles(asignacion.getId()).stream()
            .filter(tipo -> tipo.getEstado() == EstadoRegistro.ACTIVO)
            .map(this::mapearTipo)
            .toList();
    }

    public TipoEvaluacionRespuestaDto crearTipo(
        PlanificacionEvaluacionSolicitudDto.NuevoTipo solicitud,
        UsuarioAutenticado actor
    ) {
        DocenteCursoSeccion asignacion = obtenerAsignacion(solicitud.asignacionId(), actor);
        PeriodoEvaluacion periodo = obtenerPeriodo(solicitud.periodoEvaluacionId(), asignacion);
        validarFechas(solicitud.fechas(), periodo);
        String nombre = solicitud.nombre().trim().toUpperCase();
        if (nombre.length() > 100 || tiposDisponibles(asignacion.getId()).stream()
            .anyMatch(tipo -> tipo.getNombre().equalsIgnoreCase(nombre))) {
            throw new ReglaNegocioException("Ya existe un tipo con ese nombre o el nombre supera 100 caracteres.");
        }

        int orden = tiposDisponibles(asignacion.getId()).stream()
            .mapToInt(TipoEvaluacion::getOrden)
            .max().orElse(0);
        TipoEvaluacion tipo = new TipoEvaluacion();
        tipo.setNombre(nombre);
        tipo.setDescripcion(solicitud.descripcion() == null ? null : solicitud.descripcion().trim());
        tipo.setOrden((short) Math.min(orden + 1, 100));
        tipo.setDocenteCursoSeccion(asignacion);
        tipo = tipos.save(tipo);

        ConfiguracionEvaluacion configuracion = obtenerOAsegurarConfiguracion(asignacion, periodo, tipo);
        int numero = 1;
        for (LocalDate fecha : solicitud.fechas()) {
            guardarEvaluacion(asignacion, periodo, tipo, configuracion, numero++, fecha, actor);
        }
        marcarPersonalizado(asignacion, periodo);
        return mapearTipo(tipo);
    }

    public void agregar(PlanificacionEvaluacionSolicitudDto.Agregar solicitud, UsuarioAutenticado actor) {
        DocenteCursoSeccion asignacion = obtenerAsignacion(solicitud.asignacionId(), actor);
        PeriodoEvaluacion periodo = obtenerPeriodo(solicitud.periodoEvaluacionId(), asignacion);
        validarFecha(solicitud.fecha(), periodo);
        TipoEvaluacion tipo = obtenerTipo(solicitud.tipoEvaluacionId(), asignacion);
        List<Evaluacion> existentes = evaluaciones
            .findByDocenteCursoSeccionIdAndPeriodoEvaluacionIdAndTipoEvaluacionIdOrderByNumeroEvaluacionAsc(
                asignacion.getId(), periodo.getId(), tipo.getId()
            );
        if (existentes.stream().filter(item -> item.getEstado() == EstadoRegistro.ACTIVO).count() >= 99) {
            throw new ReglaNegocioException("El máximo es 99 evaluaciones de este tipo en el período.");
        }
        int numero = existentes.stream().mapToInt(Evaluacion::getNumeroEvaluacion).max().orElse(0) + 1;
        ConfiguracionEvaluacion configuracion = obtenerOAsegurarConfiguracion(asignacion, periodo, tipo);
        guardarEvaluacion(asignacion, periodo, tipo, configuracion, numero, solicitud.fecha(), actor);
        marcarPersonalizado(asignacion, periodo);
    }

    public void cambiarCantidad(PlanificacionEvaluacionSolicitudDto.Cantidad solicitud, UsuarioAutenticado actor) {
        DocenteCursoSeccion asignacion = obtenerAsignacion(solicitud.asignacionId(), actor);
        PeriodoEvaluacion periodo = obtenerPeriodo(solicitud.periodoEvaluacionId(), asignacion);
        TipoEvaluacion tipo = obtenerTipo(solicitud.tipoEvaluacionId(), asignacion);
        List<Evaluacion> todas = evaluaciones
            .findByDocenteCursoSeccionIdAndPeriodoEvaluacionIdAndTipoEvaluacionIdOrderByNumeroEvaluacionAsc(
                asignacion.getId(), periodo.getId(), tipo.getId()
            );
        List<Evaluacion> activas = new ArrayList<>(todas.stream()
            .filter(item -> item.getEstado() == EstadoRegistro.ACTIVO).toList());
        int diferencia = solicitud.cantidad() - activas.size();
        if (diferencia == 0) return;

        if (diferencia > 0) {
            ConfiguracionEvaluacion configuracion = obtenerOAsegurarConfiguracion(asignacion, periodo, tipo);
            int numero = todas.stream().mapToInt(Evaluacion::getNumeroEvaluacion).max().orElse(0);
            for (int i = 0; i < diferencia; i++) {
                guardarEvaluacion(asignacion, periodo, tipo, configuracion, ++numero, null, actor);
            }
        } else {
            validarSinNotasEnCursoSeccion(asignacion);
            activas.sort(Comparator
                .comparing((Evaluacion item) -> item.getFechaEvaluacion() != null)
                .thenComparing(Evaluacion::getNumeroEvaluacion, Comparator.reverseOrder()));
            List<Evaluacion> retirar = activas.subList(0, -diferencia);
            for (Evaluacion evaluacion : retirar) {
                evaluacion.setEstado(EstadoRegistro.INACTIVO);
                evaluacion.setModificadoPor(actor.getUsuario());
            }
            evaluaciones.saveAll(retirar);
        }
        marcarPersonalizado(asignacion, periodo);
    }

    public void quitar(Long evaluacionId, UsuarioAutenticado actor) {
        Evaluacion evaluacion = evaluaciones.findById(evaluacionId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Evaluación no encontrada."));
        DocenteCursoSeccion asignacion = obtenerAsignacion(evaluacion.getDocenteCursoSeccion().getId(), actor);
        PeriodoEvaluacion periodo = obtenerPeriodo(evaluacion.getPeriodoEvaluacion().getId(), asignacion);
        if (evaluacion.getEstado() != EstadoRegistro.ACTIVO) return;
        validarSinNotasEnCursoSeccion(asignacion);
        evaluacion.setEstado(EstadoRegistro.INACTIVO);
        evaluacion.setModificadoPor(actor.getUsuario());
        evaluaciones.save(evaluacion);
        marcarPersonalizado(asignacion, periodo);
    }

    private DocenteCursoSeccion obtenerAsignacion(Long id, UsuarioAutenticado actor) {
        DocenteCursoSeccion asignacion = asignaciones.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Asignación no encontrada."));
        if (asignacion.getEstado() != EstadoRegistro.ACTIVO) {
            throw new ReglaNegocioException("La asignación no está activa.");
        }
        boolean gestor = actor.getAuthorities().stream().map(GrantedAuthority::getAuthority)
            .anyMatch(rol -> rol.equals("ROLE_ADMIN") || rol.equals("ROLE_DIRECTOR_ACADEMICO"));
        if (!gestor) {
            var docente = docentes.findByUsuarioId(actor.getUsuario().getId())
                .orElseThrow(() -> new ReglaNegocioException("El usuario no está vinculado a un docente."));
            if (!docente.getId().equals(asignacion.getDocente().getId())) {
                throw new ReglaNegocioException("Solo puedes planificar tus propias asignaciones.");
            }
        }
        return asignacion;
    }

    private PeriodoEvaluacion obtenerPeriodo(Long id, DocenteCursoSeccion asignacion) {
        PeriodoEvaluacion periodo = periodos.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Período evaluativo no encontrado."));
        if (!periodo.getPeriodoAcademico().getId().equals(asignacion.getPeriodoAcademico().getId())
            || periodo.getEstado() != EstadoRegistro.ACTIVO) {
            throw new ReglaNegocioException("El período evaluativo no corresponde a la asignación.");
        }
        permisoPeriodo.validarEdicion(asignacion.getPeriodoAcademico());
        return periodo;
    }

    private TipoEvaluacion obtenerTipo(Long id, DocenteCursoSeccion asignacion) {
        TipoEvaluacion tipo = tipos.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Tipo de evaluación no encontrado."));
        if (tipo.getEstado() != EstadoRegistro.ACTIVO || (tipo.getDocenteCursoSeccion() != null
            && !tipo.getDocenteCursoSeccion().getId().equals(asignacion.getId()))) {
            throw new ReglaNegocioException("El tipo de evaluación no pertenece a esta asignación.");
        }
        return tipo;
    }

    private ConfiguracionEvaluacion obtenerOAsegurarConfiguracion(
        DocenteCursoSeccion asignacion, PeriodoEvaluacion periodo, TipoEvaluacion tipo
    ) {
        ConfiguracionEvaluacion configuracion = configuraciones
            .findByPeriodoEvaluacionIdAndCursoIdOrderByTipoEvaluacionOrdenAsc(periodo.getId(), asignacion.getCurso().getId())
            .stream()
            .filter(item -> item.getTipoEvaluacion().getId().equals(tipo.getId()))
            .filter(item -> item.getGrado() == null || item.getGrado().getId().equals(asignacion.getSeccion().getGrado().getId()))
            .findFirst().orElse(null);
        if (configuracion == null) {
            configuracion = new ConfiguracionEvaluacion();
            configuracion.setPeriodoAcademico(asignacion.getPeriodoAcademico());
            configuracion.setPeriodoEvaluacion(periodo);
            configuracion.setCurso(asignacion.getCurso());
            configuracion.setTipoEvaluacion(tipo);
            configuracion.setCantidadEvaluaciones(1);
            configuracion.setCalcularEnPromedio(true);
        }
        configuracion.setEstado(EstadoRegistro.ACTIVO);
        return configuraciones.save(configuracion);
    }

    private void guardarEvaluacion(
        DocenteCursoSeccion asignacion, PeriodoEvaluacion periodo, TipoEvaluacion tipo,
        ConfiguracionEvaluacion configuracion, int numero, LocalDate fecha, UsuarioAutenticado actor
    ) {
        Evaluacion evaluacion = new Evaluacion();
        evaluacion.setDocenteCursoSeccion(asignacion);
        evaluacion.setPeriodoEvaluacion(periodo);
        evaluacion.setTipoEvaluacion(tipo);
        evaluacion.setConfiguracionEvaluacion(configuracion);
        evaluacion.setNumeroEvaluacion(numero);
        evaluacion.setNombre(tipo.getNombre() + " " + numero);
        evaluacion.setFechaEvaluacion(fecha);
        evaluacion.setCreadoPor(actor.getUsuario());
        evaluaciones.save(evaluacion);
    }

    private void marcarPersonalizado(DocenteCursoSeccion asignacion, PeriodoEvaluacion periodo) {
        if (planes.existsByDocenteCursoSeccionIdAndPeriodoEvaluacionId(asignacion.getId(), periodo.getId())) return;
        PlanEvaluacionAsignacion plan = new PlanEvaluacionAsignacion();
        plan.setDocenteCursoSeccion(asignacion);
        plan.setPeriodoEvaluacion(periodo);
        planes.save(plan);
    }

    private void validarSinNotasEnCursoSeccion(DocenteCursoSeccion asignacion) {
        if (notas.existsNotasPorCursoSeccion(
            asignacion.getCurso().getId(), asignacion.getSeccion().getId(),
            asignacion.getPeriodoAcademico().getId(), EstadoRegistro.ACTIVO
        )) {
            throw new ReglaNegocioException("No se pueden quitar evaluaciones: este curso y sección ya tiene notas registradas.");
        }
    }

    private void validarFechas(List<LocalDate> fechas, PeriodoEvaluacion periodo) {
        if (fechas.isEmpty() || fechas.size() > 99) {
            throw new ReglaNegocioException("Debes indicar entre 1 y 99 fechas.");
        }
        fechas.forEach(fecha -> validarFecha(fecha, periodo));
    }

    private void validarFecha(LocalDate fecha, PeriodoEvaluacion periodo) {
        if (fecha == null || fecha.isBefore(periodo.getFechaInicio()) || fecha.isAfter(periodo.getFechaFin())) {
            throw new ReglaNegocioException("Cada fecha debe estar dentro del período evaluativo seleccionado.");
        }
    }

    private List<TipoEvaluacion> tiposDisponibles(Long asignacionId) {
        List<TipoEvaluacion> disponibles = new ArrayList<>(tipos.findByDocenteCursoSeccionIsNullOrderByOrdenAscNombreAsc());
        disponibles.addAll(tipos.findByDocenteCursoSeccionIdOrderByOrdenAscNombreAsc(asignacionId));
        disponibles.sort(Comparator.comparing(TipoEvaluacion::getOrden).thenComparing(TipoEvaluacion::getNombre));
        return disponibles;
    }

    private TipoEvaluacionRespuestaDto mapearTipo(TipoEvaluacion tipo) {
        TipoEvaluacionRespuestaDto dto = new TipoEvaluacionRespuestaDto();
        dto.setId(tipo.getId());
        dto.setNombre(tipo.getNombre());
        dto.setDescripcion(tipo.getDescripcion());
        dto.setOrden(tipo.getOrden());
        dto.setEstado(tipo.getEstado().name());
        return dto;
    }
}
