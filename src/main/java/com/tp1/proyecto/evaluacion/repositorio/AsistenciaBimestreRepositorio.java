package com.tp1.proyecto.evaluacion.repositorio;

import com.tp1.proyecto.evaluacion.entidad.AsistenciaBimestre;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AsistenciaBimestreRepositorio extends JpaRepository<AsistenciaBimestre, Long> {

    Optional<AsistenciaBimestre> findByMatriculaIdAndBimestreId(Long matriculaId, Long bimestreId);
}
