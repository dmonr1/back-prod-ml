package com.tp1.proyecto.academico.repositorio;

import com.tp1.proyecto.academico.entidad.DocenteCursoSeccion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocenteCursoSeccionRepositorio extends JpaRepository<DocenteCursoSeccion, Long> {

    List<DocenteCursoSeccion> findByDocenteIdAndPeriodoAcademicoId(Long docenteId, Long periodoAcademicoId);

    List<DocenteCursoSeccion> findByPeriodoAcademicoId(Long periodoAcademicoId);

    List<DocenteCursoSeccion> findBySeccionIdAndPeriodoAcademicoId(Long seccionId, Long periodoAcademicoId);

    boolean existsByDocenteIdAndCursoIdAndSeccionIdAndPeriodoAcademicoId(
        Long docenteId,
        Long cursoId,
        Long seccionId,
        Long periodoAcademicoId
    );
}
