package com.tp1.proyecto.alerta.entidad;

import com.tp1.proyecto.academico.entidad.Curso;
import com.tp1.proyecto.academico.entidad.Matricula;
import com.tp1.proyecto.comun.entidad.AuditoriaEntidad;
import com.tp1.proyecto.prediccion.entidad.PrediccionRiesgo;
import com.tp1.proyecto.prediccion.entidad.PrediccionRiesgoCurso;
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
@Table(name = "recomendaciones", schema = "db_tp1")
public class Recomendacion extends AuditoriaEntidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "matricula_id", nullable = false)
    private Matricula matricula;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prediccion_global_id")
    private PrediccionRiesgo prediccionGlobal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prediccion_curso_id")
    private PrediccionRiesgoCurso prediccionCurso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id")
    private Curso curso;

    @Column(name = "titulo", nullable = false, length = 150)
    private String titulo;

    @Column(name = "descripcion", nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "fuente", nullable = false, length = 50)
    private String fuente;

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

    public PrediccionRiesgo getPrediccionGlobal() {
        return prediccionGlobal;
    }

    public void setPrediccionGlobal(PrediccionRiesgo prediccionGlobal) {
        this.prediccionGlobal = prediccionGlobal;
    }

    public PrediccionRiesgoCurso getPrediccionCurso() {
        return prediccionCurso;
    }

    public void setPrediccionCurso(PrediccionRiesgoCurso prediccionCurso) {
        this.prediccionCurso = prediccionCurso;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFuente() {
        return fuente;
    }

    public void setFuente(String fuente) {
        this.fuente = fuente;
    }
}
