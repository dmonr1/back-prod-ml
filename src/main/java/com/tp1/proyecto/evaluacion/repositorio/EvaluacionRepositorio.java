package com.tp1.proyecto.evaluacion.repositorio;

import com.tp1.proyecto.evaluacion.entidad.Evaluacion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvaluacionRepositorio extends JpaRepository<Evaluacion, Long> {

    List<Evaluacion> findByDocenteCursoSeccionIdAndBimestreIdOrderByTipoEvaluacionOrdenAscNumeroEvaluacionAsc(
        Long docenteCursoSeccionId,
        Long bimestreId
    );
}
