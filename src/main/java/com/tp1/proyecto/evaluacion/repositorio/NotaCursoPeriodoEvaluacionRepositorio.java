package com.tp1.proyecto.evaluacion.repositorio;

import com.tp1.proyecto.evaluacion.entidad.NotaCursoPeriodoEvaluacion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotaCursoPeriodoEvaluacionRepositorio extends JpaRepository<NotaCursoPeriodoEvaluacion, Long> {

    Optional<NotaCursoPeriodoEvaluacion> findByMatriculaIdAndCursoIdAndPeriodoEvaluacionId(Long matriculaId, Long cursoId, Long periodoEvaluacionId);

    List<NotaCursoPeriodoEvaluacion> findByMatriculaIdAndPeriodoEvaluacionId(Long matriculaId, Long periodoEvaluacionId);
}
