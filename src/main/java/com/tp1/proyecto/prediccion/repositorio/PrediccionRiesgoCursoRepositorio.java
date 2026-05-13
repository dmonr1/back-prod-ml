package com.tp1.proyecto.prediccion.repositorio;

import com.tp1.proyecto.prediccion.entidad.PrediccionRiesgoCurso;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrediccionRiesgoCursoRepositorio extends JpaRepository<PrediccionRiesgoCurso, Long> {

    Optional<PrediccionRiesgoCurso> findByMatriculaIdAndCursoIdAndPeriodoEvaluacionId(Long matriculaId, Long cursoId, Long periodoEvaluacionId);

    List<PrediccionRiesgoCurso> findByMatriculaIdAndPeriodoEvaluacionId(Long matriculaId, Long periodoEvaluacionId);

    List<PrediccionRiesgoCurso> findByPeriodoEvaluacionIdAndMatriculaSeccionId(Long periodoEvaluacionId, Long seccionId);
}
