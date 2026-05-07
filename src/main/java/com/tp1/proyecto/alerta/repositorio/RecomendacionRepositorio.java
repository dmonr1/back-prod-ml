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

    @Query("""
        SELECT r
        FROM Recomendacion r
        WHERE (
            r.prediccionGlobal IS NOT NULL
            AND r.prediccionGlobal.bimestre.id = :bimestreId
            AND r.matricula.seccion.id = :seccionId
        ) OR (
            r.prediccionCurso IS NOT NULL
            AND r.prediccionCurso.bimestre.id = :bimestreId
            AND r.matricula.seccion.id = :seccionId
        )
        ORDER BY r.fechaRegistro DESC
        """)
    List<Recomendacion> findByBimestreIdAndSeccionId(@Param("bimestreId") Long bimestreId, @Param("seccionId") Long seccionId);
}
