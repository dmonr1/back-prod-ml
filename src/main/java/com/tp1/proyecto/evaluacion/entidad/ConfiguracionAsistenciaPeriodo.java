package com.tp1.proyecto.evaluacion.entidad;

import com.tp1.proyecto.academico.entidad.DocenteCursoSeccion;
import com.tp1.proyecto.academico.entidad.PeriodoEvaluacion;
import com.tp1.proyecto.comun.entidad.AuditoriaEntidad;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

@Entity
@Table(name = "configuraciones_asistencia_periodo", schema = "db_tp1")
public class ConfiguracionAsistenciaPeriodo extends AuditoriaEntidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "docente_curso_seccion_id", nullable = false)
    private DocenteCursoSeccion docenteCursoSeccion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "periodo_evaluacion_id", nullable = false)
    private PeriodoEvaluacion periodoEvaluacion;

    @Column(name = "clases_programadas", nullable = false)
    private Integer clasesProgramadas;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DocenteCursoSeccion getDocenteCursoSeccion() {
        return docenteCursoSeccion;
    }

    public void setDocenteCursoSeccion(DocenteCursoSeccion docenteCursoSeccion) {
        this.docenteCursoSeccion = docenteCursoSeccion;
    }

    public PeriodoEvaluacion getPeriodoEvaluacion() {
        return periodoEvaluacion;
    }

    public void setPeriodoEvaluacion(PeriodoEvaluacion periodoEvaluacion) {
        this.periodoEvaluacion = periodoEvaluacion;
    }

    public Integer getClasesProgramadas() {
        return clasesProgramadas;
    }

    public void setClasesProgramadas(Integer clasesProgramadas) {
        this.clasesProgramadas = clasesProgramadas;
    }
}
