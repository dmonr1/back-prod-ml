package com.tp1.proyecto.evaluacion.repositorio;

import com.tp1.proyecto.evaluacion.entidad.DetalleNotaEvaluacion;
import java.util.List;
import java.util.Optional;
import com.tp1.proyecto.comun.enumeracion.EstadoRegistro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DetalleNotaEvaluacionRepositorio extends JpaRepository<DetalleNotaEvaluacion, Long> {

    List<DetalleNotaEvaluacion> findByEvaluacionId(Long evaluacionId);

    List<DetalleNotaEvaluacion> findByEvaluacionIdAndEstado(Long evaluacionId, EstadoRegistro estado);

    List<DetalleNotaEvaluacion> findByEvaluacionIdIn(List<Long> evaluacionIds);

    List<DetalleNotaEvaluacion> findByEvaluacionIdInAndEstado(
        List<Long> evaluacionIds, EstadoRegistro estado
    );

    Optional<DetalleNotaEvaluacion> findByEvaluacionIdAndMatriculaId(Long evaluacionId, Long matriculaId);

    List<DetalleNotaEvaluacion> findByEvaluacionIdInAndMatriculaIdAndEstado(List<Long> evaluacionIds, Long matriculaId, EstadoRegistro estado);

    boolean existsByEvaluacionDocenteCursoSeccionId(Long docenteCursoSeccionId);

    @Query("select case when count(detalle) > 0 then true else false end from DetalleNotaEvaluacion detalle "
        + "where detalle.evaluacion.docenteCursoSeccion.curso.id = :cursoId "
        + "and detalle.evaluacion.docenteCursoSeccion.seccion.id = :seccionId "
        + "and detalle.evaluacion.docenteCursoSeccion.periodoAcademico.id = :periodoAcademicoId "
        + "and detalle.estado = :estado")
    boolean existsNotasPorCursoSeccion(
        @Param("cursoId") Long cursoId,
        @Param("seccionId") Long seccionId,
        @Param("periodoAcademicoId") Long periodoAcademicoId,
        @Param("estado") EstadoRegistro estado
    );
}
