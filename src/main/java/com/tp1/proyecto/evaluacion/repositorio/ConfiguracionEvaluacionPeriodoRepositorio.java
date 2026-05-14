package com.tp1.proyecto.evaluacion.repositorio;

import com.tp1.proyecto.evaluacion.entidad.ConfiguracionEvaluacionPeriodo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfiguracionEvaluacionPeriodoRepositorio extends JpaRepository<ConfiguracionEvaluacionPeriodo, Long> {

    List<ConfiguracionEvaluacionPeriodo> findByPeriodoAcademicoIdOrderByTipoEvaluacionOrdenAsc(Long periodoAcademicoId);
}
