package com.tp1.proyecto.academico.repositorio;

import com.tp1.proyecto.academico.entidad.Seccion;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SeccionRepositorio extends JpaRepository<Seccion, Long> {

    List<Seccion> findByGradoId(Long gradoId);

    List<Seccion> findByPeriodoAcademicoId(Long periodoAcademicoId);

    List<Seccion> findByGradoIdAndPeriodoAcademicoId(Long gradoId, Long periodoAcademicoId);

    Optional<Seccion> findByNombreAndGradoIdAndPeriodoAcademicoId(String nombre, Long gradoId, Long periodoAcademicoId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Seccion s where s.id = :id")
    Optional<Seccion> findByIdForUpdate(@Param("id") Long id);
}
