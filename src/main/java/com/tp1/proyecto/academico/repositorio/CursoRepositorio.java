package com.tp1.proyecto.academico.repositorio;

import com.tp1.proyecto.academico.entidad.Curso;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CursoRepositorio extends JpaRepository<Curso, Long> {

    List<Curso> findByNivelId(Long nivelId);

    Optional<Curso> findByNombre(String nombre);
}
