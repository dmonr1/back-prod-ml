package com.tp1.proyecto.prediccion.repositorio;

import com.tp1.proyecto.prediccion.entidad.PrediccionRiesgoCurso;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrediccionRiesgoCursoRepositorio extends JpaRepository<PrediccionRiesgoCurso, Long> {

    Optional<PrediccionRiesgoCurso> findByMatriculaIdAndCursoIdAndBimestreId(Long matriculaId, Long cursoId, Long bimestreId);

    List<PrediccionRiesgoCurso> findByMatriculaIdAndBimestreId(Long matriculaId, Long bimestreId);

    List<PrediccionRiesgoCurso> findByBimestreIdAndMatriculaSeccionId(Long bimestreId, Long seccionId);
}
