package com.tp1.proyecto.academico.repositorio;

import com.tp1.proyecto.academico.entidad.Tutoria;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TutoriaRepositorio extends JpaRepository<Tutoria, Long> {

    Optional<Tutoria> findByDocenteIdAndPeriodoAcademicoId(Long docenteId, Long periodoAcademicoId);

    List<Tutoria> findByPeriodoAcademicoId(Long periodoAcademicoId);

    List<Tutoria> findBySeccionIdAndPeriodoAcademicoId(Long seccionId, Long periodoAcademicoId);

    boolean existsByDocenteId(Long docenteId);

    boolean existsByDocenteIdAndPeriodoAcademicoId(Long docenteId, Long periodoAcademicoId);

    boolean existsBySeccionIdAndPeriodoAcademicoId(Long seccionId, Long periodoAcademicoId);
}
