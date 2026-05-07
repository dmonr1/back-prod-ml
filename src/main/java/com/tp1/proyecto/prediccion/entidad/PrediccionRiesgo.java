package com.tp1.proyecto.prediccion.entidad;

import com.tp1.proyecto.academico.entidad.Bimestre;
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
import java.time.LocalDateTime;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "predicciones_riesgo_global", schema = "db_tp1")
public class PrediccionRiesgo extends AuditoriaEntidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "matricula_id", nullable = false)
    private Matricula matricula;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bimestre_id", nullable = false)
    private Bimestre bimestre;

    @Column(name = "carga_archivo_id")
    private Long cargaArchivoId;

    @Column(name = "puntaje_riesgo", nullable = false, precision = 5, scale = 2)
    private BigDecimal puntajeRiesgo;

    @Column(name = "nivel_riesgo", nullable = false, length = 20)
    private String nivelRiesgo;

    @Column(name = "modelo_version", length = 50)
    private String modeloVersion;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "variables_entrada", columnDefinition = "jsonb")
    private String variablesEntrada;

    @Column(name = "fecha_prediccion", nullable = false)
    private LocalDateTime fechaPrediccion;

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

    public Bimestre getBimestre() {
        return bimestre;
    }

    public void setBimestre(Bimestre bimestre) {
        this.bimestre = bimestre;
    }

    public Long getCargaArchivoId() {
        return cargaArchivoId;
    }

    public void setCargaArchivoId(Long cargaArchivoId) {
        this.cargaArchivoId = cargaArchivoId;
    }

    public BigDecimal getPuntajeRiesgo() {
        return puntajeRiesgo;
    }

    public void setPuntajeRiesgo(BigDecimal puntajeRiesgo) {
        this.puntajeRiesgo = puntajeRiesgo;
    }

    public String getNivelRiesgo() {
        return nivelRiesgo;
    }

    public void setNivelRiesgo(String nivelRiesgo) {
        this.nivelRiesgo = nivelRiesgo;
    }

    public String getModeloVersion() {
        return modeloVersion;
    }

    public void setModeloVersion(String modeloVersion) {
        this.modeloVersion = modeloVersion;
    }

    public String getVariablesEntrada() {
        return variablesEntrada;
    }

    public void setVariablesEntrada(String variablesEntrada) {
        this.variablesEntrada = variablesEntrada;
    }

    public LocalDateTime getFechaPrediccion() {
        return fechaPrediccion;
    }

    public void setFechaPrediccion(LocalDateTime fechaPrediccion) {
        this.fechaPrediccion = fechaPrediccion;
    }
}
