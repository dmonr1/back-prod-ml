package com.tp1.proyecto.academico.servicio.impl;

import com.tp1.proyecto.academico.dto.AlumnoSeccionRespuestaDto;
import com.tp1.proyecto.academico.dto.AsignacionDocenteSolicitudDto;
import com.tp1.proyecto.academico.dto.AsignacionDocenteRespuestaDto;
import com.tp1.proyecto.academico.dto.TutoriaRespuestaDto;
import com.tp1.proyecto.academico.dto.TutoriaSolicitudDto;
import com.tp1.proyecto.academico.entidad.Curso;
import com.tp1.proyecto.academico.entidad.CursoPeriodoAcademico;
import com.tp1.proyecto.academico.entidad.DocenteCursoSeccion;
import com.tp1.proyecto.academico.entidad.Matricula;
import com.tp1.proyecto.academico.entidad.PeriodoAcademico;
import com.tp1.proyecto.academico.entidad.Seccion;
import com.tp1.proyecto.academico.entidad.Tutoria;
import com.tp1.proyecto.academico.repositorio.CursoPeriodoAcademicoRepositorio;
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
import com.tp1.proyecto.evaluacion.entidad.Evaluacion;
import com.tp1.proyecto.evaluacion.entidad.TipoEvaluacion;
import com.tp1.proyecto.evaluacion.repositorio.ConfiguracionEvaluacionCursoRepositorio;
import com.tp1.proyecto.evaluacion.repositorio.ConfiguracionEvaluacionRepositorio;
import com.tp1.proyecto.evaluacion.repositorio.ConfiguracionEvaluacionPeriodoRepositorio;
import com.tp1.proyecto.evaluacion.repositorio.EvaluacionRepositorio;
import com.tp1.proyecto.excepcion.RecursoNoEncontradoException;
import com.tp1.proyecto.excepcion.ReglaNegocioException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AsignacionAcademicaServicioImpl implements AsignacionAcademicaServicio {

    private final DocenteRepositorio docenteRepositorio;
    private final CursoPeriodoAcademicoRepositorio cursoPeriodoAcademicoRepositorio;
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

    public AsignacionAcademicaServicioImpl(
        DocenteRepositorio docenteRepositorio,
        CursoPeriodoAcademicoRepositorio cursoPeriodoAcademicoRepositorio,
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
        EvaluacionRepositorio evaluacionRepositorio
    ) {
        this.docenteRepositorio = docenteRepositorio;
        this.cursoPeriodoAcademicoRepositorio = cursoPeriodoAcademicoRepositorio;
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
    }

    @Override
    public AsignacionDocenteRespuestaDto crearAsignacionDocente(AsignacionDocenteSolicitudDto solicitud) {
        Docente docente = obtenerDocente(solicitud.getDocenteId());
        Curso curso = cursoRepositorio.findById(solicitud.getCursoId())
            .orElseThrow(() -> new RecursoNoEncontradoException("Curso no encontrado con id: " + solicitud.getCursoId()));
        Seccion seccion = seccionRepositorio.findById(solicitud.getSeccionId())
            .orElseThrow(() -> new RecursoNoEncontradoException("Seccion no encontrada con id: " + solicitud.getSeccionId()));
        PeriodoAcademico periodoAcademico = obtenerPeriodo(solicitud.getPeriodoAcademicoId());

        if (
            seccion.getPeriodoAcademico() == null ||
            !seccion.getPeriodoAcademico().getId().equals(periodoAcademico.getId())
        ) {
            throw new ReglaNegocioException("La seccion seleccionada no pertenece al periodo academico indicado.");
        }

        CursoPeriodoAcademico cursoPeriodo = cursoPeriodoAcademicoRepositorio
            .findByPeriodoAcademicoIdAndCursoId(periodoAcademico.getId(), curso.getId())
            .orElseThrow(() ->
                new ReglaNegocioException("El curso seleccionado no esta habilitado para el periodo academico indicado.")
            );

        if (cursoPeriodo.getEstado() != EstadoRegistro.ACTIVO) {
            throw new ReglaNegocioException("El curso seleccionado esta deshabilitado para el periodo academico indicado.");
        }

        if (docenteCursoSeccionRepositorio.existsByDocenteIdAndCursoIdAndSeccionIdAndPeriodoAcademicoId(
            docente.getId(),
            curso.getId(),
            seccion.getId(),
            periodoAcademico.getId()
        )) {
            throw new ReglaNegocioException("La asignacion docente ya existe para este curso, seccion y periodo.");
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
    public TutoriaRespuestaDto crearTutoria(TutoriaSolicitudDto solicitud) {
        Docente docente = obtenerDocente(solicitud.getDocenteId());
        Seccion seccion = seccionRepositorio.findById(solicitud.getSeccionId())
            .orElseThrow(() -> new RecursoNoEncontradoException("Seccion no encontrada con id: " + solicitud.getSeccionId()));
        PeriodoAcademico periodoAcademico = obtenerPeriodo(solicitud.getPeriodoAcademicoId());

        if (
            seccion.getPeriodoAcademico() == null ||
            !seccion.getPeriodoAcademico().getId().equals(periodoAcademico.getId())
        ) {
            throw new ReglaNegocioException("La seccion seleccionada no pertenece al periodo academico indicado.");
        }

        if (tutoriaRepositorio.existsByDocenteIdAndPeriodoAcademicoId(docente.getId(), periodoAcademico.getId())) {
            throw new ReglaNegocioException("El docente ya tiene una tutoria registrada en este periodo.");
        }

        if (tutoriaRepositorio.existsBySeccionIdAndPeriodoAcademicoId(seccion.getId(), periodoAcademico.getId())) {
            throw new ReglaNegocioException("La seccion ya tiene una tutoria asignada en este periodo.");
        }

        Tutoria tutoria = new Tutoria();
        tutoria.setDocente(docente);
        tutoria.setSeccion(seccion);
        tutoria.setPeriodoAcademico(periodoAcademico);

        return mapearTutoria(tutoriaRepositorio.save(tutoria));
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
    public List<AlumnoSeccionRespuestaDto> listarAlumnosPorSeccion(Long seccionId, Long periodoAcademicoId) {
        return matriculaRepositorio.findBySeccionIdAndPeriodoAcademicoId(seccionId, periodoAcademicoId)
            .stream()
            .map(this::mapearAlumno)
            .toList();
    }

    private AsignacionDocenteRespuestaDto mapearAsignacion(DocenteCursoSeccion asignacion) {
        AsignacionDocenteRespuestaDto dto = new AsignacionDocenteRespuestaDto();
        dto.setId(asignacion.getId());
        dto.setDocenteId(asignacion.getDocente().getId());
        dto.setDocenteNombreCompleto(
            asignacion.getDocente().getNombres() + " " + asignacion.getDocente().getApellidos()
        );
        dto.setCursoId(asignacion.getCurso().getId());
        dto.setCurso(asignacion.getCurso().getNombre());
        dto.setSeccionId(asignacion.getSeccion().getId());
        dto.setSeccion(asignacion.getSeccion().getNombre());
        dto.setGrado(asignacion.getSeccion().getGrado().getNombre());
        dto.setNivel(asignacion.getSeccion().getGrado().getNivel().getNombre());
        dto.setPeriodoAcademicoId(asignacion.getPeriodoAcademico().getId());
        dto.setPeriodoAcademico(asignacion.getPeriodoAcademico().getNombre());
        dto.setAnioAcademico(asignacion.getPeriodoAcademico().getAnio());
        return dto;
    }

    private TutoriaRespuestaDto mapearTutoria(Tutoria tutoria) {
        TutoriaRespuestaDto dto = new TutoriaRespuestaDto();
        dto.setId(tutoria.getId());
        dto.setDocenteId(tutoria.getDocente().getId());
        dto.setDocenteNombreCompleto(tutoria.getDocente().getNombres() + " " + tutoria.getDocente().getApellidos());
        dto.setSeccionId(tutoria.getSeccion().getId());
        dto.setSeccion(tutoria.getSeccion().getNombre());
        dto.setGrado(tutoria.getSeccion().getGrado().getNombre());
        dto.setNivel(tutoria.getSeccion().getGrado().getNivel().getNombre());
        dto.setPeriodoAcademicoId(tutoria.getPeriodoAcademico().getId());
        dto.setPeriodoAcademico(tutoria.getPeriodoAcademico().getNombre());
        dto.setAnioAcademico(tutoria.getPeriodoAcademico().getAnio());
        return dto;
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

    private Docente obtenerDocente(Long docenteId) {
        return docenteRepositorio.findById(docenteId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Docente no encontrado con id: " + docenteId));
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

            if (!contieneConfiguracion(resultado, clave)) {
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
}
