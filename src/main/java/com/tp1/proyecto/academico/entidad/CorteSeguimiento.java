package com.tp1.proyecto.academico.entidad;

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
@Table(name = "cortes_seguimiento", schema = "db_tp1")
public class CorteSeguimiento extends AuditoriaEntidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "periodo_academico_id", nullable = false)
    private PeriodoAcademico periodoAcademico;

    @Column(name = "semana", nullable = false)
    private Integer semana;

    @Column(name = "fecha_corte", nullable = false)
    private LocalDate fechaCorte;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public PeriodoAcademico getPeriodoAcademico() { return periodoAcademico; }
    public void setPeriodoAcademico(PeriodoAcademico periodoAcademico) { this.periodoAcademico = periodoAcademico; }
    public Integer getSemana() { return semana; }
    public void setSemana(Integer semana) { this.semana = semana; }
    public LocalDate getFechaCorte() { return fechaCorte; }
    public void setFechaCorte(LocalDate fechaCorte) { this.fechaCorte = fechaCorte; }
}
