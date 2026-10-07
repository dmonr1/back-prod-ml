package com.tp1.proyecto.academico.entidad;

import com.tp1.proyecto.comun.entidad.AuditoriaEntidad;
import com.tp1.proyecto.docente.entidad.Docente;
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
@Table(name = "tutorias", schema = "db_tp1")
public class Tutoria extends AuditoriaEntidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "docente_id", nullable = false)
    private Docente docente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seccion_id", nullable = false)
    private Seccion seccion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "periodo_academico_id", nullable = false)
    private PeriodoAcademico periodoAcademico;

    @Column(name = "docente_nombre_historico", insertable = false, updatable = false)
    private String docenteNombreHistorico;

    @Column(name = "seccion_nombre_historico", insertable = false, updatable = false)
    private String seccionNombreHistorico;

    @Column(name = "grado_nombre_historico", insertable = false, updatable = false)
    private String gradoNombreHistorico;

    @Column(name = "nivel_nombre_historico", insertable = false, updatable = false)
    private String nivelNombreHistorico;

    @Column(name = "periodo_nombre_historico", insertable = false, updatable = false)
    private String periodoNombreHistorico;

    public String getDocenteNombreHistorico() { return docenteNombreHistorico; }
    public String getSeccionNombreHistorico() { return seccionNombreHistorico; }
    public String getGradoNombreHistorico() { return gradoNombreHistorico; }
    public String getNivelNombreHistorico() { return nivelNombreHistorico; }
    public String getPeriodoNombreHistorico() { return periodoNombreHistorico; }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Docente getDocente() {
        return docente;
    }

    public void setDocente(Docente docente) {
        this.docente = docente;
    }

    public Seccion getSeccion() {
        return seccion;
    }

    public void setSeccion(Seccion seccion) {
        this.seccion = seccion;
    }

    public PeriodoAcademico getPeriodoAcademico() {
        return periodoAcademico;
    }

    public void setPeriodoAcademico(PeriodoAcademico periodoAcademico) {
        this.periodoAcademico = periodoAcademico;
    }
}
