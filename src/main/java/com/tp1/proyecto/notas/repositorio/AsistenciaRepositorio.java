package com.tp1.proyecto.notas.repositorio;

import com.tp1.proyecto.notas.entidad.Asistencia;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AsistenciaRepositorio extends JpaRepository<Asistencia, Long> {

    Optional<Asistencia> findByMatriculaIdAndBimestreId(Long matriculaId, Long bimestreId);

    List<Asistencia> findByBimestreId(Long bimestreId);
}
