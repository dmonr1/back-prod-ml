package com.tp1.proyecto.alerta.repositorio;

import com.tp1.proyecto.alerta.entidad.Recomendacion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecomendacionRepositorio extends JpaRepository<Recomendacion, Long> {

    List<Recomendacion> findByMatriculaId(Long matriculaId);

    void deleteByPrediccionGlobalId(Long prediccionGlobalId);

    void deleteByPrediccionCursoId(Long prediccionCursoId);

    @Query(
        value =
            "SELECT r.* " +
            "FROM db_tp1.recomendaciones r " +
            "LEFT JOIN db_tp1.predicciones_riesgo_global pg ON pg.id = r.prediccion_global_id " +
            "LEFT JOIN db_tp1.predicciones_riesgo_curso pc ON pc.id = r.prediccion_curso_id " +
            "JOIN db_tp1.matriculas m ON m.id = r.matricula_id " +
            "WHERE ( " +
            "pg.id IS NOT NULL " +
            "AND pg.periodo_evaluacion_id = :periodoEvaluacionId " +
            "AND m.seccion_id = :seccionId " +
            ") OR ( " +
            "pc.id IS NOT NULL " +
            "AND pc.periodo_evaluacion_id = :periodoEvaluacionId " +
            "AND m.seccion_id = :seccionId " +
            ") " +
            "ORDER BY r.fecha_registro DESC",
        nativeQuery = true
    )
    List<Recomendacion> findByPeriodoEvaluacionIdAndSeccionId(@Param("periodoEvaluacionId") Long periodoEvaluacionId, @Param("seccionId") Long seccionId);
}
