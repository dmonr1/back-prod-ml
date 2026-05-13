package com.tp1.proyecto.notas.repositorio;

import com.tp1.proyecto.notas.entidad.Asistencia;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AsistenciaRepositorio extends JpaRepository<Asistencia, Long> {

    Optional<Asistencia> findByMatriculaIdAndPeriodoEvaluacionId(Long matriculaId, Long periodoEvaluacionId);

    List<Asistencia> findByPeriodoEvaluacionId(Long periodoEvaluacionId);
}
