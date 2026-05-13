package com.tp1.proyecto.academico.repositorio;

import com.tp1.proyecto.academico.entidad.PeriodoEvaluacion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PeriodoEvaluacionRepositorio extends JpaRepository<PeriodoEvaluacion, Long> {

    List<PeriodoEvaluacion> findByPeriodoAcademicoId(Long periodoAcademicoId);
}
