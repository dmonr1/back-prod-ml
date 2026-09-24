package com.tp1.proyecto.evaluacion.entidad;

import com.tp1.proyecto.academico.entidad.DocenteCursoSeccion;
import com.tp1.proyecto.academico.entidad.HorarioSemanal;
import com.tp1.proyecto.academico.entidad.Matricula;
import com.tp1.proyecto.academico.entidad.PeriodoEvaluacion;
import com.tp1.proyecto.comun.entidad.AuditoriaEntidad;
import com.tp1.proyecto.evaluacion.enumeracion.EstadoAsistenciaSesion;
import com.tp1.proyecto.usuario.entidad.Usuario;
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
import java.time.LocalDate;

@Entity
@Table(name = "asistencias_sesion", schema = "db_tp1")
public class AsistenciaSesion extends AuditoriaEntidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "docente_curso_seccion_id", nullable = false)
    private DocenteCursoSeccion asignacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "horario_semanal_id")
    private HorarioSemanal horarioSemanal;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "periodo_evaluacion_id", nullable = false)
    private PeriodoEvaluacion periodoEvaluacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "matricula_id", nullable = false)
    private Matricula matricula;

    @Column(name = "fecha_clase", nullable = false)
    private LocalDate fechaClase;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_asistencia", nullable = false, length = 20)
    private EstadoAsistenciaSesion estadoAsistencia;

    @Column(name = "observacion", length = 255)
    private String observacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_registro_id", nullable = false)
    private Usuario usuarioRegistro;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public DocenteCursoSeccion getAsignacion() { return asignacion; }
    public void setAsignacion(DocenteCursoSeccion asignacion) { this.asignacion = asignacion; }
    public HorarioSemanal getHorarioSemanal() { return horarioSemanal; }
    public void setHorarioSemanal(HorarioSemanal horarioSemanal) { this.horarioSemanal = horarioSemanal; }
    public PeriodoEvaluacion getPeriodoEvaluacion() { return periodoEvaluacion; }
    public void setPeriodoEvaluacion(PeriodoEvaluacion periodoEvaluacion) { this.periodoEvaluacion = periodoEvaluacion; }
    public Matricula getMatricula() { return matricula; }
    public void setMatricula(Matricula matricula) { this.matricula = matricula; }
    public LocalDate getFechaClase() { return fechaClase; }
    public void setFechaClase(LocalDate fechaClase) { this.fechaClase = fechaClase; }
    public EstadoAsistenciaSesion getEstadoAsistencia() { return estadoAsistencia; }
    public void setEstadoAsistencia(EstadoAsistenciaSesion estadoAsistencia) { this.estadoAsistencia = estadoAsistencia; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
    public Usuario getUsuarioRegistro() { return usuarioRegistro; }
    public void setUsuarioRegistro(Usuario usuarioRegistro) { this.usuarioRegistro = usuarioRegistro; }
}
