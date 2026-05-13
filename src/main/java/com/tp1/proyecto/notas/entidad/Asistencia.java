package com.tp1.proyecto.notas.entidad;

import com.tp1.proyecto.academico.entidad.PeriodoEvaluacion;
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

@Entity
@Table(name = "asistencias", schema = "db_tp1")
public class Asistencia extends AuditoriaEntidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "matricula_id", nullable = false)
    private Matricula matricula;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "periodo_evaluacion_id", nullable = false)
    private PeriodoEvaluacion periodoEvaluacion;

    @Column(name = "clases_programadas", nullable = false)
    private Integer clasesProgramadas;

    @Column(name = "clases_asistidas", nullable = false)
    private Integer clasesAsistidas;

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

    public Integer getClasesAsistidas() {
        return clasesAsistidas;
    }

    public void setClasesAsistidas(Integer clasesAsistidas) {
        this.clasesAsistidas = clasesAsistidas;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}
