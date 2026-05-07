package com.tp1.proyecto.evaluacion.entidad;

import com.tp1.proyecto.academico.entidad.Bimestre;
import com.tp1.proyecto.academico.entidad.DocenteCursoSeccion;
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
import java.time.LocalDate;

@Entity
@Table(name = "evaluaciones", schema = "db_tp1")
public class Evaluacion extends AuditoriaEntidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "configuracion_evaluacion_id", nullable = false)
    private ConfiguracionEvaluacion configuracionEvaluacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "docente_curso_seccion_id", nullable = false)
    private DocenteCursoSeccion docenteCursoSeccion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bimestre_id", nullable = false)
    private Bimestre bimestre;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tipo_evaluacion_id", nullable = false)
    private TipoEvaluacion tipoEvaluacion;

    @Column(name = "numero_evaluacion", nullable = false)
    private Integer numeroEvaluacion;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Column(name = "fecha_evaluacion")
    private LocalDate fechaEvaluacion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ConfiguracionEvaluacion getConfiguracionEvaluacion() {
        return configuracionEvaluacion;
    }

    public void setConfiguracionEvaluacion(ConfiguracionEvaluacion configuracionEvaluacion) {
        this.configuracionEvaluacion = configuracionEvaluacion;
    }

    public DocenteCursoSeccion getDocenteCursoSeccion() {
        return docenteCursoSeccion;
    }

    public void setDocenteCursoSeccion(DocenteCursoSeccion docenteCursoSeccion) {
        this.docenteCursoSeccion = docenteCursoSeccion;
    }

    public Bimestre getBimestre() {
        return bimestre;
    }

    public void setBimestre(Bimestre bimestre) {
        this.bimestre = bimestre;
    }

    public TipoEvaluacion getTipoEvaluacion() {
        return tipoEvaluacion;
    }

    public void setTipoEvaluacion(TipoEvaluacion tipoEvaluacion) {
        this.tipoEvaluacion = tipoEvaluacion;
    }

    public Integer getNumeroEvaluacion() {
        return numeroEvaluacion;
    }

    public void setNumeroEvaluacion(Integer numeroEvaluacion) {
        this.numeroEvaluacion = numeroEvaluacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDate getFechaEvaluacion() {
        return fechaEvaluacion;
    }

    public void setFechaEvaluacion(LocalDate fechaEvaluacion) {
        this.fechaEvaluacion = fechaEvaluacion;
    }
}
