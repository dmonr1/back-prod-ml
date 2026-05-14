package com.tp1.proyecto.evaluacion.repositorio;

import com.tp1.proyecto.evaluacion.entidad.Evaluacion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvaluacionRepositorio extends JpaRepository<Evaluacion, Long> {

    List<Evaluacion> findByDocenteCursoSeccionId(Long docenteCursoSeccionId);

    List<Evaluacion> findByDocenteCursoSeccionIdAndPeriodoEvaluacionIdOrderByTipoEvaluacionOrdenAscNumeroEvaluacionAsc(
        Long docenteCursoSeccionId,
        Long periodoEvaluacionId
    );

    List<Evaluacion> findByDocenteCursoSeccionIdAndPeriodoEvaluacionIdAndTipoEvaluacionIdOrderByNumeroEvaluacionAsc(
        Long docenteCursoSeccionId,
        Long periodoEvaluacionId,
        Long tipoEvaluacionId
    );

    boolean existsByDocenteCursoSeccionIdAndPeriodoEvaluacionIdAndTipoEvaluacionIdAndNumeroEvaluacion(
        Long docenteCursoSeccionId,
        Long periodoEvaluacionId,
        Long tipoEvaluacionId,
        Integer numeroEvaluacion
    );
}
