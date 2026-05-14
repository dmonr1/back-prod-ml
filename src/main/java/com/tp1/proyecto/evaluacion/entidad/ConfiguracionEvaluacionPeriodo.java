package com.tp1.proyecto.evaluacion.entidad;

import com.tp1.proyecto.academico.entidad.PeriodoAcademico;
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
@Table(name = "configuraciones_evaluacion_periodo", schema = "db_tp1")
public class ConfiguracionEvaluacionPeriodo extends AuditoriaEntidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "periodo_academico_id", nullable = false)
    private PeriodoAcademico periodoAcademico;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tipo_evaluacion_id", nullable = false)
    private TipoEvaluacion tipoEvaluacion;

    @Column(name = "cantidad_evaluaciones", nullable = false)
    private Integer cantidadEvaluaciones;

    @Column(name = "calcular_en_promedio", nullable = false)
    private Boolean calcularEnPromedio;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PeriodoAcademico getPeriodoAcademico() {
        return periodoAcademico;
    }

    public void setPeriodoAcademico(PeriodoAcademico periodoAcademico) {
        this.periodoAcademico = periodoAcademico;
    }

    public TipoEvaluacion getTipoEvaluacion() {
        return tipoEvaluacion;
    }

    public void setTipoEvaluacion(TipoEvaluacion tipoEvaluacion) {
        this.tipoEvaluacion = tipoEvaluacion;
    }

    public Integer getCantidadEvaluaciones() {
        return cantidadEvaluaciones;
    }

    public void setCantidadEvaluaciones(Integer cantidadEvaluaciones) {
        this.cantidadEvaluaciones = cantidadEvaluaciones;
    }

    public Boolean getCalcularEnPromedio() {
        return calcularEnPromedio;
    }

    public void setCalcularEnPromedio(Boolean calcularEnPromedio) {
        this.calcularEnPromedio = calcularEnPromedio;
    }
}
