package com.tp1.proyecto.academico.servicio.impl;

import com.tp1.proyecto.academico.dto.AlumnoSeccionRespuestaDto;
import com.tp1.proyecto.academico.dto.AsignacionDocenteSolicitudDto;
import com.tp1.proyecto.academico.dto.AsignacionDocenteRespuestaDto;
import com.tp1.proyecto.academico.dto.TutoriaRespuestaDto;
import com.tp1.proyecto.academico.dto.TutoriaResumenAcademicoRespuestaDto;
import com.tp1.proyecto.academico.dto.TutoriaSolicitudDto;
import com.tp1.proyecto.academico.entidad.Curso;
import com.tp1.proyecto.academico.entidad.DocenteCursoSeccion;
import com.tp1.proyecto.academico.entidad.Matricula;
import com.tp1.proyecto.academico.entidad.PeriodoAcademico;
import com.tp1.proyecto.academico.entidad.Seccion;
import com.tp1.proyecto.academico.entidad.Tutoria;
import com.tp1.proyecto.academico.repositorio.CursoRepositorio;
import com.tp1.proyecto.academico.repositorio.DocenteCursoSeccionRepositorio;
import com.tp1.proyecto.academico.repositorio.MatriculaRepositorio;
import com.tp1.proyecto.academico.repositorio.PeriodoAcademicoRepositorio;
import com.tp1.proyecto.academico.repositorio.PeriodoEvaluacionRepositorio;
import com.tp1.proyecto.academico.repositorio.SeccionRepositorio;
import com.tp1.proyecto.academico.repositorio.TutoriaRepositorio;
import com.tp1.proyecto.academico.servicio.AsignacionAcademicaServicio;
import com.tp1.proyecto.comun.enumeracion.EstadoRegistro;
import com.tp1.proyecto.docente.entidad.Docente;
import com.tp1.proyecto.docente.repositorio.DocenteRepositorio;
import com.tp1.proyecto.evaluacion.entidad.ConfiguracionEvaluacion;
import com.tp1.proyecto.evaluacion.entidad.ConfiguracionEvaluacionCurso;
import com.tp1.proyecto.evaluacion.entidad.ConfiguracionEvaluacionPeriodo;
import com.tp1.proyecto.evaluacion.entidad.AsistenciaPeriodoEvaluacion;
import com.tp1.proyecto.evaluacion.entidad.AsistenciaSesion;
import com.tp1.proyecto.evaluacion.entidad.DetalleNotaEvaluacion;
import com.tp1.proyecto.evaluacion.entidad.Evaluacion;
import com.tp1.proyecto.evaluacion.entidad.TipoEvaluacion;
import com.tp1.proyecto.evaluacion.enumeracion.EstadoAsistenciaSesion;
import com.tp1.proyecto.evaluacion.repositorio.AsistenciaPeriodoEvaluacionRepositorio;
import com.tp1.proyecto.evaluacion.repositorio.AsistenciaSesionRepositorio;
import com.tp1.proyecto.evaluacion.repositorio.ConfiguracionEvaluacionCursoRepositorio;
import com.tp1.proyecto.evaluacion.repositorio.ConfiguracionEvaluacionRepositorio;
import com.tp1.proyecto.evaluacion.repositorio.ConfiguracionEvaluacionPeriodoRepositorio;
import com.tp1.proyecto.evaluacion.repositorio.DetalleNotaEvaluacionRepositorio;
import com.tp1.proyecto.evaluacion.repositorio.EvaluacionRepositorio;
import com.tp1.proyecto.excepcion.RecursoNoEncontradoException;
import com.tp1.proyecto.excepcion.ReglaNegocioException;
import com.tp1.proyecto.seguridad.servicio.PermisoPeriodoServicio;
import com.tp1.proyecto.usuario.entidad.Rol;
import com.tp1.proyecto.usuario.entidad.Usuario;
import com.tp1.proyecto.usuario.repositorio.RolRepositorio;
import com.tp1.proyecto.usuario.repositorio.UsuarioRepositorio;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Year;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AsignacionAcademicaServicioImpl implements AsignacionAcademicaServicio {

    private final DocenteRepositorio docenteRepositorio;
    private final CursoRepositorio cursoRepositorio;
    private final SeccionRepositorio seccionRepositorio;
    private final PeriodoAcademicoRepositorio periodoAcademicoRepositorio;
    private final PeriodoEvaluacionRepositorio periodoEvaluacionRepositorio;
    private final DocenteCursoSeccionRepositorio docenteCursoSeccionRepositorio;
    private final TutoriaRepositorio tutoriaRepositorio;
    private final MatriculaRepositorio matriculaRepositorio;
    private final ConfiguracionEvaluacionPeriodoRepositorio configuracionEvaluacionPeriodoRepositorio;
    private final ConfiguracionEvaluacionCursoRepositorio configuracionEvaluacionCursoRepositorio;
    private final ConfiguracionEvaluacionRepositorio configuracionEvaluacionRepositorio;
    private final EvaluacionRepositorio evaluacionRepositorio;
    private final DetalleNotaEvaluacionRepositorio detalleNotaEvaluacionRepositorio;
    private final AsistenciaPeriodoEvaluacionRepositorio asistenciaPeriodoEvaluacionRepositorio;
    private final AsistenciaSesionRepositorio asistenciaSesionRepositorio;
    private final RolRepositorio rolRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;
    private final PermisoPeriodoServicio permisoPeriodoServicio;

    public AsignacionAcademicaServicioImpl(
        DocenteRepositorio docenteRepositorio,
        CursoRepositorio cursoRepositorio,
        SeccionRepositorio seccionRepositorio,
        PeriodoAcademicoRepositorio periodoAcademicoRepositorio,
        PeriodoEvaluacionRepositorio periodoEvaluacionRepositorio,
        DocenteCursoSeccionRepositorio docenteCursoSeccionRepositorio,
        TutoriaRepositorio tutoriaRepositorio,
        MatriculaRepositorio matriculaRepositorio,
        ConfiguracionEvaluacionPeriodoRepositorio configuracionEvaluacionPeriodoRepositorio,
        ConfiguracionEvaluacionCursoRepositorio configuracionEvaluacionCursoRepositorio,
        ConfiguracionEvaluacionRepositorio configuracionEvaluacionRepositorio,
        EvaluacionRepositorio evaluacionRepositorio,
        DetalleNotaEvaluacionRepositorio detalleNotaEvaluacionRepositorio,
        AsistenciaPeriodoEvaluacionRepositorio asistenciaPeriodoEvaluacionRepositorio,
        AsistenciaSesionRepositorio asistenciaSesionRepositorio,
        RolRepositorio rolRepositorio,
        UsuarioRepositorio usuarioRepositorio,
        PermisoPeriodoServicio permisoPeriodoServicio
    ) {
        this.docenteRepositorio = docenteRepositorio;
        this.cursoRepositorio = cursoRepositorio;
        this.seccionRepositorio = seccionRepositorio;
        this.periodoAcademicoRepositorio = periodoAcademicoRepositorio;
        this.periodoEvaluacionRepositorio = periodoEvaluacionRepositorio;
        this.docenteCursoSeccionRepositorio = docenteCursoSeccionRepositorio;
        this.tutoriaRepositorio = tutoriaRepositorio;
        this.matriculaRepositorio = matriculaRepositorio;
        this.configuracionEvaluacionPeriodoRepositorio = configuracionEvaluacionPeriodoRepositorio;
        this.configuracionEvaluacionCursoRepositorio = configuracionEvaluacionCursoRepositorio;
        this.configuracionEvaluacionRepositorio = configuracionEvaluacionRepositorio;
        this.evaluacionRepositorio = evaluacionRepositorio;
        this.detalleNotaEvaluacionRepositorio = detalleNotaEvaluacionRepositorio;
        this.asistenciaPeriodoEvaluacionRepositorio = asistenciaPeriodoEvaluacionRepositorio;
        this.asistenciaSesionRepositorio = asistenciaSesionRepositorio;
        this.rolRepositorio = rolRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
        this.permisoPeriodoServicio = permisoPeriodoServicio;
    }

    @Override
    public AsignacionDocenteRespuestaDto crearAsignacionDocente(AsignacionDocenteSolicitudDto solicitud) {
        Docente docente = obtenerDocente(solicitud.getDocenteId());
        Curso curso = cursoRepositorio.findById(solicitud.getCursoId())
            .orElseThrow(() -> new RecursoNoEncontradoException("Curso no encontrado con id: " + solicitud.getCursoId()));
        Seccion seccion = seccionRepositorio.findById(solicitud.getSeccionId())
            .orElseThrow(() -> new RecursoNoEncontradoException("Seccion no encontrada con id: " + solicitud.getSeccionId()));
        PeriodoAcademico periodoAcademico = obtenerPeriodo(solicitud.getPeriodoAcademicoId());
        validarEdicionAsignaciones(periodoAcademico);

        if (
            seccion.getPeriodoAcademico() == null ||
            !seccion.getPeriodoAcademico().getId().equals(periodoAcademico.getId())
        ) {
            throw new ReglaNegocioException("La seccion seleccionada no pertenece al periodo academico indicado.");
        }

        if (curso.getEstado() != EstadoRegistro.ACTIVO) {
            throw new ReglaNegocioException("El curso seleccionado se encuentra deshabilitado.");
        }

        if (!curso.getNivel().getId().equals(seccion.getGrado().getNivel().getId())) {
            throw new ReglaNegocioException("El curso seleccionado no pertenece al nivel de la seccion indicada.");
        }

        if (docenteCursoSeccionRepositorio.existsByCursoIdAndSeccionIdAndPeriodoAcademicoIdAndEstado(
            curso.getId(),
            seccion.getId(),
            periodoAcademico.getId(),
            EstadoRegistro.ACTIVO
        )) {
            throw new ReglaNegocioException(
                "El curso seleccionado ya tiene un docente asignado para esta seccion y periodo academico."
            );
        }

        DocenteCursoSeccion asignacion = new DocenteCursoSeccion();
        asignacion.setDocente(docente);
        asignacion.setCurso(curso);
        asignacion.setSeccion(seccion);
        asignacion.setPeriodoAcademico(periodoAcademico);

        DocenteCursoSeccion asignacionGuardada = docenteCursoSeccionRepositorio.save(asignacion);
        generarEvaluacionesProgramadas(asignacionGuardada);
        return mapearAsignacion(asignacionGuardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AsignacionDocenteRespuestaDto> listarAsignacionesPorPeriodo(Long periodoAcademicoId) {
        obtenerPeriodo(periodoAcademicoId);

        return docenteCursoSeccionRepositorio.findByPeriodoAcademicoId(periodoAcademicoId)
            .stream()
            .map(this::mapearAsignacion)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AsignacionDocenteRespuestaDto> listarAsignacionesDocente(Long docenteId, Long periodoAcademicoId) {
        obtenerDocente(docenteId);

        return docenteCursoSeccionRepositorio.findByDocenteIdAndPeriodoAcademicoId(docenteId, periodoAcademicoId)
            .stream()
            .map(this::mapearAsignacion)
            .toList();
    }

    @Override
    public AsignacionDocenteRespuestaDto actualizarEstadoAsignacion(Long asignacionId, boolean activo) {
        DocenteCursoSeccion asignacion = docenteCursoSeccionRepositorio.findById(asignacionId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Asignacion docente no encontrada con id: " + asignacionId));
        validarEdicionAsignaciones(asignacion.getPeriodoAcademico());

        if (activo && asignacion.getEstado() != EstadoRegistro.ACTIVO &&
            docenteCursoSeccionRepositorio.existsByCursoIdAndSeccionIdAndPeriodoAcademicoIdAndEstado(
            asignacion.getCurso().getId(),
            asignacion.getSeccion().getId(),
            asignacion.getPeriodoAcademico().getId(),
            EstadoRegistro.ACTIVO
        )) {
            throw new ReglaNegocioException(
                "Ya existe un docente activo para este curso, seccion y periodo academico."
            );
        }

        asignacion.setEstado(activo ? EstadoRegistro.ACTIVO : EstadoRegistro.INACTIVO);
        return mapearAsignacion(docenteCursoSeccionRepositorio.save(asignacion));
    }

    @Override
    public AsignacionDocenteRespuestaDto actualizarAsignacionDocente(Long asignacionId, AsignacionDocenteSolicitudDto solicitud) {
        DocenteCursoSeccion asignacion = docenteCursoSeccionRepositorio.findById(asignacionId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Asignacion docente no encontrada con id: " + asignacionId));

        PeriodoAcademico periodoAcademico = asignacion.getPeriodoAcademico();
        validarEdicionAsignaciones(periodoAcademico);

        Docente nuevoDocente = obtenerDocente(solicitud.getDocenteId());
        Curso nuevoCurso = cursoRepositorio.findById(solicitud.getCursoId())
            .orElseThrow(() -> new RecursoNoEncontradoException("Curso no encontrado con id: " + solicitud.getCursoId()));
        Seccion nuevaSeccion = seccionRepositorio.findById(solicitud.getSeccionId())
            .orElseThrow(() -> new RecursoNoEncontradoException("Seccion no encontrada con id: " + solicitud.getSeccionId()));

        if (
            nuevaSeccion.getPeriodoAcademico() == null ||
            !nuevaSeccion.getPeriodoAcademico().getId().equals(periodoAcademico.getId())
        ) {
            throw new ReglaNegocioException("La seccion seleccionada no pertenece al periodo academico de la asignacion.");
        }

        if (nuevoCurso.getEstado() != EstadoRegistro.ACTIVO) {
            throw new ReglaNegocioException("El curso seleccionado se encuentra deshabilitado.");
        }

        if (!nuevoCurso.getNivel().getId().equals(nuevaSeccion.getGrado().getNivel().getId())) {
            throw new ReglaNegocioException("El curso seleccionado no pertenece al nivel de la seccion indicada.");
        }

        if (docenteCursoSeccionRepositorio.existsByCursoIdAndSeccionIdAndPeriodoAcademicoIdAndEstadoAndIdNot(
            nuevoCurso.getId(),
            nuevaSeccion.getId(),
            periodoAcademico.getId(),
            EstadoRegistro.ACTIVO,
            asignacionId
        )) {
            throw new ReglaNegocioException(
                "El curso seleccionado ya tiene un docente asignado para esta seccion y periodo academico."
            );
        }

        boolean cambioCurso = !nuevoCurso.getId().equals(asignacion.getCurso().getId());
        boolean cambioSeccion = !nuevaSeccion.getId().equals(asignacion.getSeccion().getId());

        if (cambioCurso || cambioSeccion) {
            if (detalleNotaEvaluacionRepositorio.existsByEvaluacionDocenteCursoSeccionId(asignacionId)) {
                throw new ReglaNegocioException(
                    "No se puede cambiar el curso o la seccion porque ya existen calificaciones registradas para esta asignacion. Solo es posible reasignar el docente."
                );
            }

            if (cambioCurso) {
                List<Evaluacion> evaluacionesAntiguas = evaluacionRepositorio.findByDocenteCursoSeccionId(asignacionId);
                if (!evaluacionesAntiguas.isEmpty()) {
                    evaluacionRepositorio.deleteAll(evaluacionesAntiguas);
                }
            }
        }

        asignacion.setDocente(nuevoDocente);
        asignacion.setCurso(nuevoCurso);
        asignacion.setSeccion(nuevaSeccion);

        DocenteCursoSeccion guardada = docenteCursoSeccionRepositorio.save(asignacion);
        if (cambioCurso) {
            generarEvaluacionesProgramadas(guardada);
        }

        return mapearAsignacion(guardada);
    }

    @Override
    public TutoriaRespuestaDto crearTutoria(TutoriaSolicitudDto solicitud) {
        Docente docente = obtenerDocente(solicitud.getDocenteId());
        Seccion seccion = seccionRepositorio.findById(solicitud.getSeccionId())
            .orElseThrow(() -> new RecursoNoEncontradoException("Seccion no encontrada con id: " + solicitud.getSeccionId()));
        PeriodoAcademico periodoAcademico = obtenerPeriodo(solicitud.getPeriodoAcademicoId());
        validarEdicionAsignaciones(periodoAcademico);

        if (
            seccion.getPeriodoAcademico() == null ||
            !seccion.getPeriodoAcademico().getId().equals(periodoAcademico.getId())
        ) {
            throw new ReglaNegocioException("La seccion seleccionada no pertenece al periodo academico indicado.");
        }

        if (
            tutoriaRepositorio.existsBySeccionIdAndPeriodoAcademicoIdAndEstado(
                seccion.getId(),
                periodoAcademico.getId(),
                EstadoRegistro.ACTIVO
            )
        ) {
            throw new ReglaNegocioException("La seccion ya tiene una tutoria asignada en este periodo.");
        }

        Tutoria tutoria = new Tutoria();
        tutoria.setDocente(docente);
        tutoria.setSeccion(seccion);
        tutoria.setPeriodoAcademico(periodoAcademico);
        Tutoria guardada = tutoriaRepositorio.save(tutoria);
        sincronizarRolTutor(docente);
        return mapearTutoria(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TutoriaRespuestaDto> listarTutoriasPorPeriodo(Long periodoAcademicoId) {
        obtenerPeriodo(periodoAcademicoId);

        return tutoriaRepositorio.findByPeriodoAcademicoId(periodoAcademicoId)
            .stream()
            .map(this::mapearTutoria)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TutoriaRespuestaDto> listarTutoriasDocente(Long docenteId, Long periodoAcademicoId) {
        obtenerPeriodo(periodoAcademicoId);

        return tutoriaRepositorio.findByDocenteIdAndPeriodoAcademicoId(docenteId, periodoAcademicoId)
            .stream()
            .map(this::mapearTutoria)
            .toList();
    }

    @Override
    public TutoriaRespuestaDto actualizarEstadoTutoria(Long tutoriaId, boolean activo) {
        Tutoria tutoria = tutoriaRepositorio.findById(tutoriaId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Tutoria no encontrada con id: " + tutoriaId));
        validarEdicionAsignaciones(tutoria.getPeriodoAcademico());

        if (
            activo &&
            tutoria.getEstado() != EstadoRegistro.ACTIVO &&
            tutoriaRepositorio.existsBySeccionIdAndPeriodoAcademicoIdAndEstado(
                tutoria.getSeccion().getId(),
                tutoria.getPeriodoAcademico().getId(),
                EstadoRegistro.ACTIVO
            )
        ) {
            throw new ReglaNegocioException(
                "Ya existe un docente tutor activo para esta seccion y periodo academico."
            );
        }

        tutoria.setEstado(activo ? EstadoRegistro.ACTIVO : EstadoRegistro.INACTIVO);
        Tutoria guardada = tutoriaRepositorio.save(tutoria);
        sincronizarRolTutor(guardada.getDocente());
        return mapearTutoria(guardada);
    }

    @Override
    public TutoriaRespuestaDto actualizarTutoria(Long tutoriaId, TutoriaSolicitudDto solicitud) {
        Tutoria tutoria = tutoriaRepositorio.findById(tutoriaId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Tutoria no encontrada con id: " + tutoriaId));

        PeriodoAcademico periodoAcademico = tutoria.getPeriodoAcademico();
        validarEdicionAsignaciones(periodoAcademico);

        Docente nuevoDocente = obtenerDocente(solicitud.getDocenteId());
        Seccion nuevaSeccion = seccionRepositorio.findById(solicitud.getSeccionId())
            .orElseThrow(() -> new RecursoNoEncontradoException("Seccion no encontrada con id: " + solicitud.getSeccionId()));

        if (
            nuevaSeccion.getPeriodoAcademico() == null ||
            !nuevaSeccion.getPeriodoAcademico().getId().equals(periodoAcademico.getId())
        ) {
            throw new ReglaNegocioException("La seccion seleccionada no pertenece al periodo academico de la tutoria.");
        }

        if (tutoriaRepositorio.existsBySeccionIdAndPeriodoAcademicoIdAndEstadoAndIdNot(
            nuevaSeccion.getId(),
            periodoAcademico.getId(),
            EstadoRegistro.ACTIVO,
            tutoriaId
        )) {
            throw new ReglaNegocioException("La seccion ya tiene una tutoria asignada en este periodo.");
        }

        Docente docenteAnterior = tutoria.getDocente();
        tutoria.setDocente(nuevoDocente);
        tutoria.setSeccion(nuevaSeccion);

        Tutoria guardada = tutoriaRepositorio.save(tutoria);
        sincronizarRolTutor(docenteAnterior);
        sincronizarRolTutor(nuevoDocente);

        return mapearTutoria(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlumnoSeccionRespuestaDto> listarAlumnosPorSeccion(Long seccionId, Long periodoAcademicoId) {
        return matriculaRepositorio.findBySeccionIdAndPeriodoAcademicoId(seccionId, periodoAcademicoId)
            .stream()
            .map(this::mapearAlumno)
            .toList();
    }

    @Override
    @Transactional
    public TutoriaResumenAcademicoRespuestaDto obtenerResumenAcademicoTutoria(
        Long tutoriaId,
        Long periodoEvaluacionId
    ) {
        Tutoria tutoria = tutoriaRepositorio.findById(tutoriaId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Tutoria no encontrada con id: " + tutoriaId));

        var periodoEvaluacion = periodoEvaluacionRepositorio.findById(periodoEvaluacionId)
            .orElseThrow(() ->
                new RecursoNoEncontradoException(
                    "Periodo de evaluacion no encontrado con id: " + periodoEvaluacionId
                )
            );

        if (!periodoEvaluacion.getPeriodoAcademico().getId().equals(tutoria.getPeriodoAcademico().getId())) {
            throw new ReglaNegocioException(
                "El periodo de evaluacion no pertenece al periodo academico de la tutoria seleccionada."
            );
        }

        List<Matricula> matriculas = matriculaRepositorio.findBySeccionIdAndPeriodoAcademicoId(
            tutoria.getSeccion().getId(),
            tutoria.getPeriodoAcademico().getId()
        );
        List<DocenteCursoSeccion> asignaciones = docenteCursoSeccionRepositorio.findBySeccionIdAndPeriodoAcademicoId(
            tutoria.getSeccion().getId(),
            tutoria.getPeriodoAcademico().getId()
        ).stream()
            .filter(asignacion -> asignacion.getEstado() == EstadoRegistro.ACTIVO)
            .sorted(Comparator.comparing(asignacion -> asignacion.getCurso().getNombre()))
            .toList();

        List<Evaluacion> evaluaciones = new ArrayList<>();
        for (DocenteCursoSeccion asignacion : asignaciones) {
            evaluaciones.addAll(
                evaluacionRepositorio.findByDocenteCursoSeccionIdAndPeriodoEvaluacionIdOrderByTipoEvaluacionOrdenAscNumeroEvaluacionAsc(
                    asignacion.getId(),
                    periodoEvaluacionId
                )
            );
        }

        Map<Long, List<DetalleNotaEvaluacion>> detallesPorEvaluacionId = new LinkedHashMap<>();
        if (!evaluaciones.isEmpty()) {
            List<Long> evaluacionIds = evaluaciones.stream().map(Evaluacion::getId).toList();
            for (DetalleNotaEvaluacion detalle : detalleNotaEvaluacionRepositorio.findByEvaluacionIdIn(evaluacionIds)) {
                detallesPorEvaluacionId
                    .computeIfAbsent(detalle.getEvaluacion().getId(), key -> new ArrayList<>())
                    .add(detalle);
            }
        }

        Map<String, List<BigDecimal>> notasPorMatriculaCurso = new LinkedHashMap<>();
        Map<String, List<TutoriaResumenAcademicoRespuestaDto.NotaEvaluacionTutoriaDto>> detalleNotasPorMatriculaCurso =
            new LinkedHashMap<>();
        for (Evaluacion evaluacion : evaluaciones) {
            List<DetalleNotaEvaluacion> detalles = detallesPorEvaluacionId.getOrDefault(evaluacion.getId(), List.of());
            Long cursoId = evaluacion.getDocenteCursoSeccion().getCurso().getId();
            String etiquetaEvaluacion = construirEtiquetaEvaluacion(evaluacion);

            for (DetalleNotaEvaluacion detalle : detalles) {
                String clave = construirClaveResumen(detalle.getMatricula().getId(), cursoId);
                notasPorMatriculaCurso.computeIfAbsent(clave, key -> new ArrayList<>()).add(detalle.getNota());
                TutoriaResumenAcademicoRespuestaDto.NotaEvaluacionTutoriaDto notaDto =
                    new TutoriaResumenAcademicoRespuestaDto.NotaEvaluacionTutoriaDto();
                notaDto.setEtiqueta(etiquetaEvaluacion);
                notaDto.setNota(detalle.getNota());
                detalleNotasPorMatriculaCurso.computeIfAbsent(clave, key -> new ArrayList<>()).add(notaDto);
            }
        }

        TutoriaResumenAcademicoRespuestaDto respuesta = new TutoriaResumenAcademicoRespuestaDto();
        respuesta.setTutoriaId(tutoria.getId());
        respuesta.setDocenteTutorId(tutoria.getDocente().getId());
        respuesta.setDocenteTutorNombreCompleto(
            nombreHistorico(tutoria.getPeriodoAcademico(), tutoria.getDocenteNombreHistorico(),
                tutoria.getDocente().getNombres() + " " + tutoria.getDocente().getApellidos())
        );
        respuesta.setSeccionId(tutoria.getSeccion().getId());
        respuesta.setSeccion(nombreHistorico(tutoria.getPeriodoAcademico(), tutoria.getSeccionNombreHistorico(), tutoria.getSeccion().getNombre()));
        respuesta.setGrado(nombreHistorico(tutoria.getPeriodoAcademico(), tutoria.getGradoNombreHistorico(), tutoria.getSeccion().getGrado().getNombre()));
        respuesta.setNivel(nombreHistorico(tutoria.getPeriodoAcademico(), tutoria.getNivelNombreHistorico(), tutoria.getSeccion().getGrado().getNivel().getNombre()));
        respuesta.setPeriodoAcademicoId(tutoria.getPeriodoAcademico().getId());
        respuesta.setPeriodoAcademico(nombreHistorico(tutoria.getPeriodoAcademico(), tutoria.getPeriodoNombreHistorico(), tutoria.getPeriodoAcademico().getNombre()));
        respuesta.setAnioAcademico(tutoria.getPeriodoAcademico().getAnio());
        respuesta.setPeriodoEvaluacionId(periodoEvaluacion.getId());
        respuesta.setPeriodoEvaluacion(periodoEvaluacion.getNombre());
        respuesta.setPeriodoEvaluacionFechaInicio(periodoEvaluacion.getFechaInicio());
        respuesta.setPeriodoEvaluacionFechaFin(periodoEvaluacion.getFechaFin());
        respuesta.setCursos(
            asignaciones.stream()
                .map(this::mapearCursoTutoriaResumen)
                .toList()
        );
        List<Long> matriculaIds = matriculas.stream().map(Matricula::getId).toList();
        Map<Long, List<AsistenciaSesion>> sesionesPorMatricula = matriculaIds.isEmpty()
            ? Map.of()
            : asistenciaSesionRepositorio.findByMatriculaIdInAndPeriodoEvaluacionIdAndEstado(
                matriculaIds, periodoEvaluacion.getId(), EstadoRegistro.ACTIVO
            ).stream().collect(Collectors.groupingBy(s -> s.getMatricula().getId()));

        respuesta.setAlumnos(
            matriculas.stream()
                .sorted(
                    Comparator.comparing(
                        matricula -> matricula.getAlumno().getApellidos() + " " + matricula.getAlumno().getNombres()
                    )
                )
                .map(
                    matricula ->
                        mapearAlumnoTutoriaResumen(
                            matricula,
                            asignaciones,
                            notasPorMatriculaCurso,
                            detalleNotasPorMatriculaCurso,
                            periodoEvaluacion.getId(),
                            sesionesPorMatricula
                        )
                )
                .toList()
        );
        return respuesta;
    }

    private AsignacionDocenteRespuestaDto mapearAsignacion(DocenteCursoSeccion asignacion) {
        AsignacionDocenteRespuestaDto dto = new AsignacionDocenteRespuestaDto();
        dto.setId(asignacion.getId());
        dto.setDocenteId(asignacion.getDocente().getId());
        dto.setDocenteNombreCompleto(nombreHistorico(asignacion.getPeriodoAcademico(), asignacion.getDocenteNombreHistorico(),
            asignacion.getDocente().getNombres() + " " + asignacion.getDocente().getApellidos()));
        dto.setCursoId(asignacion.getCurso().getId());
        dto.setCurso(nombreHistorico(asignacion.getPeriodoAcademico(), asignacion.getCursoNombreHistorico(), asignacion.getCurso().getNombre()));
        dto.setSeccionId(asignacion.getSeccion().getId());
        dto.setSeccion(nombreHistorico(asignacion.getPeriodoAcademico(), asignacion.getSeccionNombreHistorico(), asignacion.getSeccion().getNombre()));
        dto.setGrado(nombreHistorico(asignacion.getPeriodoAcademico(), asignacion.getGradoNombreHistorico(), asignacion.getSeccion().getGrado().getNombre()));
        dto.setNivel(nombreHistorico(asignacion.getPeriodoAcademico(), asignacion.getNivelNombreHistorico(), asignacion.getSeccion().getGrado().getNivel().getNombre()));
        dto.setPeriodoAcademicoId(asignacion.getPeriodoAcademico().getId());
        dto.setPeriodoAcademico(nombreHistorico(asignacion.getPeriodoAcademico(), asignacion.getPeriodoNombreHistorico(), asignacion.getPeriodoAcademico().getNombre()));
        dto.setAnioAcademico(asignacion.getPeriodoAcademico().getAnio());
        dto.setEstado(asignacion.getEstado() != null ? asignacion.getEstado().name() : EstadoRegistro.ACTIVO.name());
        dto.setFechaRegistro(asignacion.getFechaRegistro());
        dto.setFechaModificacion(asignacion.getFechaModificacion());
        return dto;
    }

    private TutoriaRespuestaDto mapearTutoria(Tutoria tutoria) {
        TutoriaRespuestaDto dto = new TutoriaRespuestaDto();
        dto.setId(tutoria.getId());
        dto.setDocenteId(tutoria.getDocente().getId());
        dto.setDocenteNombreCompleto(nombreHistorico(tutoria.getPeriodoAcademico(), tutoria.getDocenteNombreHistorico(),
            tutoria.getDocente().getNombres() + " " + tutoria.getDocente().getApellidos()));
        dto.setSeccionId(tutoria.getSeccion().getId());
        dto.setSeccion(nombreHistorico(tutoria.getPeriodoAcademico(), tutoria.getSeccionNombreHistorico(), tutoria.getSeccion().getNombre()));
        dto.setGrado(nombreHistorico(tutoria.getPeriodoAcademico(), tutoria.getGradoNombreHistorico(), tutoria.getSeccion().getGrado().getNombre()));
        dto.setNivel(nombreHistorico(tutoria.getPeriodoAcademico(), tutoria.getNivelNombreHistorico(), tutoria.getSeccion().getGrado().getNivel().getNombre()));
        dto.setPeriodoAcademicoId(tutoria.getPeriodoAcademico().getId());
        dto.setPeriodoAcademico(nombreHistorico(tutoria.getPeriodoAcademico(), tutoria.getPeriodoNombreHistorico(), tutoria.getPeriodoAcademico().getNombre()));
        dto.setAnioAcademico(tutoria.getPeriodoAcademico().getAnio());
        dto.setEstado(tutoria.getEstado() != null ? tutoria.getEstado().name() : EstadoRegistro.ACTIVO.name());
        dto.setFechaRegistro(tutoria.getFechaRegistro());
        dto.setFechaModificacion(tutoria.getFechaModificacion());
        return dto;
    }

    private String nombreHistorico(PeriodoAcademico periodo, String historico, String actual) {
        return periodo.getAnio() < Year.now().getValue() && historico != null && !historico.isBlank()
            ? historico : actual;
    }

    private void validarEdicionAsignaciones(PeriodoAcademico periodoAcademico) {
        permisoPeriodoServicio.validarEdicion(periodoAcademico);
        if (periodoAcademico.getAnio() < Year.now().getValue()) {
            throw new ReglaNegocioException("Las asignaciones y tutorias de periodos anteriores son de solo lectura.");
        }
    }

    private AlumnoSeccionRespuestaDto mapearAlumno(Matricula matricula) {
        AlumnoSeccionRespuestaDto dto = new AlumnoSeccionRespuestaDto();
        dto.setMatriculaId(matricula.getId());
        dto.setAlumnoId(matricula.getAlumno().getId());
        dto.setCodigoAlumno(matricula.getAlumno().getCodigo());
        dto.setAlumnoNombreCompleto(matricula.getAlumno().getNombres() + " " + matricula.getAlumno().getApellidos());
        dto.setNivel(matricula.getGrado().getNivel().getNombre());
        dto.setGrado(matricula.getGrado().getNombre());
        dto.setSeccion(matricula.getSeccion().getNombre());
        dto.setPeriodoAcademicoId(matricula.getPeriodoAcademico().getId());
        dto.setAnioAcademico(matricula.getPeriodoAcademico().getAnio());
        return dto;
    }

    private TutoriaResumenAcademicoRespuestaDto.CursoTutoriaResumenDto mapearCursoTutoriaResumen(
        DocenteCursoSeccion asignacion
    ) {
        TutoriaResumenAcademicoRespuestaDto.CursoTutoriaResumenDto dto =
            new TutoriaResumenAcademicoRespuestaDto.CursoTutoriaResumenDto();
        dto.setAsignacionId(asignacion.getId());
        dto.setCursoId(asignacion.getCurso().getId());
        dto.setCurso(nombreHistorico(asignacion.getPeriodoAcademico(), asignacion.getCursoNombreHistorico(), asignacion.getCurso().getNombre()));
        dto.setDocenteId(asignacion.getDocente().getId());
        dto.setDocenteNombreCompleto(nombreHistorico(asignacion.getPeriodoAcademico(), asignacion.getDocenteNombreHistorico(),
            asignacion.getDocente().getNombres() + " " + asignacion.getDocente().getApellidos()));
        return dto;
    }

    private TutoriaResumenAcademicoRespuestaDto.AlumnoTutoriaResumenDto mapearAlumnoTutoriaResumen(
        Matricula matricula,
        List<DocenteCursoSeccion> asignaciones,
        Map<String, List<BigDecimal>> notasPorMatriculaCurso,
        Map<String, List<TutoriaResumenAcademicoRespuestaDto.NotaEvaluacionTutoriaDto>> detalleNotasPorMatriculaCurso,
        Long periodoEvaluacionId,
        Map<Long, List<AsistenciaSesion>> sesionesPorMatricula
    ) {
        TutoriaResumenAcademicoRespuestaDto.AlumnoTutoriaResumenDto dto =
            new TutoriaResumenAcademicoRespuestaDto.AlumnoTutoriaResumenDto();
        dto.setMatriculaId(matricula.getId());
        dto.setAlumnoId(matricula.getAlumno().getId());
        dto.setCodigoAlumno(matricula.getAlumno().getCodigo());
        dto.setAlumnoNombreCompleto(matricula.getAlumno().getNombres() + " " + matricula.getAlumno().getApellidos());

        List<AsistenciaSesion> sesionesAlumno = sesionesPorMatricula.getOrDefault(matricula.getId(), List.of());
        int clasesProgramadas = 0;
        int clasesAsistidas = 0;
        int inasistenciasTotal = 0;
        int tardanzasTotal = 0;
        int justificacionesTotal = 0;

        if (!sesionesAlumno.isEmpty()) {
            Map<String, EstadoAsistenciaSesion> estadoPorSesion = new HashMap<>();
            for (AsistenciaSesion sesion : sesionesAlumno) {
                String claveProgramacion = sesion.getHorarioSemanal() != null
                    ? "horario:" + sesion.getHorarioSemanal().getId()
                    : "asignacion:" + sesion.getAsignacion().getId();
                estadoPorSesion.put(claveProgramacion + ":" + sesion.getFechaClase(), sesion.getEstadoAsistencia());
            }

            clasesProgramadas = (int) estadoPorSesion.values().stream()
                .filter(estado -> estado != EstadoAsistenciaSesion.JUSTIFICADO)
                .count();
            clasesAsistidas = (int) estadoPorSesion.values().stream()
                .filter(estado -> estado == EstadoAsistenciaSesion.PRESENTE || estado == EstadoAsistenciaSesion.TARDANZA)
                .count();
            inasistenciasTotal = (int) estadoPorSesion.values().stream()
                .filter(estado -> estado == EstadoAsistenciaSesion.AUSENTE)
                .count();
            tardanzasTotal = (int) estadoPorSesion.values().stream()
                .filter(estado -> estado == EstadoAsistenciaSesion.TARDANZA)
                .count();
            justificacionesTotal = (int) estadoPorSesion.values().stream()
                .filter(estado -> estado == EstadoAsistenciaSesion.JUSTIFICADO)
                .count();

            // Sincronizar AsistenciaPeriodoEvaluacion
            AsistenciaPeriodoEvaluacion consolidado = asistenciaPeriodoEvaluacionRepositorio
                .findByMatriculaIdAndPeriodoEvaluacionId(matricula.getId(), periodoEvaluacionId)
                .orElseGet(AsistenciaPeriodoEvaluacion::new);
            if (consolidado.getId() == null
                || !Integer.valueOf(clasesProgramadas).equals(consolidado.getClasesProgramadas())
                || !Integer.valueOf(clasesAsistidas).equals(consolidado.getClasesAsistidas())) {
                if (consolidado.getId() == null) {
                    consolidado.setMatricula(matricula);
                    consolidado.setPeriodoEvaluacion(periodoEvaluacionRepositorio.findById(periodoEvaluacionId).orElse(null));
                }
                consolidado.setClasesProgramadas(clasesProgramadas);
                consolidado.setClasesAsistidas(clasesAsistidas);
                consolidado.setObservacion("Sincronizado automáticamente desde asistencias de sesión");
                consolidado.setEstado(EstadoRegistro.ACTIVO);
                asistenciaPeriodoEvaluacionRepositorio.save(consolidado);
            }
        } else {
            AsistenciaPeriodoEvaluacion asistencia = asistenciaPeriodoEvaluacionRepositorio
                .findByMatriculaIdAndPeriodoEvaluacionId(matricula.getId(), periodoEvaluacionId)
                .orElse(null);
            if (asistencia != null) {
                clasesProgramadas = asistencia.getClasesProgramadas() != null ? asistencia.getClasesProgramadas() : 0;
                clasesAsistidas = asistencia.getClasesAsistidas() != null ? asistencia.getClasesAsistidas() : 0;
                inasistenciasTotal = Math.max(0, clasesProgramadas - clasesAsistidas);
            }
        }

        dto.setClasesProgramadas(clasesProgramadas);
        dto.setClasesAsistidas(clasesAsistidas);
        dto.setInasistencias(inasistenciasTotal);
        dto.setTardanzas(tardanzasTotal);
        dto.setJustificaciones(justificacionesTotal);
        dto.setPorcentajeAsistencia(calcularPorcentajeAsistencia(clasesProgramadas, clasesAsistidas));

        List<TutoriaResumenAcademicoRespuestaDto.CursoAlumnoTutoriaResumenDto> cursos = new ArrayList<>();
        List<BigDecimal> promediosCurso = new ArrayList<>();

        for (DocenteCursoSeccion asignacion : asignaciones) {
            String clave = construirClaveResumen(matricula.getId(), asignacion.getCurso().getId());
            List<BigDecimal> notas = notasPorMatriculaCurso.getOrDefault(clave, List.of());
            List<TutoriaResumenAcademicoRespuestaDto.NotaEvaluacionTutoriaDto> detalleNotas =
                detalleNotasPorMatriculaCurso.getOrDefault(clave, List.of());
            BigDecimal promedio = calcularPromedio(notas);

            TutoriaResumenAcademicoRespuestaDto.CursoAlumnoTutoriaResumenDto cursoDto =
                new TutoriaResumenAcademicoRespuestaDto.CursoAlumnoTutoriaResumenDto();
            cursoDto.setAsignacionId(asignacion.getId());
            cursoDto.setCursoId(asignacion.getCurso().getId());
            cursoDto.setCurso(nombreHistorico(asignacion.getPeriodoAcademico(), asignacion.getCursoNombreHistorico(), asignacion.getCurso().getNombre()));
            cursoDto.setDocenteId(asignacion.getDocente().getId());
            cursoDto.setDocenteNombreCompleto(nombreHistorico(asignacion.getPeriodoAcademico(), asignacion.getDocenteNombreHistorico(),
                asignacion.getDocente().getNombres() + " " + asignacion.getDocente().getApellidos()));
            cursoDto.setEvaluacionesRegistradas(notas.size());
            cursoDto.setPromedio(promedio);
            cursoDto.setNotas(new ArrayList<>(notas));
            cursoDto.setDetalleNotas(new ArrayList<>(detalleNotas));

            List<AsistenciaSesion> sesionesCurso = sesionesAlumno.stream()
                .filter(s -> s.getAsignacion() != null && s.getAsignacion().getId().equals(asignacion.getId()))
                .toList();

            int cursoProgramadas = 0;
            int cursoAsistidas = 0;
            int cursoInasistencias = 0;
            int cursoTardanzas = 0;
            int cursoJustificadas = 0;

            if (!sesionesCurso.isEmpty()) {
                Map<String, EstadoAsistenciaSesion> estadoPorSesionCurso = new HashMap<>();
                for (AsistenciaSesion sesion : sesionesCurso) {
                    String claveProgramacion = sesion.getHorarioSemanal() != null
                        ? "horario:" + sesion.getHorarioSemanal().getId()
                        : "asignacion:" + sesion.getAsignacion().getId();
                    estadoPorSesionCurso.put(claveProgramacion + ":" + sesion.getFechaClase(), sesion.getEstadoAsistencia());
                }

                cursoProgramadas = (int) estadoPorSesionCurso.values().stream()
                    .filter(estado -> estado != EstadoAsistenciaSesion.JUSTIFICADO)
                    .count();
                cursoAsistidas = (int) estadoPorSesionCurso.values().stream()
                    .filter(estado -> estado == EstadoAsistenciaSesion.PRESENTE || estado == EstadoAsistenciaSesion.TARDANZA)
                    .count();
                cursoInasistencias = (int) estadoPorSesionCurso.values().stream()
                    .filter(estado -> estado == EstadoAsistenciaSesion.AUSENTE)
                    .count();
                cursoTardanzas = (int) estadoPorSesionCurso.values().stream()
                    .filter(estado -> estado == EstadoAsistenciaSesion.TARDANZA)
                    .count();
                cursoJustificadas = (int) estadoPorSesionCurso.values().stream()
                    .filter(estado -> estado == EstadoAsistenciaSesion.JUSTIFICADO)
                    .count();
            }

            cursoDto.setClasesProgramadas(cursoProgramadas);
            cursoDto.setClasesAsistidas(cursoAsistidas);
            cursoDto.setInasistencias(cursoInasistencias);
            cursoDto.setTardanzas(cursoTardanzas);
            cursoDto.setJustificaciones(cursoJustificadas);
            cursoDto.setPorcentajeAsistencia(calcularPorcentajeAsistencia(cursoProgramadas, cursoAsistidas));

            cursos.add(cursoDto);

            if (promedio != null) {
                promediosCurso.add(promedio);
            }
        }

        dto.setCursos(cursos);
        dto.setPromedioGeneral(calcularPromedio(promediosCurso));
        return dto;
    }

    private BigDecimal calcularPorcentajeAsistencia(int clasesProgramadas, int clasesAsistidas) {
        if (clasesProgramadas <= 0) {
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf(clasesAsistidas)
            .multiply(BigDecimal.valueOf(100))
            .divide(BigDecimal.valueOf(clasesProgramadas), 2, RoundingMode.HALF_UP);
    }

    private Docente obtenerDocente(Long docenteId) {
        Docente docente = docenteRepositorio.findById(docenteId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Docente no encontrado con id: " + docenteId));
        if (docente.getEstado() != EstadoRegistro.ACTIVO) {
            throw new ReglaNegocioException("El docente seleccionado se encuentra inactivo.");
        }
        return docente;
    }

    private void generarEvaluacionesProgramadas(DocenteCursoSeccion asignacion) {
        List<ConfiguracionEvaluacion> configuraciones = asegurarConfiguracionesDerivadas(asignacion);

        if (configuraciones.isEmpty()) {
            return;
        }

        List<Evaluacion> evaluaciones = new ArrayList<>();
        for (ConfiguracionEvaluacion configuracion : configuraciones) {
            int cantidad = configuracion.getCantidadEvaluaciones() != null ? configuracion.getCantidadEvaluaciones() : 0;

            for (int numero = 1; numero <= cantidad; numero++) {
                if (
                    evaluacionRepositorio.existsByDocenteCursoSeccionIdAndPeriodoEvaluacionIdAndTipoEvaluacionIdAndNumeroEvaluacion(
                        asignacion.getId(),
                        configuracion.getPeriodoEvaluacion().getId(),
                        configuracion.getTipoEvaluacion().getId(),
                        numero
                    )
                ) {
                    continue;
                }

                Evaluacion evaluacion = new Evaluacion();
                evaluacion.setConfiguracionEvaluacion(configuracion);
                evaluacion.setDocenteCursoSeccion(asignacion);
                evaluacion.setPeriodoEvaluacion(configuracion.getPeriodoEvaluacion());
                evaluacion.setTipoEvaluacion(configuracion.getTipoEvaluacion());
                evaluacion.setNumeroEvaluacion(numero);
                evaluacion.setNombre(configuracion.getTipoEvaluacion().getNombre() + " " + numero);
                evaluaciones.add(evaluacion);
            }
        }

        if (!evaluaciones.isEmpty()) {
            evaluacionRepositorio.saveAll(evaluaciones);
        }
    }

    private List<ConfiguracionEvaluacion> asegurarConfiguracionesDerivadas(DocenteCursoSeccion asignacion) {
        Long periodoAcademicoId = asignacion.getPeriodoAcademico().getId();
        Long cursoId = asignacion.getCurso().getId();

        List<ConfiguracionEvaluacionPeriodo> configuracionesPeriodo = configuracionEvaluacionPeriodoRepositorio
            .findByPeriodoAcademicoIdOrderByTipoEvaluacionOrdenAsc(periodoAcademicoId)
            .stream()
            .filter(configuracion -> configuracion.getEstado() == EstadoRegistro.ACTIVO)
            .toList();

        if (configuracionesPeriodo.isEmpty()) {
            return List.of();
        }

        Map<Long, ConfiguracionFuente> configuracionesEfectivas = new LinkedHashMap<>();
        for (ConfiguracionEvaluacionPeriodo configuracion : configuracionesPeriodo) {
            if (configuracion.getCantidadEvaluaciones() == null || configuracion.getCantidadEvaluaciones() <= 0) {
                continue;
            }

            configuracionesEfectivas.put(
                configuracion.getTipoEvaluacion().getId(),
                new ConfiguracionFuente(
                    configuracion.getTipoEvaluacion(),
                    configuracion.getCantidadEvaluaciones(),
                    Boolean.TRUE.equals(configuracion.getCalcularEnPromedio())
                )
            );
        }

        List<ConfiguracionEvaluacionCurso> configuracionesCurso = configuracionEvaluacionCursoRepositorio
            .findByPeriodoAcademicoIdAndCursoIdOrderByTipoEvaluacionOrdenAsc(periodoAcademicoId, cursoId)
            .stream()
            .filter(configuracion -> configuracion.getEstado() == EstadoRegistro.ACTIVO)
            .toList();

        for (ConfiguracionEvaluacionCurso configuracion : configuracionesCurso) {
            Long tipoEvaluacionId = configuracion.getTipoEvaluacion().getId();

            if (configuracion.getCantidadEvaluaciones() == null || configuracion.getCantidadEvaluaciones() <= 0) {
                configuracionesEfectivas.remove(tipoEvaluacionId);
                continue;
            }

            configuracionesEfectivas.put(
                tipoEvaluacionId,
                new ConfiguracionFuente(
                    configuracion.getTipoEvaluacion(),
                    configuracion.getCantidadEvaluaciones(),
                    Boolean.TRUE.equals(configuracion.getCalcularEnPromedio())
                )
            );
        }

        List<ConfiguracionEvaluacion> existentes = configuracionEvaluacionRepositorio.findByPeriodoAcademicoIdAndCursoId(
            periodoAcademicoId,
            cursoId
        );
        Map<String, ConfiguracionEvaluacion> existentesPorClave = new LinkedHashMap<>();
        for (ConfiguracionEvaluacion configuracion : existentes) {
            existentesPorClave.put(
                construirClave(
                    configuracion.getPeriodoEvaluacion().getId(),
                    configuracion.getTipoEvaluacion().getId()
                ),
                configuracion
            );
        }

        List<ConfiguracionEvaluacion> cambios = new ArrayList<>();
        List<ConfiguracionEvaluacion> resultado = new ArrayList<>();

        periodoEvaluacionRepositorio.findByPeriodoAcademicoId(periodoAcademicoId)
            .stream()
            .sorted(Comparator.comparingInt(periodo -> periodo.getNumero() != null ? periodo.getNumero() : 0))
            .forEach(periodoEvaluacion -> {
                for (ConfiguracionFuente configuracionFuente : configuracionesEfectivas.values()) {
                    String clave = construirClave(periodoEvaluacion.getId(), configuracionFuente.tipoEvaluacion().getId());
                    ConfiguracionEvaluacion configuracion = existentesPorClave.get(clave);

                    if (configuracion == null) {
                        configuracion = new ConfiguracionEvaluacion();
                        configuracion.setPeriodoAcademico(asignacion.getPeriodoAcademico());
                        configuracion.setPeriodoEvaluacion(periodoEvaluacion);
                        configuracion.setCurso(asignacion.getCurso());
                    }

                    configuracion.setTipoEvaluacion(configuracionFuente.tipoEvaluacion());
                    configuracion.setCantidadEvaluaciones(configuracionFuente.cantidadEvaluaciones());
                    configuracion.setCalcularEnPromedio(configuracionFuente.calcularEnPromedio());
                    configuracion.setEstado(EstadoRegistro.ACTIVO);
                    cambios.add(configuracion);
                    resultado.add(configuracion);
                }
            });

        for (ConfiguracionEvaluacion configuracion : existentes) {
            String clave = construirClave(
                configuracion.getPeriodoEvaluacion().getId(),
                configuracion.getTipoEvaluacion().getId()
            );

            if (!contieneConfiguracion(resultado, clave)
                && configuracion.getTipoEvaluacion().getDocenteCursoSeccion() == null
                && !evaluacionRepositorio.existsByConfiguracionEvaluacionIdAndEstado(configuracion.getId(), EstadoRegistro.ACTIVO)) {
                configuracion.setEstado(EstadoRegistro.INACTIVO);
                cambios.add(configuracion);
            }
        }

        if (!cambios.isEmpty()) {
            configuracionEvaluacionRepositorio.saveAll(cambios);
        }

        return resultado.stream()
            .sorted(
                Comparator
                    .comparing((ConfiguracionEvaluacion configuracion) -> configuracion.getPeriodoEvaluacion().getNumero())
                    .thenComparing(configuracion -> configuracion.getTipoEvaluacion().getOrden())
            )
            .toList();
    }

    private boolean contieneConfiguracion(List<ConfiguracionEvaluacion> configuraciones, String clave) {
        return configuraciones.stream().anyMatch(configuracion ->
            construirClave(configuracion.getPeriodoEvaluacion().getId(), configuracion.getTipoEvaluacion().getId()).equals(clave)
        );
    }

    private String construirClave(Long periodoEvaluacionId, Long tipoEvaluacionId) {
        return periodoEvaluacionId + "-" + tipoEvaluacionId;
    }

    private String construirClaveResumen(Long matriculaId, Long cursoId) {
        return matriculaId + "-" + cursoId;
    }

    private String construirEtiquetaEvaluacion(Evaluacion evaluacion) {
        String tipo = evaluacion.getTipoEvaluacion() != null ? evaluacion.getTipoEvaluacion().getNombre() : "EV";
        String abreviatura;
        switch (tipo) {
            case "EXAMEN_DIARIO":
                abreviatura = "ED";
                break;
            case "REVISION_CUADERNO":
                abreviatura = "RC";
                break;
            case "REVISION_LIBRO":
                abreviatura = "RL";
                break;
            case "TAREA_TRABAJO":
                abreviatura = "TT";
                break;
            case "EXPOSICION_PARTICIPACION":
                abreviatura = "EP";
                break;
            case "EXAMEN":
                abreviatura = "EX";
                break;
            default:
                abreviatura = "EV";
                break;
        }

        Integer numero = evaluacion.getNumeroEvaluacion() != null ? evaluacion.getNumeroEvaluacion() : 1;
        return abreviatura + numero;
    }

    private BigDecimal calcularPromedio(List<BigDecimal> valores) {
        if (valores.isEmpty()) {
            return null;
        }

        BigDecimal suma = valores.stream()
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return suma.divide(BigDecimal.valueOf(valores.size()), 2, RoundingMode.HALF_UP);
    }

    private record ConfiguracionFuente(
        TipoEvaluacion tipoEvaluacion,
        Integer cantidadEvaluaciones,
        Boolean calcularEnPromedio
    ) {}

    private PeriodoAcademico obtenerPeriodo(Long periodoAcademicoId) {
        return periodoAcademicoRepositorio.findById(periodoAcademicoId)
            .orElseThrow(() ->
                new RecursoNoEncontradoException("Periodo academico no encontrado con id: " + periodoAcademicoId)
            );
    }

    private void sincronizarRolTutor(Docente docente) {
        Usuario usuario = docente.getUsuario();
        if (usuario == null) {
            return;
        }

        Rol rolTutor = rolRepositorio.findByNombre("DOCENTE_TUTOR")
            .orElseThrow(() -> new ReglaNegocioException("No se encontro el rol DOCENTE_TUTOR."));

        boolean tieneTutoriaActiva = tutoriaRepositorio.existsByDocenteIdAndEstado(docente.getId(), EstadoRegistro.ACTIVO);
        boolean yaTieneRolTutor = usuario.getRoles().stream()
            .anyMatch(rol -> "DOCENTE_TUTOR".equals(rol.getNombre()));

        if (tieneTutoriaActiva && !yaTieneRolTutor) {
            usuario.getRoles().add(rolTutor);
            usuarioRepositorio.save(usuario);
            return;
        }

        if (!tieneTutoriaActiva && yaTieneRolTutor) {
            usuario.getRoles().removeIf(rol -> "DOCENTE_TUTOR".equals(rol.getNombre()));
            usuarioRepositorio.save(usuario);
        }
    }
}
