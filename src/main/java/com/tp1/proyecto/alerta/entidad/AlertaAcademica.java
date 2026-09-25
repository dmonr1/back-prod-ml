package com.tp1.proyecto.alerta.entidad;

import com.tp1.proyecto.academico.entidad.DocenteCursoSeccion;
import com.tp1.proyecto.academico.entidad.HorarioSemanal;
import com.tp1.proyecto.comun.entidad.AuditoriaEntidad;
import com.tp1.proyecto.evaluacion.entidad.Evaluacion;
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
import java.time.LocalDateTime;

@Entity
@Table(name = "alertas_academicas", schema = "db_tp1")
public class AlertaAcademica extends AuditoriaEntidad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "clave_origen", nullable = false, unique = true, length = 120)
    private String claveOrigen;

    @Column(name = "tipo", nullable = false, length = 30)
    private String tipo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "docente_curso_seccion_id", nullable = false)
    private DocenteCursoSeccion asignacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "horario_semanal_id")
    private HorarioSemanal horario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluacion_id")
    private Evaluacion evaluacion;

    @Column(name = "fecha_referencia", nullable = false)
    private LocalDate fechaReferencia;

    @Column(name = "fecha_limite", nullable = false)
    private LocalDateTime fechaLimite;

    @Column(name = "cantidad_pendiente", nullable = false)
    private Integer cantidadPendiente = 0;

    @Column(name = "estado_alerta", nullable = false, length = 20)
    private String estadoAlerta = "PENDIENTE";

    public Long getId() { return id; }
    public String getClaveOrigen() { return claveOrigen; }
    public void setClaveOrigen(String claveOrigen) { this.claveOrigen = claveOrigen; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public DocenteCursoSeccion getAsignacion() { return asignacion; }
    public void setAsignacion(DocenteCursoSeccion asignacion) { this.asignacion = asignacion; }
    public HorarioSemanal getHorario() { return horario; }
    public void setHorario(HorarioSemanal horario) { this.horario = horario; }
    public Evaluacion getEvaluacion() { return evaluacion; }
    public void setEvaluacion(Evaluacion evaluacion) { this.evaluacion = evaluacion; }
    public LocalDate getFechaReferencia() { return fechaReferencia; }
    public void setFechaReferencia(LocalDate fechaReferencia) { this.fechaReferencia = fechaReferencia; }
    public LocalDateTime getFechaLimite() { return fechaLimite; }
    public void setFechaLimite(LocalDateTime fechaLimite) { this.fechaLimite = fechaLimite; }
    public Integer getCantidadPendiente() { return cantidadPendiente; }
    public void setCantidadPendiente(Integer cantidadPendiente) { this.cantidadPendiente = cantidadPendiente; }
    public String getEstadoAlerta() { return estadoAlerta; }
    public void setEstadoAlerta(String estadoAlerta) { this.estadoAlerta = estadoAlerta; }
}
