package com.tp1.proyecto.academico.repositorio;

import com.tp1.proyecto.academico.entidad.Grado;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradoRepositorio extends JpaRepository<Grado, Long> {

    List<Grado> findByNivelId(Long nivelId);

    Optional<Grado> findByNombreAndNivelId(String nombre, Long nivelId);
}
