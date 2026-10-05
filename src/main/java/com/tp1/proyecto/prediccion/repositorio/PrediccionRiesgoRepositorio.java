package com.tp1.proyecto.prediccion.repositorio;

import com.tp1.proyecto.prediccion.entidad.PrediccionRiesgo;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PrediccionRiesgoRepositorio extends JpaRepository<PrediccionRiesgo, Long> {

    @Query("SELECT p FROM PrediccionRiesgo p WHERE p.matricula.id = :matriculaId AND p.periodoEvaluacion.id = :periodoEvaluacionId AND p.corteSeguimiento IS NULL")
    Optional<PrediccionRiesgo> findByMatriculaIdAndPeriodoEvaluacionId(
        @Param("matriculaId") Long matriculaId,
        @Param("periodoEvaluacionId") Long periodoEvaluacionId
    );

    @Query("SELECT p FROM PrediccionRiesgo p WHERE p.periodoEvaluacion.id = :periodoEvaluacionId AND p.corteSeguimiento IS NULL")
    List<PrediccionRiesgo> findByPeriodoEvaluacionId(@Param("periodoEvaluacionId") Long periodoEvaluacionId);

    @Query("SELECT p FROM PrediccionRiesgo p WHERE p.periodoEvaluacion.id = :periodoEvaluacionId AND p.matricula.seccion.id = :seccionId AND p.corteSeguimiento IS NULL")
    List<PrediccionRiesgo> findByPeriodoEvaluacionIdAndMatriculaSeccionId(
        @Param("periodoEvaluacionId") Long periodoEvaluacionId,
        @Param("seccionId") Long seccionId
    );

    Optional<PrediccionRiesgo> findByMatriculaIdAndCorteSeguimientoId(Long matriculaId, Long corteSeguimientoId);

    List<PrediccionRiesgo> findByCorteSeguimientoIdAndMatriculaSeccionId(Long corteSeguimientoId, Long seccionId);

    List<PrediccionRiesgo> findByMatriculaAlumnoIdOrderByFechaPrediccionDesc(Long alumnoId);
}
