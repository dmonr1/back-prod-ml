package com.tp1.proyecto.academico.repositorio;

import com.tp1.proyecto.academico.entidad.CursoPeriodoAcademico;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CursoPeriodoAcademicoRepositorio extends JpaRepository<CursoPeriodoAcademico, Long> {

    List<CursoPeriodoAcademico> findByPeriodoAcademicoId(Long periodoAcademicoId);

    Optional<CursoPeriodoAcademico> findByPeriodoAcademicoIdAndCursoId(Long periodoAcademicoId, Long cursoId);

    boolean existsByPeriodoAcademicoIdAndCursoId(Long periodoAcademicoId, Long cursoId);
}
