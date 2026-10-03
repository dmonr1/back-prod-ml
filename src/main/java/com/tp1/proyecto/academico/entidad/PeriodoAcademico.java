package com.tp1.proyecto.academico.entidad;

import com.tp1.proyecto.comun.entidad.AuditoriaEntidad;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "periodos_academicos", schema = "db_tp1")
public class PeriodoAcademico extends AuditoriaEntidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "anio", nullable = false)
    private Integer anio;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "tipo_periodo_evaluacion", nullable = false, length = 30)
    private String tipoPeriodoEvaluacion;

    @Column(name = "duracion_hora_primaria_minutos", nullable = false)
    private Integer duracionHoraPrimariaMinutos = 50;

    @Column(name = "duracion_recreo_primaria_minutos", nullable = false)
    private Integer duracionRecreoPrimariaMinutos = 20;

    @Column(name = "hora_inicio_jornada_primaria", nullable = false)
    private LocalTime horaInicioJornadaPrimaria = LocalTime.of(7, 0);

    @Column(name = "hora_fin_jornada_primaria", nullable = false)
    private LocalTime horaFinJornadaPrimaria = LocalTime.of(18, 0);

    @Column(name = "duracion_hora_secundaria_minutos", nullable = false)
    private Integer duracionHoraSecundariaMinutos = 90;

    @Column(name = "duracion_recreo_secundaria_minutos", nullable = false)
    private Integer duracionRecreoSecundariaMinutos = 20;

    @Column(name = "hora_inicio_jornada_secundaria", nullable = false)
    private LocalTime horaInicioJornadaSecundaria = LocalTime.of(7, 0);

    @Column(name = "hora_fin_jornada_secundaria", nullable = false)
    private LocalTime horaFinJornadaSecundaria = LocalTime.of(18, 0);

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getTipoPeriodoEvaluacion() {
        return tipoPeriodoEvaluacion;
    }

    public void setTipoPeriodoEvaluacion(String tipoPeriodoEvaluacion) {
        this.tipoPeriodoEvaluacion = tipoPeriodoEvaluacion;
    }

    public Integer getDuracionHoraPrimariaMinutos() { return duracionHoraPrimariaMinutos; }
    public void setDuracionHoraPrimariaMinutos(Integer value) { this.duracionHoraPrimariaMinutos = value; }
    public Integer getDuracionRecreoPrimariaMinutos() { return duracionRecreoPrimariaMinutos; }
    public void setDuracionRecreoPrimariaMinutos(Integer value) { this.duracionRecreoPrimariaMinutos = value; }
    public Integer getDuracionHoraSecundariaMinutos() { return duracionHoraSecundariaMinutos; }
    public void setDuracionHoraSecundariaMinutos(Integer value) { this.duracionHoraSecundariaMinutos = value; }
    public Integer getDuracionRecreoSecundariaMinutos() { return duracionRecreoSecundariaMinutos; }
    public void setDuracionRecreoSecundariaMinutos(Integer value) { this.duracionRecreoSecundariaMinutos = value; }
    public LocalTime getHoraInicioJornadaPrimaria() { return horaInicioJornadaPrimaria; }
    public void setHoraInicioJornadaPrimaria(LocalTime value) { this.horaInicioJornadaPrimaria = value; }
    public LocalTime getHoraFinJornadaPrimaria() { return horaFinJornadaPrimaria; }
    public void setHoraFinJornadaPrimaria(LocalTime value) { this.horaFinJornadaPrimaria = value; }
    public LocalTime getHoraInicioJornadaSecundaria() { return horaInicioJornadaSecundaria; }
    public void setHoraInicioJornadaSecundaria(LocalTime value) { this.horaInicioJornadaSecundaria = value; }
    public LocalTime getHoraFinJornadaSecundaria() { return horaFinJornadaSecundaria; }
    public void setHoraFinJornadaSecundaria(LocalTime value) { this.horaFinJornadaSecundaria = value; }
}
