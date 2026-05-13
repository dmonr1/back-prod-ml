package com.tp1.proyecto.alumno.repositorio;

import com.tp1.proyecto.alumno.entidad.Alumno;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlumnoRepositorio extends JpaRepository<Alumno, Long> {

    Optional<Alumno> findByCodigo(String codigo);

    Optional<Alumno> findByDni(String dni);

    Optional<Alumno> findTopByCodigoStartingWithOrderByCodigoDesc(String prefijo);
}
