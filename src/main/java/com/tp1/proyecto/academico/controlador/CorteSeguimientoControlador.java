package com.tp1.proyecto.academico.controlador;

import com.tp1.proyecto.academico.dto.CorteSeguimientoRespuestaDto;
import com.tp1.proyecto.academico.dto.PreparacionCorteRespuestaDto;
import com.tp1.proyecto.academico.entidad.CorteSeguimiento;
import com.tp1.proyecto.academico.entidad.Seccion;
import com.tp1.proyecto.academico.repositorio.CorteSeguimientoRepositorio;
import com.tp1.proyecto.academico.repositorio.DocenteCursoSeccionRepositorio;
import com.tp1.proyecto.academico.repositorio.PeriodoEvaluacionRepositorio;
import com.tp1.proyecto.academico.repositorio.SeccionRepositorio;
import com.tp1.proyecto.academico.servicio.PreparacionCorteServicio;
import com.tp1.proyecto.comun.enumeracion.EstadoRegistro;
import com.tp1.proyecto.docente.repositorio.DocenteRepositorio;
import com.tp1.proyecto.evaluacion.dto.EvaluacionPendienteFechaRespuestaDto;
import com.tp1.proyecto.evaluacion.entidad.Evaluacion;
import com.tp1.proyecto.evaluacion.repositorio.EvaluacionRepositorio;
import com.tp1.proyecto.prediccion.servicio.CorteSeguimientoPrediccionServicio;
import com.tp1.proyecto.prediccion.servicio.ProgresoRecalculoPredicciones;
import com.tp1.proyecto.seguridad.servicio.UsuarioAutenticado;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/cortes-seguimiento")
@PreAuthorize("hasAnyRole('ADMIN','DOCENTE','DOCENTE_TUTOR')")
public class CorteSeguimientoControlador {
    private final CorteSeguimientoRepositorio corteRepositorio;
    private final PeriodoEvaluacionRepositorio periodoEvaluacionRepositorio;
    private final EvaluacionRepositorio evaluacionRepositorio;
    private final CorteSeguimientoPrediccionServicio prediccionServicio;
    private final PreparacionCorteServicio preparacionServicio;
    private final SeccionRepositorio seccionRepositorio;
    private final DocenteCursoSeccionRepositorio asignacionRepositorio;
    private final DocenteRepositorio docenteRepositorio;
    private final ProgresoRecalculoPredicciones progresoRecalculo;

    public CorteSeguimientoControlador(
        CorteSeguimientoRepositorio corteRepositorio,
        PeriodoEvaluacionRepositorio periodoEvaluacionRepositorio,
        EvaluacionRepositorio evaluacionRepositorio,
        CorteSeguimientoPrediccionServicio prediccionServicio,
        PreparacionCorteServicio preparacionServicio,
        SeccionRepositorio seccionRepositorio,
        DocenteCursoSeccionRepositorio asignacionRepositorio,
        DocenteRepositorio docenteRepositorio,
        ProgresoRecalculoPredicciones progresoRecalculo
    ) {
        this.corteRepositorio = corteRepositorio;
        this.periodoEvaluacionRepositorio = periodoEvaluacionRepositorio;
        this.evaluacionRepositorio = evaluacionRepositorio;
        this.prediccionServicio = prediccionServicio;
        this.preparacionServicio = preparacionServicio;
        this.seccionRepositorio = seccionRepositorio;
        this.asignacionRepositorio = asignacionRepositorio;
        this.docenteRepositorio = docenteRepositorio;
        this.progresoRecalculo = progresoRecalculo;
    }

    @GetMapping
    public List<CorteSeguimientoRespuestaDto> listar(@RequestParam Long periodoAcademicoId) {
        return corteRepositorio.findByPeriodoAcademicoIdAndEstadoOrderBySemanaAsc(periodoAcademicoId, EstadoRegistro.ACTIVO)
            .stream().map(this::mapear).toList();
    }

    @GetMapping("/{corteId}/evaluaciones-pendientes")
    public List<EvaluacionPendienteFechaRespuestaDto> listarEvaluacionesSinFecha(
        @PathVariable Long corteId,
        @RequestParam Long seccionId
    ) {
        CorteSeguimiento corte = corteRepositorio.findById(corteId)
            .orElseThrow(() -> new IllegalArgumentException("Corte de seguimiento no encontrado."));
        var periodo = periodoEvaluacionRepositorio.findByPeriodoAcademicoId(corte.getPeriodoAcademico().getId()).stream()
            .filter(p -> p.getEstado() == EstadoRegistro.ACTIVO)
            .filter(p -> !corte.getFechaCorte().isBefore(p.getFechaInicio()) && !corte.getFechaCorte().isAfter(p.getFechaFin()))
            .findFirst();
        if (periodo.isEmpty()) return List.of();
        return evaluacionRepositorio
            .findByDocenteCursoSeccionSeccionIdAndDocenteCursoSeccionPeriodoAcademicoIdAndDocenteCursoSeccionEstadoAndPeriodoEvaluacionIdAndFechaEvaluacionIsNullAndEstadoOrderByTipoEvaluacionOrdenAscNumeroEvaluacionAsc(
                seccionId, corte.getPeriodoAcademico().getId(), EstadoRegistro.ACTIVO, periodo.get().getId(), EstadoRegistro.ACTIVO
            ).stream().map(this::mapearPendiente).toList();
    }

    @GetMapping("/{corteId}/preparacion")
    public PreparacionCorteRespuestaDto obtenerPreparacion(@PathVariable Long corteId, @RequestParam Long seccionId) {
        return preparacionServicio.obtener(corteId, seccionId);
    }

    @PostMapping("/{corteId}/recalcular")
    @PreAuthorize("hasAnyRole('ADMIN','DOCENTE','DOCENTE_TUTOR')")
    public java.util.Map<String, Object> recalcular(@PathVariable Long corteId, @RequestParam Long seccionId) {
        int procesadas = prediccionServicio.recalcular(corteId, seccionId);
        return java.util.Map.of("corteSeguimientoId", corteId, "seccionId", seccionId, "matriculasProcesadas", procesadas);
    }

    @PostMapping("/{corteId}/secciones/{seccionId}/recalcular-faltantes")
    @PreAuthorize("hasAnyRole('ADMIN','DIRECTOR_ACADEMICO','DOCENTE','DOCENTE_TUTOR')")
    public Map<String, Object> recalcularFaltantesDeSeccion(
        @PathVariable Long corteId,
        @PathVariable Long seccionId,
        @RequestParam String trabajoId,
        @AuthenticationPrincipal UsuarioAutenticado actor
    ) {
        CorteSeguimiento corte = corteRepositorio.findById(corteId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Corte de seguimiento no encontrado."));
        Seccion seccion = seccionRepositorio.findById(seccionId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sección no encontrada."));
        validarAccesoRecalculo(actor, corte, seccion, seccionId);
        try {
            UUID.fromString(trabajoId);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El identificador del trabajo no es válido.");
        }
        progresoRecalculo.iniciar(trabajoId);
        int procesadas;
        try {
            procesadas = prediccionServicio.recalcularFaltantes(corteId, seccionId, trabajoId);
            progresoRecalculo.finalizar(trabajoId);
        } catch (RuntimeException ex) {
            progresoRecalculo.fallar(trabajoId);
            throw ex;
        }
        return Map.of(
            "corteSeguimientoId", corteId,
            "seccionId", seccionId,
            "matriculasProcesadas", procesadas,
            "erroresPrediccion", progresoRecalculo.obtener(trabajoId).erroresPrediccion()
        );
    }

    @GetMapping("/{corteId}/secciones/{seccionId}/recalcular-faltantes/progreso")
    @PreAuthorize("hasAnyRole('ADMIN','DIRECTOR_ACADEMICO','DOCENTE','DOCENTE_TUTOR')")
    public Map<String, Object> progresoRecalculoDeSeccion(
        @PathVariable Long corteId,
        @PathVariable Long seccionId,
        @RequestParam String trabajoId,
        @AuthenticationPrincipal UsuarioAutenticado actor
    ) {
        CorteSeguimiento corte = corteRepositorio.findById(corteId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Corte de seguimiento no encontrado."));
        Seccion seccion = seccionRepositorio.findById(seccionId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sección no encontrada."));
        validarAccesoRecalculo(actor, corte, seccion, seccionId);
        ProgresoRecalculoPredicciones.Estado estado = progresoRecalculo.obtener(trabajoId);
        if (estado == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró el proceso de recálculo.");
        return Map.of(
            "procesados", estado.procesados(),
            "total", estado.total(),
            "alumnoEnCurso", estado.alumnoEnCurso(),
            "erroresPrediccion", estado.erroresPrediccion(),
            "porcentaje", estado.porcentaje(),
            "finalizado", estado.finalizado(),
            "error", estado.error()
        );
    }

    private void validarAccesoRecalculo(UsuarioAutenticado actor, CorteSeguimiento corte, Seccion seccion, Long seccionId) {
        if (!corte.getPeriodoAcademico().getId().equals(seccion.getPeriodoAcademico().getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El corte y la sección pertenecen a períodos académicos distintos.");
        }
        boolean esGestor = actor.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(rol -> rol.equals("ROLE_ADMIN") || rol.equals("ROLE_DIRECTOR_ACADEMICO"));
        if (esGestor) return;
        var docente = docenteRepositorio.findByUsuarioId(actor.getUsuario().getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "El usuario no está vinculado a un docente."));
        boolean tieneAsignacion = asignacionRepositorio
            .findByDocenteIdAndPeriodoAcademicoId(docente.getId(), corte.getPeriodoAcademico().getId())
            .stream()
            .anyMatch(asignacion -> asignacion.getEstado() == EstadoRegistro.ACTIVO
                && asignacion.getSeccion().getId().equals(seccionId));
        if (!tieneAsignacion) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo puedes generar predicciones de tus secciones asignadas.");
        }
    }

    @PostMapping("/recalcular-faltantes")
    @PreAuthorize("hasAnyRole('ADMIN','DIRECTOR_ACADEMICO')")
    public Map<String, Object> recalcularFaltantes(@RequestParam Long periodoAcademicoId) {
        List<CorteSeguimiento> cortes = corteRepositorio
            .findByPeriodoAcademicoIdAndFechaCorteLessThanEqualAndEstadoOrderByFechaCorteAsc(
                periodoAcademicoId, LocalDate.now(), EstadoRegistro.ACTIVO
            );
        List<Seccion> secciones = seccionRepositorio.findByPeriodoAcademicoId(periodoAcademicoId).stream()
            .filter(seccion -> seccion.getEstado() == EstadoRegistro.ACTIVO)
            .toList();

        int combinacionesEvaluadas = 0;
        int combinacionesConDatos = 0;
        int matriculasProcesadas = 0;
        int errores = 0;
        List<String> detallesError = new ArrayList<>();
        for (CorteSeguimiento corte : cortes) {
            for (Seccion seccion : secciones) {
                combinacionesEvaluadas++;
                try {
                    int procesadas = prediccionServicio.recalcularFaltantes(corte.getId(), seccion.getId());
                    if (procesadas > 0) combinacionesConDatos++;
                    matriculasProcesadas += procesadas;
                } catch (Exception ex) {
                    errores++;
                    if (detallesError.size() < 10) {
                        detallesError.add("Semana " + corte.getSemana() + ", sección " + seccion.getId() + ": " + ex.getMessage());
                    }
                }
            }
        }

        return Map.of(
            "periodoAcademicoId", periodoAcademicoId,
            "cortesEvaluados", combinacionesEvaluadas,
            "cortesConNuevasPredicciones", combinacionesConDatos,
            "matriculasProcesadas", matriculasProcesadas,
            "errores", errores,
            "detallesError", detallesError
        );
    }

    private CorteSeguimientoRespuestaDto mapear(CorteSeguimiento corte) {
        CorteSeguimientoRespuestaDto dto = new CorteSeguimientoRespuestaDto();
        dto.setId(corte.getId()); dto.setPeriodoAcademicoId(corte.getPeriodoAcademico().getId());
        dto.setSemana(corte.getSemana()); dto.setFechaCorte(corte.getFechaCorte()); return dto;
    }

    private EvaluacionPendienteFechaRespuestaDto mapearPendiente(Evaluacion e) {
        EvaluacionPendienteFechaRespuestaDto dto = new EvaluacionPendienteFechaRespuestaDto();
        dto.setEvaluacionId(e.getId()); dto.setAsignacionId(e.getDocenteCursoSeccion().getId());
        dto.setPeriodoEvaluacionId(e.getPeriodoEvaluacion().getId()); dto.setPeriodoEvaluacion(e.getPeriodoEvaluacion().getNombre());
        dto.setCurso(e.getDocenteCursoSeccion().getCurso().getNombre()); dto.setTipoEvaluacion(e.getTipoEvaluacion().getNombre());
        dto.setNombre(e.getNombre()); dto.setNumeroEvaluacion(e.getNumeroEvaluacion()); dto.setFechaEvaluacion(e.getFechaEvaluacion());
        return dto;
    }
}
