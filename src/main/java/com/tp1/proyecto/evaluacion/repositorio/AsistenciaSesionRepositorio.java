package com.tp1.proyecto.evaluacion.repositorio;

import com.tp1.proyecto.evaluacion.entidad.AsistenciaSesion;
import com.tp1.proyecto.comun.enumeracion.EstadoRegistro;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AsistenciaSesionRepositorio extends JpaRepository<AsistenciaSesion, Long> {

    Optional<AsistenciaSesion> findByAsignacionIdAndPeriodoEvaluacionIdAndFechaClaseAndMatriculaId(
        Long asignacionId,
        Long periodoEvaluacionId,
        LocalDate fechaClase,
        Long matriculaId
    );

    Optional<AsistenciaSesion> findByHorarioSemanalIdAndAsignacionIdAndPeriodoEvaluacionIdAndFechaClaseAndMatriculaId(
        Long horarioSemanalId, Long asignacionId, Long periodoEvaluacionId, LocalDate fechaClase, Long matriculaId
    );

    Optional<AsistenciaSesion> findByHorarioSemanalIsNullAndAsignacionIdAndPeriodoEvaluacionIdAndFechaClaseAndMatriculaId(
        Long asignacionId, Long periodoEvaluacionId, LocalDate fechaClase, Long matriculaId
    );

    List<AsistenciaSesion> findByAsignacionIdAndPeriodoEvaluacionIdAndFechaClaseAndEstado(
        Long asignacionId,
        Long periodoEvaluacionId,
        LocalDate fechaClase,
        EstadoRegistro estado
    );

    List<AsistenciaSesion> findByHorarioSemanalIdAndAsignacionIdAndPeriodoEvaluacionIdAndFechaClaseAndEstado(
        Long horarioSemanalId, Long asignacionId, Long periodoEvaluacionId, LocalDate fechaClase, EstadoRegistro estado
    );

    List<AsistenciaSesion> findByMatriculaIdAndPeriodoEvaluacionIdAndEstado(
        Long matriculaId, Long periodoEvaluacionId, EstadoRegistro estado
    );

    List<AsistenciaSesion> findByAsignacionSeccionIdAndAsignacionPeriodoAcademicoIdAndFechaClaseLessThanEqualAndEstado(
        Long seccionId, Long periodoAcademicoId, LocalDate fechaCorte, EstadoRegistro estado
    );
}
