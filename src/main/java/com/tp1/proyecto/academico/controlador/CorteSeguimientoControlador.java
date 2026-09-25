package com.tp1.proyecto.academico.controlador;

import com.tp1.proyecto.academico.dto.CorteSeguimientoRespuestaDto;
import com.tp1.proyecto.academico.dto.PreparacionCorteRespuestaDto;
import com.tp1.proyecto.academico.entidad.CorteSeguimiento;
import com.tp1.proyecto.academico.repositorio.CorteSeguimientoRepositorio;
import com.tp1.proyecto.academico.repositorio.PeriodoEvaluacionRepositorio;
import com.tp1.proyecto.academico.servicio.PreparacionCorteServicio;
import com.tp1.proyecto.comun.enumeracion.EstadoRegistro;
import com.tp1.proyecto.evaluacion.dto.EvaluacionPendienteFechaRespuestaDto;
import com.tp1.proyecto.evaluacion.entidad.Evaluacion;
import com.tp1.proyecto.evaluacion.repositorio.EvaluacionRepositorio;
import com.tp1.proyecto.prediccion.servicio.CorteSeguimientoPrediccionServicio;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cortes-seguimiento")
@PreAuthorize("hasAnyRole('ADMIN','DOCENTE','DOCENTE_TUTOR')")
public class CorteSeguimientoControlador {
    private final CorteSeguimientoRepositorio corteRepositorio;
    private final PeriodoEvaluacionRepositorio periodoEvaluacionRepositorio;
    private final EvaluacionRepositorio evaluacionRepositorio;
    private final CorteSeguimientoPrediccionServicio prediccionServicio;
    private final PreparacionCorteServicio preparacionServicio;

    public CorteSeguimientoControlador(
        CorteSeguimientoRepositorio corteRepositorio,
        PeriodoEvaluacionRepositorio periodoEvaluacionRepositorio,
        EvaluacionRepositorio evaluacionRepositorio,
        CorteSeguimientoPrediccionServicio prediccionServicio,
        PreparacionCorteServicio preparacionServicio
    ) {
        this.corteRepositorio = corteRepositorio;
        this.periodoEvaluacionRepositorio = periodoEvaluacionRepositorio;
        this.evaluacionRepositorio = evaluacionRepositorio;
        this.prediccionServicio = prediccionServicio;
        this.preparacionServicio = preparacionServicio;
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
