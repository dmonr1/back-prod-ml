package com.tp1.proyecto.alerta.servicio.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tp1.proyecto.academico.entidad.BloqueHorario;
import com.tp1.proyecto.academico.entidad.DocenteCursoSeccion;
import com.tp1.proyecto.academico.entidad.DiaSemana;
import com.tp1.proyecto.academico.entidad.HorarioSemanal;
import com.tp1.proyecto.academico.entidad.Matricula;
import com.tp1.proyecto.academico.entidad.PeriodoAcademico;
import com.tp1.proyecto.academico.entidad.PeriodoEvaluacion;
import com.tp1.proyecto.academico.entidad.Seccion;
import com.tp1.proyecto.academico.repositorio.HorarioSemanalRepositorio;
import com.tp1.proyecto.academico.repositorio.MatriculaRepositorio;
import com.tp1.proyecto.academico.repositorio.PeriodoEvaluacionRepositorio;
import com.tp1.proyecto.alerta.entidad.AlertaAcademica;
import com.tp1.proyecto.alerta.repositorio.AlertaAcademicaRepositorio;
import com.tp1.proyecto.comun.enumeracion.EstadoRegistro;
import com.tp1.proyecto.evaluacion.entidad.AsistenciaSesion;
import com.tp1.proyecto.evaluacion.entidad.DetalleNotaEvaluacion;
import com.tp1.proyecto.evaluacion.entidad.Evaluacion;
import com.tp1.proyecto.evaluacion.evento.AsistenciaSesionRegistradaEvent;
import com.tp1.proyecto.evaluacion.repositorio.AsistenciaSesionRepositorio;
import com.tp1.proyecto.evaluacion.repositorio.DetalleNotaEvaluacionRepositorio;
import com.tp1.proyecto.evaluacion.repositorio.EvaluacionRepositorio;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AlertaAcademicaServicioImplTest {

    private final AlertaAcademicaRepositorio alertaRepositorio = mock(AlertaAcademicaRepositorio.class);
    private final HorarioSemanalRepositorio horarioRepositorio = mock(HorarioSemanalRepositorio.class);
    private final PeriodoEvaluacionRepositorio periodoRepositorio = mock(PeriodoEvaluacionRepositorio.class);
    private final MatriculaRepositorio matriculaRepositorio = mock(MatriculaRepositorio.class);
    private final AsistenciaSesionRepositorio asistenciaRepositorio = mock(AsistenciaSesionRepositorio.class);
    private final EvaluacionRepositorio evaluacionRepositorio = mock(EvaluacionRepositorio.class);
    private final DetalleNotaEvaluacionRepositorio detalleRepositorio = mock(DetalleNotaEvaluacionRepositorio.class);

    private AlertaAcademicaServicioImpl servicio;

    @BeforeEach
    void crearServicio() {
        servicio = new AlertaAcademicaServicioImpl(
            alertaRepositorio,
            horarioRepositorio,
            periodoRepositorio,
            matriculaRepositorio,
            asistenciaRepositorio,
            evaluacionRepositorio,
            detalleRepositorio
        );
    }

    @Test
    void actualizaLaAlertaInmediatamenteCuandoLaAsistenciaQuedaCompleta() {
        LocalDate fecha = LocalDate.of(2026, 10, 7);
        HorarioSemanal horario = mock(HorarioSemanal.class);
        BloqueHorario bloque = mock(BloqueHorario.class);
        DocenteCursoSeccion asignacion = mock(DocenteCursoSeccion.class);
        PeriodoAcademico periodoAcademico = mock(PeriodoAcademico.class);
        PeriodoEvaluacion periodoEvaluacion = mock(PeriodoEvaluacion.class);
        Seccion seccion = mock(Seccion.class);
        Matricula matricula = mock(Matricula.class);
        AsistenciaSesion asistencia = mock(AsistenciaSesion.class);

        when(horario.getId()).thenReturn(4653L);
        when(horario.getAsignacion()).thenReturn(asignacion);
        when(horario.getDiaSemana()).thenReturn(DiaSemana.MIERCOLES);
        when(horario.getBloque()).thenReturn(bloque);
        when(bloque.getHoraInicio()).thenReturn(LocalTime.of(12, 50));
        when(bloque.getHoraFin()).thenReturn(LocalTime.of(13, 40));
        when(asignacion.getId()).thenReturn(4L);
        when(asignacion.getSeccion()).thenReturn(seccion);
        when(asignacion.getPeriodoAcademico()).thenReturn(periodoAcademico);
        when(periodoAcademico.getId()).thenReturn(1L);
        when(periodoAcademico.getEstado()).thenReturn(EstadoRegistro.ACTIVO);
        when(seccion.getId()).thenReturn(8L);
        when(periodoEvaluacion.getEstado()).thenReturn(EstadoRegistro.ACTIVO);
        when(periodoEvaluacion.getId()).thenReturn(3L);
        when(periodoEvaluacion.getFechaInicio()).thenReturn(fecha.minusDays(10));
        when(periodoEvaluacion.getFechaFin()).thenReturn(fecha.plusDays(10));
        when(matricula.getId()).thenReturn(101L);
        when(asistencia.getMatricula()).thenReturn(matricula);

        when(horarioRepositorio.findByIdAndEstado(4653L, EstadoRegistro.ACTIVO)).thenReturn(Optional.of(horario));
        when(periodoRepositorio.findById(3L)).thenReturn(Optional.of(periodoEvaluacion));
        when(horarioRepositorio.findByAsignacionPeriodoAcademicoIdAndEstado(1L, EstadoRegistro.ACTIVO))
            .thenReturn(List.of(horario));
        when(matriculaRepositorio.findBySeccionIdAndPeriodoAcademicoIdAndEstado(8L, 1L, EstadoRegistro.ACTIVO))
            .thenReturn(List.of(matricula));
        when(asistenciaRepositorio.findByAsignacionIdAndPeriodoEvaluacionIdAndFechaClaseAndEstado(
            4L, 3L, fecha, EstadoRegistro.ACTIVO
        )).thenReturn(List.of(asistencia));

        AlertaAcademica alerta = new AlertaAcademica();
        alerta.setCantidadPendiente(23);
        alerta.setEstadoAlerta("PENDIENTE");
        when(alertaRepositorio.findByClaveOrigen("ASISTENCIA:4653:2026-10-07"))
            .thenReturn(Optional.of(alerta));

        servicio.alRegistrarAsistencia(new AsistenciaSesionRegistradaEvent(3L, List.of(101L), 4L, 4653L, fecha));

        assertEquals(0, alerta.getCantidadPendiente());
        assertEquals("ATENDIDA", alerta.getEstadoAlerta());
        verify(alertaRepositorio).save(alerta);
    }

    @Test
    void detectaNotasEnLoteYNoGeneraAlertaCuandoTodosYaTienenNota() {
        LocalDate fecha = LocalDate.now(ZoneId.of("America/Lima")).minusDays(1);
        Evaluacion evaluacion = mock(Evaluacion.class);
        DocenteCursoSeccion asignacion = mock(DocenteCursoSeccion.class);
        PeriodoAcademico periodoAcademico = mock(PeriodoAcademico.class);
        Seccion seccion = mock(Seccion.class);
        Matricula matricula = mock(Matricula.class);
        DetalleNotaEvaluacion detalle = mock(DetalleNotaEvaluacion.class);

        when(evaluacion.getId()).thenReturn(50L);
        when(evaluacion.getFechaEvaluacion()).thenReturn(fecha);
        when(evaluacion.getDocenteCursoSeccion()).thenReturn(asignacion);
        when(asignacion.getId()).thenReturn(4L);
        when(asignacion.getEstado()).thenReturn(EstadoRegistro.ACTIVO);
        when(asignacion.getPeriodoAcademico()).thenReturn(periodoAcademico);
        when(asignacion.getSeccion()).thenReturn(seccion);
        when(periodoAcademico.getId()).thenReturn(1L);
        when(periodoAcademico.getEstado()).thenReturn(EstadoRegistro.ACTIVO);
        when(periodoAcademico.getFechaInicio()).thenReturn(fecha.minusMonths(1));
        when(periodoAcademico.getFechaFin()).thenReturn(fecha.plusMonths(1));
        when(seccion.getId()).thenReturn(8L);
        when(matricula.getId()).thenReturn(101L);
        when(matricula.getSeccion()).thenReturn(seccion);
        when(detalle.getEvaluacion()).thenReturn(evaluacion);
        when(detalle.getMatricula()).thenReturn(matricula);

        when(horarioRepositorio.findByEstado(EstadoRegistro.ACTIVO)).thenReturn(List.of());
        when(evaluacionRepositorio.findByDocenteCursoSeccionEstadoAndEstadoAndFechaEvaluacionIsNotNull(
            EstadoRegistro.ACTIVO, EstadoRegistro.ACTIVO
        )).thenReturn(List.of(evaluacion));
        when(matriculaRepositorio.findByPeriodoAcademicoIdAndEstado(1L, EstadoRegistro.ACTIVO))
            .thenReturn(List.of(matricula));
        when(detalleRepositorio.findByEvaluacionIdInAndEstado(List.of(50L), EstadoRegistro.ACTIVO))
            .thenReturn(List.of(detalle));
        when(alertaRepositorio.findByClaveOrigenIn(List.of("NOTAS:50"))).thenReturn(List.of());
        when(alertaRepositorio.findByEstadoAlertaOrderByFechaLimiteDesc("PENDIENTE"))
            .thenReturn(List.of());

        servicio.detectarPendientes();

        verify(alertaRepositorio, never()).findByClaveOrigen("NOTAS:50");
        verify(alertaRepositorio, never()).saveAll(anyList());
        verify(detalleRepositorio, never()).findByEvaluacionIdAndEstado(50L, EstadoRegistro.ACTIVO);
    }
}
