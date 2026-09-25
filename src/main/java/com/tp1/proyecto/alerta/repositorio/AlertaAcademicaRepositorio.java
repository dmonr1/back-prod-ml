package com.tp1.proyecto.alerta.repositorio;

import com.tp1.proyecto.alerta.entidad.AlertaAcademica;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertaAcademicaRepositorio extends JpaRepository<AlertaAcademica, Long> {
    Optional<AlertaAcademica> findByClaveOrigen(String claveOrigen);

    @EntityGraph(attributePaths = {
        "asignacion.docente.usuario", "asignacion.curso", "asignacion.seccion.grado",
        "horario.bloque", "evaluacion.periodoEvaluacion", "evaluacion.tipoEvaluacion"
    })
    List<AlertaAcademica> findAllByOrderByFechaLimiteDesc();

    @EntityGraph(attributePaths = {
        "asignacion.docente.usuario", "asignacion.curso", "asignacion.seccion.grado",
        "horario.bloque", "evaluacion.periodoEvaluacion", "evaluacion.tipoEvaluacion"
    })
    List<AlertaAcademica> findByEstadoAlertaOrderByFechaLimiteDesc(String estadoAlerta);
}
