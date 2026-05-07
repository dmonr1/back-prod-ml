package com.tp1.proyecto.usuario.repositorio;

import com.tp1.proyecto.usuario.entidad.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {

    @EntityGraph(attributePaths = "roles")
    Optional<Usuario> findByUsername(String username);

    @EntityGraph(attributePaths = "roles")
    Optional<Usuario> findByCorreo(String correo);

    @EntityGraph(attributePaths = "roles")
    Optional<Usuario> findByUsernameOrCorreo(String username, String correo);
}
