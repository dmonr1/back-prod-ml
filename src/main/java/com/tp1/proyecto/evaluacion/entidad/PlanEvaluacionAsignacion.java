package com.tp1.proyecto.evaluacion.entidad;

import com.tp1.proyecto.academico.entidad.DocenteCursoSeccion;
import com.tp1.proyecto.academico.entidad.PeriodoEvaluacion;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "planes_evaluacion_asignacion", schema = "db_tp1")
public class PlanEvaluacionAsignacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "docente_curso_seccion_id", nullable = false)
    private DocenteCursoSeccion docenteCursoSeccion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "periodo_evaluacion_id", nullable = false)
    private PeriodoEvaluacion periodoEvaluacion;

    public void setDocenteCursoSeccion(DocenteCursoSeccion docenteCursoSeccion) {
        this.docenteCursoSeccion = docenteCursoSeccion;
    }

    public void setPeriodoEvaluacion(PeriodoEvaluacion periodoEvaluacion) {
        this.periodoEvaluacion = periodoEvaluacion;
    }
}
