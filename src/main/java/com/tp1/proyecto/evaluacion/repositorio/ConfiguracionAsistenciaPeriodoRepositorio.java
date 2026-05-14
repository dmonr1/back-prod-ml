package com.tp1.proyecto.evaluacion.repositorio;

import com.tp1.proyecto.evaluacion.entidad.ConfiguracionAsistenciaPeriodo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfiguracionAsistenciaPeriodoRepositorio extends JpaRepository<ConfiguracionAsistenciaPeriodo, Long> {

    Optional<ConfiguracionAsistenciaPeriodo> findByDocenteCursoSeccionIdAndPeriodoEvaluacionId(
        Long docenteCursoSeccionId,
        Long periodoEvaluacionId
    );
}
