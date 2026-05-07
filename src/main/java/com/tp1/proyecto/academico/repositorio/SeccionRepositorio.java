package com.tp1.proyecto.academico.repositorio;

import com.tp1.proyecto.academico.entidad.Seccion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeccionRepositorio extends JpaRepository<Seccion, Long> {

    List<Seccion> findByGradoId(Long gradoId);
}
