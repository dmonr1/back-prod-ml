package com.tp1.proyecto.academico.repositorio;

import com.tp1.proyecto.academico.entidad.Nivel;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NivelRepositorio extends JpaRepository<Nivel, Long> {

    Optional<Nivel> findByNombre(String nombre);
}
