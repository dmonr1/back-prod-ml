package com.tp1.proyecto.evaluacion.repositorio;

import com.tp1.proyecto.evaluacion.entidad.DetalleNotaEvaluacion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleNotaEvaluacionRepositorio extends JpaRepository<DetalleNotaEvaluacion, Long> {

    List<DetalleNotaEvaluacion> findByEvaluacionId(Long evaluacionId);

    Optional<DetalleNotaEvaluacion> findByEvaluacionIdAndMatriculaId(Long evaluacionId, Long matriculaId);
}
