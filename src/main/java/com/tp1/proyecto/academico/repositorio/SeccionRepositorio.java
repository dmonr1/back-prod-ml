package com.tp1.proyecto.academico.repositorio;

import com.tp1.proyecto.academico.entidad.Seccion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeccionRepositorio extends JpaRepository<Seccion, Long> {

    List<Seccion> findByGradoId(Long gradoId);

    List<Seccion> findByPeriodoAcademicoId(Long periodoAcademicoId);

    List<Seccion> findByGradoIdAndPeriodoAcademicoId(Long gradoId, Long periodoAcademicoId);

    Optional<Seccion> findByNombreAndGradoIdAndPeriodoAcademicoId(String nombre, Long gradoId, Long periodoAcademicoId);
}
