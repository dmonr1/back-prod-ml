package com.tp1.proyecto.evaluacion.repositorio;

import com.tp1.proyecto.evaluacion.entidad.ConfiguracionEvaluacion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfiguracionEvaluacionRepositorio extends JpaRepository<ConfiguracionEvaluacion, Long> {

    List<ConfiguracionEvaluacion> findByPeriodoEvaluacionIdAndCursoIdOrderByTipoEvaluacionOrdenAsc(Long periodoEvaluacionId, Long cursoId);

    List<ConfiguracionEvaluacion> findByPeriodoAcademicoIdAndCursoIdOrderByPeriodoEvaluacionNumeroAscTipoEvaluacionOrdenAsc(
        Long periodoAcademicoId,
        Long cursoId
    );

    List<ConfiguracionEvaluacion> findByPeriodoAcademicoIdAndCursoId(Long periodoAcademicoId, Long cursoId);
}
