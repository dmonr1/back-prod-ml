package com.tp1.proyecto.alerta.repositorio;

import com.tp1.proyecto.alerta.entidad.Alerta;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AlertaRepositorio extends JpaRepository<Alerta, Long> {

    List<Alerta> findByAtendidaFalse();

    List<Alerta> findByMatriculaId(Long matriculaId);

    void deleteByPrediccionGlobalId(Long prediccionGlobalId);

    void deleteByPrediccionCursoId(Long prediccionCursoId);

    @Query("""
        SELECT a
        FROM Alerta a
        WHERE (
            a.prediccionGlobal IS NOT NULL
            AND a.prediccionGlobal.bimestre.id = :bimestreId
            AND a.matricula.seccion.id = :seccionId
        ) OR (
            a.prediccionCurso IS NOT NULL
            AND a.prediccionCurso.bimestre.id = :bimestreId
            AND a.matricula.seccion.id = :seccionId
        )
        ORDER BY a.fechaRegistro DESC
        """)
    List<Alerta> findByBimestreIdAndSeccionId(@Param("bimestreId") Long bimestreId, @Param("seccionId") Long seccionId);
}
