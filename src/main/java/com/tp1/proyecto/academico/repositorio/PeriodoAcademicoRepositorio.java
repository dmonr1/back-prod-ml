package com.tp1.proyecto.academico.repositorio;

import com.tp1.proyecto.academico.entidad.PeriodoAcademico;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PeriodoAcademicoRepositorio extends JpaRepository<PeriodoAcademico, Long> {

    Optional<PeriodoAcademico> findByAnio(Integer anio);

    Optional<PeriodoAcademico> findFirstByAnioLessThanOrderByAnioDesc(Integer anio);
}
