package com.tp1.proyecto.evaluacion.repositorio;

import com.tp1.proyecto.evaluacion.entidad.TipoEvaluacion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoEvaluacionRepositorio extends JpaRepository<TipoEvaluacion, Long> {

    List<TipoEvaluacion> findAllByOrderByOrdenAscNombreAsc();
}
