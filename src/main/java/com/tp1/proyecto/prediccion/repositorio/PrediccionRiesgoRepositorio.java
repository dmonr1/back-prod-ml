package com.tp1.proyecto.prediccion.repositorio;

import com.tp1.proyecto.prediccion.entidad.PrediccionRiesgo;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrediccionRiesgoRepositorio extends JpaRepository<PrediccionRiesgo, Long> {

    Optional<PrediccionRiesgo> findByMatriculaIdAndBimestreId(Long matriculaId, Long bimestreId);

    List<PrediccionRiesgo> findByBimestreId(Long bimestreId);

    List<PrediccionRiesgo> findByBimestreIdAndMatriculaSeccionId(Long bimestreId, Long seccionId);

    List<PrediccionRiesgo> findByMatriculaAlumnoIdOrderByFechaPrediccionDesc(Long alumnoId);
}
