package com.tp1.proyecto.academico.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.tp1.proyecto.academico.entidad.CorteSeguimiento;
import com.tp1.proyecto.academico.entidad.Matricula;
import com.tp1.proyecto.academico.entidad.PeriodoAcademico;
import com.tp1.proyecto.academico.entidad.PeriodoEvaluacion;
import com.tp1.proyecto.academico.repositorio.CorteSeguimientoRepositorio;
import com.tp1.proyecto.academico.repositorio.HorarioSemanalRepositorio;
import com.tp1.proyecto.academico.repositorio.MatriculaRepositorio;
import com.tp1.proyecto.academico.repositorio.PeriodoEvaluacionRepositorio;
import com.tp1.proyecto.comun.enumeracion.EstadoRegistro;
import com.tp1.proyecto.evaluacion.entidad.AsistenciaSesion;
import com.tp1.proyecto.evaluacion.entidad.DetalleNotaEvaluacion;
import com.tp1.proyecto.evaluacion.entidad.Evaluacion;
import com.tp1.proyecto.evaluacion.repositorio.AsistenciaSesionRepositorio;
import com.tp1.proyecto.evaluacion.repositorio.DetalleNotaEvaluacionRepositorio;
import com.tp1.proyecto.evaluacion.repositorio.EvaluacionRepositorio;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class PreparacionCorteServicioTest {
    @Test
    void cuentaSoloRegistrosDisponiblesHastaElCorte() {
        var cortes = mock(CorteSeguimientoRepositorio.class);
        var periodos = mock(PeriodoEvaluacionRepositorio.class);
        var matriculas = mock(MatriculaRepositorio.class);
        var evaluaciones = mock(EvaluacionRepositorio.class);
        var notas = mock(DetalleNotaEvaluacionRepositorio.class);
        var asistencias = mock(AsistenciaSesionRepositorio.class);
        var horarios = mock(HorarioSemanalRepositorio.class);
        var servicio = new PreparacionCorteServicio(cortes, periodos, matriculas, evaluaciones, notas, asistencias, horarios);

        LocalDate fecha = LocalDate.of(2025, 4, 5);
        var periodoAcademico = mock(PeriodoAcademico.class);
        when(periodoAcademico.getId()).thenReturn(1L);
        when(periodoAcademico.getNombre()).thenReturn("Año 2025");
        var corte = mock(CorteSeguimiento.class);
        when(corte.getPeriodoAcademico()).thenReturn(periodoAcademico);
        when(corte.getFechaCorte()).thenReturn(fecha);
        when(cortes.findById(3L)).thenReturn(Optional.of(corte));

        var periodoEvaluacion = mock(PeriodoEvaluacion.class);
        when(periodoEvaluacion.getEstado()).thenReturn(EstadoRegistro.ACTIVO);
        when(periodoEvaluacion.getFechaInicio()).thenReturn(LocalDate.of(2025, 3, 1));
        when(periodoEvaluacion.getFechaFin()).thenReturn(LocalDate.of(2025, 5, 31));
        when(periodoEvaluacion.getNombre()).thenReturn("PERIODO I");
        when(periodos.findByPeriodoAcademicoId(1L)).thenReturn(List.of(periodoEvaluacion));

        var alumnoConNota = mock(Matricula.class);
        var alumnoSinDatos = mock(Matricula.class);
        when(alumnoConNota.getId()).thenReturn(10L);
        when(alumnoConNota.getEstado()).thenReturn(EstadoRegistro.ACTIVO);
        when(alumnoSinDatos.getId()).thenReturn(11L);
        when(alumnoSinDatos.getEstado()).thenReturn(EstadoRegistro.ACTIVO);
        when(matriculas.findBySeccionIdAndPeriodoAcademicoId(2L, 1L))
            .thenReturn(List.of(alumnoConNota, alumnoSinDatos));

        var evaluacionAlCorte = mock(Evaluacion.class);
        when(evaluacionAlCorte.getId()).thenReturn(20L);
        when(evaluacionAlCorte.getFechaEvaluacion()).thenReturn(fecha.minusDays(1));
        var evaluacionFutura = mock(Evaluacion.class);
        when(evaluacionFutura.getFechaEvaluacion()).thenReturn(fecha.plusDays(1));
        var evaluacionSinFecha = mock(Evaluacion.class);
        when(evaluaciones.findByDocenteCursoSeccionSeccionIdAndDocenteCursoSeccionPeriodoAcademicoIdAndEstadoOrderByPeriodoEvaluacionNumeroAscTipoEvaluacionOrdenAscNumeroEvaluacionAsc(
            2L, 1L, EstadoRegistro.ACTIVO))
            .thenReturn(List.of(evaluacionAlCorte, evaluacionFutura, evaluacionSinFecha));

        var detalle = mock(DetalleNotaEvaluacion.class);
        when(detalle.getEstado()).thenReturn(EstadoRegistro.ACTIVO);
        when(detalle.getMatricula()).thenReturn(alumnoConNota);
        when(notas.findByEvaluacionIdIn(List.of(20L))).thenReturn(List.of(detalle));
        var sesion = mock(AsistenciaSesion.class);
        when(sesion.getMatricula()).thenReturn(alumnoConNota);
        when(asistencias.findByAsignacionSeccionIdAndAsignacionPeriodoAcademicoIdAndFechaClaseLessThanEqualAndEstado(
            2L, 1L, fecha, EstadoRegistro.ACTIVO)).thenReturn(List.of(sesion));

        var resultado = servicio.obtener(3L, 2L);

        assertEquals("PERIODO I", resultado.periodoEvaluacion());
        assertTrue(resultado.corteDisponible());
        assertEquals(2, resultado.alumnosMatriculados());
        assertEquals(1, resultado.alumnosConDatos());
        assertEquals(1, resultado.evaluacionesSinFecha());
        assertEquals(1, resultado.evaluacionesAlCorte());
        assertEquals(2, resultado.notasEsperadas());
        assertEquals(1, resultado.notasRegistradas());
        assertEquals(1, resultado.alumnosConAsistencia());
    }
}
