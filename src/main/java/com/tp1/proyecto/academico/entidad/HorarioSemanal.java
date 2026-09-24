package com.tp1.proyecto.academico.entidad;

import com.tp1.proyecto.comun.entidad.AuditoriaEntidad;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "horarios_semanales", schema = "db_tp1")
public class HorarioSemanal extends AuditoriaEntidad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "docente_curso_seccion_id", nullable = false)
    private DocenteCursoSeccion asignacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bloque_horario_id", nullable = false)
    private BloqueHorario bloque;

    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false, length = 15)
    private DiaSemana diaSemana;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public DocenteCursoSeccion getAsignacion() { return asignacion; }
    public void setAsignacion(DocenteCursoSeccion asignacion) { this.asignacion = asignacion; }
    public BloqueHorario getBloque() { return bloque; }
    public void setBloque(BloqueHorario bloque) { this.bloque = bloque; }
    public DiaSemana getDiaSemana() { return diaSemana; }
    public void setDiaSemana(DiaSemana diaSemana) { this.diaSemana = diaSemana; }
}
