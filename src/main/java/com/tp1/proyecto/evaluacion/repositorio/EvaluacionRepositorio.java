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

    List<Evaluacion> findByDocenteCursoSeccionIdAndPeriodoEvaluacionIdAndEstadoOrderByTipoEvaluacionOrdenAscNumeroEvaluacionAsc(
        Long docenteCursoSeccionId,
        Long periodoEvaluacionId,
        com.tp1.proyecto.comun.enumeracion.EstadoRegistro estado
    );

    List<Evaluacion> findByDocenteCursoSeccionIdAndPeriodoEvaluacionIdAndTipoEvaluacionIdOrderByNumeroEvaluacionAsc(
        Long docenteCursoSeccionId,
        Long periodoEvaluacionId,
        Long tipoEvaluacionId
    );

    List<Evaluacion> findByDocenteCursoSeccionIdAndPeriodoEvaluacionIdAndTipoEvaluacionIdAndEstadoOrderByNumeroEvaluacionAsc(
        Long docenteCursoSeccionId,
        Long periodoEvaluacionId,
        Long tipoEvaluacionId,
        com.tp1.proyecto.comun.enumeracion.EstadoRegistro estado
    );

    List<Evaluacion> findByDocenteCursoSeccionCursoIdAndDocenteCursoSeccionSeccionIdAndPeriodoEvaluacionIdAndEstadoOrderByTipoEvaluacionOrdenAscNumeroEvaluacionAsc(
        Long cursoId,
        Long seccionId,
        Long periodoEvaluacionId,
        com.tp1.proyecto.comun.enumeracion.EstadoRegistro estado
    );

    boolean existsByDocenteCursoSeccionIdAndPeriodoEvaluacionIdAndTipoEvaluacionIdAndNumeroEvaluacion(
        Long docenteCursoSeccionId,
        Long periodoEvaluacionId,
        Long tipoEvaluacionId,
        Integer numeroEvaluacion
    );
}
