package com.tp1.proyecto.evaluacion.repositorio;

import com.tp1.proyecto.evaluacion.entidad.NotaCursoBimestre;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotaCursoBimestreRepositorio extends JpaRepository<NotaCursoBimestre, Long> {

    Optional<NotaCursoBimestre> findByMatriculaIdAndCursoIdAndBimestreId(Long matriculaId, Long cursoId, Long bimestreId);

    List<NotaCursoBimestre> findByMatriculaIdAndBimestreId(Long matriculaId, Long bimestreId);
}
