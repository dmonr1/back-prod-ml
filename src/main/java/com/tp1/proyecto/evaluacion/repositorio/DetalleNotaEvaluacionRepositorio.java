package com.tp1.proyecto.evaluacion.repositorio;

import com.tp1.proyecto.evaluacion.entidad.DetalleNotaEvaluacion;
import java.util.List;
import java.util.Optional;
import com.tp1.proyecto.comun.enumeracion.EstadoRegistro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleNotaEvaluacionRepositorio extends JpaRepository<DetalleNotaEvaluacion, Long> {

    List<DetalleNotaEvaluacion> findByEvaluacionId(Long evaluacionId);

    List<DetalleNotaEvaluacion> findByEvaluacionIdAndEstado(Long evaluacionId, EstadoRegistro estado);

    List<DetalleNotaEvaluacion> findByEvaluacionIdIn(List<Long> evaluacionIds);

    Optional<DetalleNotaEvaluacion> findByEvaluacionIdAndMatriculaId(Long evaluacionId, Long matriculaId);

    List<DetalleNotaEvaluacion> findByEvaluacionIdInAndMatriculaIdAndEstado(List<Long> evaluacionIds, Long matriculaId, EstadoRegistro estado);
}
