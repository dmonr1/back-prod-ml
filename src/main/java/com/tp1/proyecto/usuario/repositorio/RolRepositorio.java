package com.tp1.proyecto.usuario.repositorio;

import com.tp1.proyecto.usuario.entidad.Rol;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolRepositorio extends JpaRepository<Rol, Long> {

    Optional<Rol> findByNombre(String nombre);
}
