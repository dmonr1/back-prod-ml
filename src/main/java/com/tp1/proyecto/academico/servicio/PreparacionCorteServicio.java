package com.tp1.proyecto.academico.servicio;

import com.tp1.proyecto.academico.dto.PreparacionCorteRespuestaDto;
import com.tp1.proyecto.academico.entidad.CorteSeguimiento;
import com.tp1.proyecto.academico.entidad.PeriodoEvaluacion;
import com.tp1.proyecto.academico.repositorio.CorteSeguimientoRepositorio;
import com.tp1.proyecto.academico.repositorio.HorarioSemanalRepositorio;
import com.tp1.proyecto.academico.repositorio.MatriculaRepositorio;
import com.tp1.proyecto.academico.repositorio.PeriodoEvaluacionRepositorio;
import com.tp1.proyecto.comun.enumeracion.EstadoRegistro;
import com.tp1.proyecto.evaluacion.entidad.Evaluacion;
import com.tp1.proyecto.evaluacion.repositorio.AsistenciaSesionRepositorio;
import com.tp1.proyecto.evaluacion.repositorio.DetalleNotaEvaluacionRepositorio;
import com.tp1.proyecto.evaluacion.repositorio.EvaluacionRepositorio;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class PreparacionCorteServicio {
    private final CorteSeguimientoRepositorio cortes;
    private final PeriodoEvaluacionRepositorio periodos;
    private final MatriculaRepositorio matriculas;
    private final EvaluacionRepositorio evaluaciones;
    private final DetalleNotaEvaluacionRepositorio notas;
    private final AsistenciaSesionRepositorio asistencias;
    private final HorarioSemanalRepositorio horarios;

    public PreparacionCorteServicio(
        CorteSeguimientoRepositorio cortes,
        PeriodoEvaluacionRepositorio periodos,
        MatriculaRepositorio matriculas,
        EvaluacionRepositorio evaluaciones,
        DetalleNotaEvaluacionRepositorio notas,
        AsistenciaSesionRepositorio asistencias,
        HorarioSemanalRepositorio horarios
    ) {
        this.cortes = cortes;
        this.periodos = periodos;
        this.matriculas = matriculas;
        this.evaluaciones = evaluaciones;
        this.notas = notas;
        this.asistencias = asistencias;
        this.horarios = horarios;
    }

    public PreparacionCorteRespuestaDto obtener(Long corteId, Long seccionId) {
        CorteSeguimiento corte = cortes.findById(corteId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Corte de seguimiento no encontrado."));
        Long periodoId = corte.getPeriodoAcademico().getId();
        LocalDate fechaCorte = corte.getFechaCorte();
        PeriodoEvaluacion periodoActual = periodos.findByPeriodoAcademicoId(periodoId).stream()
            .filter(p -> p.getEstado() == EstadoRegistro.ACTIVO)
            .filter(p -> !fechaCorte.isBefore(p.getFechaInicio()) && !fechaCorte.isAfter(p.getFechaFin()))
            .findFirst().orElse(null);
        String periodoEvaluacion = periodoActual == null ? null : periodoActual.getNombre();

        Set<Long> matriculaIds = new HashSet<>();
        matriculas.findBySeccionIdAndPeriodoAcademicoId(seccionId, periodoId).stream()
            .filter(m -> m.getEstado() == EstadoRegistro.ACTIVO)
            .forEach(m -> matriculaIds.add(m.getId()));

        var configuradas = evaluaciones
            .findByDocenteCursoSeccionSeccionIdAndDocenteCursoSeccionPeriodoAcademicoIdAndDocenteCursoSeccionEstadoAndEstadoOrderByPeriodoEvaluacionNumeroAscTipoEvaluacionOrdenAscNumeroEvaluacionAsc(
                seccionId, periodoId, EstadoRegistro.ACTIVO, EstadoRegistro.ACTIVO);
        int sinFecha = (int) configuradas.stream()
            .filter(e -> periodoActual != null && e.getPeriodoEvaluacion().getId().equals(periodoActual.getId()))
            .filter(e -> e.getFechaEvaluacion() == null)
            .count();
        var alCorte = configuradas.stream()
            .filter(e -> e.getFechaEvaluacion() != null && !e.getFechaEvaluacion().isAfter(fechaCorte)).toList();
        var evaluacionIds = alCorte.stream().map(Evaluacion::getId).toList();

        Set<Long> alumnosConDatos = new HashSet<>();
        Set<Long> alumnosConAsistencia = new HashSet<>();
        int notasRegistradas = 0;
        if (!evaluacionIds.isEmpty() && !matriculaIds.isEmpty()) {
            var detalles = notas.findByEvaluacionIdIn(evaluacionIds).stream()
                .filter(d -> d.getEstado() == EstadoRegistro.ACTIVO && matriculaIds.contains(d.getMatricula().getId()))
                .toList();
            notasRegistradas = detalles.size();
            detalles.forEach(d -> alumnosConDatos.add(d.getMatricula().getId()));
        }
        var sesiones = asistencias
            .findByAsignacionSeccionIdAndAsignacionPeriodoAcademicoIdAndFechaClaseLessThanEqualAndEstado(
                seccionId, periodoId, fechaCorte, EstadoRegistro.ACTIVO)
            .stream().filter(s -> matriculaIds.contains(s.getMatricula().getId())).toList();
        sesiones.forEach(s -> {
            alumnosConDatos.add(s.getMatricula().getId());
            alumnosConAsistencia.add(s.getMatricula().getId());
        });
        int bloques = (int) horarios.findByAsignacionPeriodoAcademicoIdAndEstado(periodoId, EstadoRegistro.ACTIVO)
            .stream().filter(h -> h.getAsignacion().getSeccion().getId().equals(seccionId)).count();

        return new PreparacionCorteRespuestaDto(
            corte.getPeriodoAcademico().getNombre(), periodoEvaluacion, fechaCorte,
            !fechaCorte.isAfter(LocalDate.now()), matriculaIds.size(), alumnosConDatos.size(),
            alumnosConAsistencia.size(), sinFecha, alCorte.size(), alCorte.size() * matriculaIds.size(),
            notasRegistradas, bloques, sesiones.size()
        );
    }
}
