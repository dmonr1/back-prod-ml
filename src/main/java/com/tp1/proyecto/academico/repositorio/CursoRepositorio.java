package com.tp1.proyecto.academico.repositorio;

import com.tp1.proyecto.academico.entidad.Curso;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CursoRepositorio extends JpaRepository<Curso, Long> {

    List<Curso> findByNivelId(Long nivelId);

    boolean existsByNombreAndNivelId(String nombre, Long nivelId);

    boolean existsByNombreAndNivelIdAndIdNot(String nombre, Long nivelId, Long id);
}
