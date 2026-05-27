package com.tp1.proyecto.seguridad.repositorio;

import com.tp1.proyecto.seguridad.entidad.RecuperacionPasswordToken;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecuperacionPasswordTokenRepositorio extends JpaRepository<RecuperacionPasswordToken, Long> {

    List<RecuperacionPasswordToken> findByUsuarioIdAndUsadoFalse(Long usuarioId);

    Optional<RecuperacionPasswordToken> findTopByUsuarioIdAndUsadoFalseOrderByFechaCreacionDesc(Long usuarioId);

    Optional<RecuperacionPasswordToken> findByTokenRecuperacionAndUsadoFalse(String tokenRecuperacion);
}
