package com.tp1.proyecto.evaluacion.repositorio;

import com.tp1.proyecto.evaluacion.entidad.Evaluacion;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvaluacionRepositorio extends JpaRepository<Evaluacion, Long> {

    @EntityGraph(attributePaths = {"docenteCursoSeccion.docente.usuario", "docenteCursoSeccion.curso",
        "docenteCursoSeccion.seccion.grado", "docenteCursoSeccion.periodoAcademico", "periodoEvaluacion", "tipoEvaluacion"})
    List<Evaluacion> findByDocenteCursoSeccionEstadoAndEstadoAndFechaEvaluacionIsNotNull(
        com.tp1.proyecto.comun.enumeracion.EstadoRegistro estadoAsignacion,
        com.tp1.proyecto.comun.enumeracion.EstadoRegistro estadoEvaluacion
    );

    List<Evaluacion> findByDocenteCursoSeccionSeccionIdAndDocenteCursoSeccionPeriodoAcademicoIdAndEstadoOrderByPeriodoEvaluacionNumeroAscTipoEvaluacionOrdenAscNumeroEvaluacionAsc(
        Long seccionId,
        Long periodoAcademicoId,
        com.tp1.proyecto.comun.enumeracion.EstadoRegistro estado
    );

    List<Evaluacion> findByDocenteCursoSeccionSeccionIdAndDocenteCursoSeccionPeriodoAcademicoIdAndDocenteCursoSeccionEstadoAndEstadoOrderByPeriodoEvaluacionNumeroAscTipoEvaluacionOrdenAscNumeroEvaluacionAsc(
        Long seccionId,
        Long periodoAcademicoId,
        com.tp1.proyecto.comun.enumeracion.EstadoRegistro estadoAsignacion,
        com.tp1.proyecto.comun.enumeracion.EstadoRegistro estadoEvaluacion
    );

    @EntityGraph(attributePaths = {"docenteCursoSeccion.curso", "periodoEvaluacion", "tipoEvaluacion"})
    List<Evaluacion> findByDocenteCursoSeccionSeccionIdAndDocenteCursoSeccionPeriodoAcademicoIdAndFechaEvaluacionIsNullAndEstadoOrderByPeriodoEvaluacionNumeroAscTipoEvaluacionOrdenAscNumeroEvaluacionAsc(
        Long seccionId,
        Long periodoAcademicoId,
        com.tp1.proyecto.comun.enumeracion.EstadoRegistro estado
    );

    @EntityGraph(attributePaths = {"docenteCursoSeccion.curso", "periodoEvaluacion", "tipoEvaluacion"})
    List<Evaluacion> findByDocenteCursoSeccionSeccionIdAndDocenteCursoSeccionPeriodoAcademicoIdAndPeriodoEvaluacionIdAndFechaEvaluacionIsNullAndEstadoOrderByTipoEvaluacionOrdenAscNumeroEvaluacionAsc(
        Long seccionId,
        Long periodoAcademicoId,
        Long periodoEvaluacionId,
        com.tp1.proyecto.comun.enumeracion.EstadoRegistro estado
    );

    @EntityGraph(attributePaths = {"docenteCursoSeccion.curso", "periodoEvaluacion", "tipoEvaluacion"})
    List<Evaluacion> findByDocenteCursoSeccionSeccionIdAndDocenteCursoSeccionPeriodoAcademicoIdAndDocenteCursoSeccionEstadoAndPeriodoEvaluacionIdAndFechaEvaluacionIsNullAndEstadoOrderByTipoEvaluacionOrdenAscNumeroEvaluacionAsc(
        Long seccionId,
        Long periodoAcademicoId,
        com.tp1.proyecto.comun.enumeracion.EstadoRegistro estadoAsignacion,
        Long periodoEvaluacionId,
        com.tp1.proyecto.comun.enumeracion.EstadoRegistro estadoEvaluacion
    );

    List<Evaluacion> findByDocenteCursoSeccionId(Long docenteCursoSeccionId);

    List<Evaluacion> findByDocenteCursoSeccionIdAndPeriodoEvaluacionIdOrderByTipoEvaluacionOrdenAscNumeroEvaluacionAsc(
        Long docenteCursoSeccionId,
        Long periodoEvaluacionId
    );

    List<Evaluacion> findByDocenteCursoSeccionIdAndPeriodoEvaluacionIdAndEstadoOrderByTipoEvaluacionOrdenAscNumeroEvaluacionAsc(
        Long docenteCursoSeccionId,
        Long periodoEvaluacionId,
        com.tp1.proyecto.comun.enumeracion.EstadoRegistro estado
    );

    List<Evaluacion> findByDocenteCursoSeccionIdAndPeriodoEvaluacionIdAndTipoEvaluacionIdOrderByNumeroEvaluacionAsc(
        Long docenteCursoSeccionId,
        Long periodoEvaluacionId,
        Long tipoEvaluacionId
    );

    List<Evaluacion> findByDocenteCursoSeccionIdAndPeriodoEvaluacionIdAndTipoEvaluacionIdAndEstadoOrderByNumeroEvaluacionAsc(
        Long docenteCursoSeccionId,
        Long periodoEvaluacionId,
        Long tipoEvaluacionId,
        com.tp1.proyecto.comun.enumeracion.EstadoRegistro estado
    );

    List<Evaluacion> findByDocenteCursoSeccionCursoIdAndDocenteCursoSeccionSeccionIdAndPeriodoEvaluacionIdAndEstadoOrderByTipoEvaluacionOrdenAscNumeroEvaluacionAsc(
        Long cursoId,
        Long seccionId,
        Long periodoEvaluacionId,
        com.tp1.proyecto.comun.enumeracion.EstadoRegistro estado
    );

    boolean existsByDocenteCursoSeccionIdAndPeriodoEvaluacionIdAndTipoEvaluacionIdAndNumeroEvaluacion(
        Long docenteCursoSeccionId,
        Long periodoEvaluacionId,
        Long tipoEvaluacionId,
        Integer numeroEvaluacion
    );
}
