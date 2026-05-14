package com.tp1.proyecto.evaluacion.repositorio;

import com.tp1.proyecto.evaluacion.entidad.ConfiguracionEvaluacionCurso;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfiguracionEvaluacionCursoRepositorio extends JpaRepository<ConfiguracionEvaluacionCurso, Long> {

    List<ConfiguracionEvaluacionCurso> findByPeriodoAcademicoId(Long periodoAcademicoId);

    List<ConfiguracionEvaluacionCurso> findByPeriodoAcademicoIdAndCursoIdOrderByTipoEvaluacionOrdenAsc(
        Long periodoAcademicoId,
        Long cursoId
    );

    List<ConfiguracionEvaluacionCurso> findByPeriodoAcademicoIdAndCursoId(Long periodoAcademicoId, Long cursoId);
}
