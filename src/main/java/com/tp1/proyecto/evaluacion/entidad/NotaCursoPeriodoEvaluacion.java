package com.tp1.proyecto.evaluacion.entidad;

import com.tp1.proyecto.academico.entidad.PeriodoEvaluacion;
import com.tp1.proyecto.academico.entidad.Curso;
import com.tp1.proyecto.academico.entidad.Matricula;
import com.tp1.proyecto.comun.entidad.AuditoriaEntidad;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "notas_curso_periodo_evaluacion", schema = "db_tp1")
public class NotaCursoPeriodoEvaluacion extends AuditoriaEntidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "matricula_id", nullable = false)
    private Matricula matricula;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "periodo_evaluacion_id", nullable = false)
    private PeriodoEvaluacion periodoEvaluacion;

    @Column(name = "promedio_curso", nullable = false, precision = 5, scale = 2)
    private BigDecimal promedioCurso;

    @Column(name = "cantidad_evaluaciones_registradas", nullable = false)
    private Integer cantidadEvaluacionesRegistradas;

    @Column(name = "observacion", length = 255)
    private String observacion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Matricula getMatricula() {
        return matricula;
    }

    public void setMatricula(Matricula matricula) {
        this.matricula = matricula;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public PeriodoEvaluacion getPeriodoEvaluacion() {
        return periodoEvaluacion;
    }

    public void setPeriodoEvaluacion(PeriodoEvaluacion periodoEvaluacion) {
        this.periodoEvaluacion = periodoEvaluacion;
    }

    public BigDecimal getPromedioCurso() {
        return promedioCurso;
    }

    public void setPromedioCurso(BigDecimal promedioCurso) {
        this.promedioCurso = promedioCurso;
    }

    public Integer getCantidadEvaluacionesRegistradas() {
        return cantidadEvaluacionesRegistradas;
    }

    public void setCantidadEvaluacionesRegistradas(Integer cantidadEvaluacionesRegistradas) {
        this.cantidadEvaluacionesRegistradas = cantidadEvaluacionesRegistradas;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}
