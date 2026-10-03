package com.tp1.proyecto.evaluacion.repositorio;

import com.tp1.proyecto.evaluacion.entidad.PlanEvaluacionAsignacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanEvaluacionAsignacionRepositorio extends JpaRepository<PlanEvaluacionAsignacion, Long> {
    boolean existsByDocenteCursoSeccionIdAndPeriodoEvaluacionId(Long asignacionId, Long periodoEvaluacionId);
}
