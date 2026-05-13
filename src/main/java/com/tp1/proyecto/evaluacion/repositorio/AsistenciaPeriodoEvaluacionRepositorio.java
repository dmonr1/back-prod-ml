package com.tp1.proyecto.evaluacion.repositorio;

import com.tp1.proyecto.evaluacion.entidad.AsistenciaPeriodoEvaluacion;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AsistenciaPeriodoEvaluacionRepositorio extends JpaRepository<AsistenciaPeriodoEvaluacion, Long> {

    Optional<AsistenciaPeriodoEvaluacion> findByMatriculaIdAndPeriodoEvaluacionId(Long matriculaId, Long periodoEvaluacionId);
}
