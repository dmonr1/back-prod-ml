package com.tp1.proyecto.prediccion.repositorio;

import com.tp1.proyecto.prediccion.entidad.PrediccionRiesgoCurso;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PrediccionRiesgoCursoRepositorio extends JpaRepository<PrediccionRiesgoCurso, Long> {

    @Query("SELECT p FROM PrediccionRiesgoCurso p WHERE p.matricula.id = :matriculaId AND p.curso.id = :cursoId AND p.periodoEvaluacion.id = :periodoEvaluacionId AND p.corteSeguimiento IS NULL")
    Optional<PrediccionRiesgoCurso> findByMatriculaIdAndCursoIdAndPeriodoEvaluacionId(
        @Param("matriculaId") Long matriculaId,
        @Param("cursoId") Long cursoId,
        @Param("periodoEvaluacionId") Long periodoEvaluacionId
    );

    @Query("SELECT p FROM PrediccionRiesgoCurso p WHERE p.matricula.id = :matriculaId AND p.periodoEvaluacion.id = :periodoEvaluacionId AND p.corteSeguimiento IS NULL")
    List<PrediccionRiesgoCurso> findByMatriculaIdAndPeriodoEvaluacionId(
        @Param("matriculaId") Long matriculaId,
        @Param("periodoEvaluacionId") Long periodoEvaluacionId
    );

    @Query("SELECT p FROM PrediccionRiesgoCurso p WHERE p.periodoEvaluacion.id = :periodoEvaluacionId AND p.matricula.seccion.id = :seccionId AND p.corteSeguimiento IS NULL")
    List<PrediccionRiesgoCurso> findByPeriodoEvaluacionIdAndMatriculaSeccionId(
        @Param("periodoEvaluacionId") Long periodoEvaluacionId,
        @Param("seccionId") Long seccionId
    );

    Optional<PrediccionRiesgoCurso> findByMatriculaIdAndCursoIdAndCorteSeguimientoId(
        Long matriculaId, Long cursoId, Long corteSeguimientoId
    );

    List<PrediccionRiesgoCurso> findByCorteSeguimientoIdAndMatriculaSeccionId(Long corteSeguimientoId, Long seccionId);

    List<PrediccionRiesgoCurso> findByMatriculaAlumnoIdOrderByFechaPrediccionDesc(Long alumnoId);
}
