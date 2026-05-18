package com.tp1.proyecto.academico.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TutoriaResumenAcademicoRespuestaDto {

    private Long tutoriaId;
    private Long docenteTutorId;
    private String docenteTutorNombreCompleto;
    private Long seccionId;
    private String seccion;
    private String grado;
    private String nivel;
    private Long periodoAcademicoId;
    private String periodoAcademico;
    private Integer anioAcademico;
    private Long periodoEvaluacionId;
    private String periodoEvaluacion;
    private LocalDate periodoEvaluacionFechaInicio;
    private LocalDate periodoEvaluacionFechaFin;
    private List<CursoTutoriaResumenDto> cursos = new ArrayList<>();
    private List<AlumnoTutoriaResumenDto> alumnos = new ArrayList<>();

    public Long getTutoriaId() {
        return tutoriaId;
    }

    public void setTutoriaId(Long tutoriaId) {
        this.tutoriaId = tutoriaId;
    }

    public Long getDocenteTutorId() {
        return docenteTutorId;
    }

    public void setDocenteTutorId(Long docenteTutorId) {
        this.docenteTutorId = docenteTutorId;
    }

    public String getDocenteTutorNombreCompleto() {
        return docenteTutorNombreCompleto;
    }

    public void setDocenteTutorNombreCompleto(String docenteTutorNombreCompleto) {
        this.docenteTutorNombreCompleto = docenteTutorNombreCompleto;
    }

    public Long getSeccionId() {
        return seccionId;
    }

    public void setSeccionId(Long seccionId) {
        this.seccionId = seccionId;
    }

    public String getSeccion() {
        return seccion;
    }

    public void setSeccion(String seccion) {
        this.seccion = seccion;
    }

    public String getGrado() {
        return grado;
    }

    public void setGrado(String grado) {
        this.grado = grado;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public Long getPeriodoAcademicoId() {
        return periodoAcademicoId;
    }

    public void setPeriodoAcademicoId(Long periodoAcademicoId) {
        this.periodoAcademicoId = periodoAcademicoId;
    }

    public String getPeriodoAcademico() {
        return periodoAcademico;
    }

    public void setPeriodoAcademico(String periodoAcademico) {
        this.periodoAcademico = periodoAcademico;
    }

    public Integer getAnioAcademico() {
        return anioAcademico;
    }

    public void setAnioAcademico(Integer anioAcademico) {
        this.anioAcademico = anioAcademico;
    }

    public Long getPeriodoEvaluacionId() {
        return periodoEvaluacionId;
    }

    public void setPeriodoEvaluacionId(Long periodoEvaluacionId) {
        this.periodoEvaluacionId = periodoEvaluacionId;
    }

    public String getPeriodoEvaluacion() {
        return periodoEvaluacion;
    }

    public void setPeriodoEvaluacion(String periodoEvaluacion) {
        this.periodoEvaluacion = periodoEvaluacion;
    }

    public LocalDate getPeriodoEvaluacionFechaInicio() {
        return periodoEvaluacionFechaInicio;
    }

    public void setPeriodoEvaluacionFechaInicio(LocalDate periodoEvaluacionFechaInicio) {
        this.periodoEvaluacionFechaInicio = periodoEvaluacionFechaInicio;
    }

    public LocalDate getPeriodoEvaluacionFechaFin() {
        return periodoEvaluacionFechaFin;
    }

    public void setPeriodoEvaluacionFechaFin(LocalDate periodoEvaluacionFechaFin) {
        this.periodoEvaluacionFechaFin = periodoEvaluacionFechaFin;
    }

    public List<CursoTutoriaResumenDto> getCursos() {
        return cursos;
    }

    public void setCursos(List<CursoTutoriaResumenDto> cursos) {
        this.cursos = cursos;
    }

    public List<AlumnoTutoriaResumenDto> getAlumnos() {
        return alumnos;
    }

    public void setAlumnos(List<AlumnoTutoriaResumenDto> alumnos) {
        this.alumnos = alumnos;
    }

    public static class CursoTutoriaResumenDto {

        private Long asignacionId;
        private Long cursoId;
        private String curso;
        private Long docenteId;
        private String docenteNombreCompleto;

        public Long getAsignacionId() {
            return asignacionId;
        }

        public void setAsignacionId(Long asignacionId) {
            this.asignacionId = asignacionId;
        }

        public Long getCursoId() {
            return cursoId;
        }

        public void setCursoId(Long cursoId) {
            this.cursoId = cursoId;
        }

        public String getCurso() {
            return curso;
        }

        public void setCurso(String curso) {
            this.curso = curso;
        }

        public Long getDocenteId() {
            return docenteId;
        }

        public void setDocenteId(Long docenteId) {
            this.docenteId = docenteId;
        }

        public String getDocenteNombreCompleto() {
            return docenteNombreCompleto;
        }

        public void setDocenteNombreCompleto(String docenteNombreCompleto) {
            this.docenteNombreCompleto = docenteNombreCompleto;
        }
    }

    public static class AlumnoTutoriaResumenDto {

        private Long matriculaId;
        private Long alumnoId;
        private String codigoAlumno;
        private String alumnoNombreCompleto;
        private Integer clasesProgramadas;
        private Integer clasesAsistidas;
        private BigDecimal porcentajeAsistencia;
        private BigDecimal promedioGeneral;
        private List<CursoAlumnoTutoriaResumenDto> cursos = new ArrayList<>();

        public Long getMatriculaId() {
            return matriculaId;
        }

        public void setMatriculaId(Long matriculaId) {
            this.matriculaId = matriculaId;
        }

        public Long getAlumnoId() {
            return alumnoId;
        }

        public void setAlumnoId(Long alumnoId) {
            this.alumnoId = alumnoId;
        }

        public String getCodigoAlumno() {
            return codigoAlumno;
        }

        public void setCodigoAlumno(String codigoAlumno) {
            this.codigoAlumno = codigoAlumno;
        }

        public String getAlumnoNombreCompleto() {
            return alumnoNombreCompleto;
        }

        public void setAlumnoNombreCompleto(String alumnoNombreCompleto) {
            this.alumnoNombreCompleto = alumnoNombreCompleto;
        }

        public Integer getClasesProgramadas() {
            return clasesProgramadas;
        }

        public void setClasesProgramadas(Integer clasesProgramadas) {
            this.clasesProgramadas = clasesProgramadas;
        }

        public Integer getClasesAsistidas() {
            return clasesAsistidas;
        }

        public void setClasesAsistidas(Integer clasesAsistidas) {
            this.clasesAsistidas = clasesAsistidas;
        }

        public BigDecimal getPorcentajeAsistencia() {
            return porcentajeAsistencia;
        }

        public void setPorcentajeAsistencia(BigDecimal porcentajeAsistencia) {
            this.porcentajeAsistencia = porcentajeAsistencia;
        }

        public BigDecimal getPromedioGeneral() {
            return promedioGeneral;
        }

        public void setPromedioGeneral(BigDecimal promedioGeneral) {
            this.promedioGeneral = promedioGeneral;
        }

        public List<CursoAlumnoTutoriaResumenDto> getCursos() {
            return cursos;
        }

        public void setCursos(List<CursoAlumnoTutoriaResumenDto> cursos) {
            this.cursos = cursos;
        }
    }

    public static class CursoAlumnoTutoriaResumenDto {

        private Long asignacionId;
        private Long cursoId;
        private String curso;
        private Long docenteId;
        private String docenteNombreCompleto;
        private Integer evaluacionesRegistradas;
        private BigDecimal promedio;
        private List<BigDecimal> notas = new ArrayList<>();

        public Long getAsignacionId() {
            return asignacionId;
        }

        public void setAsignacionId(Long asignacionId) {
            this.asignacionId = asignacionId;
        }

        public Long getCursoId() {
            return cursoId;
        }

        public void setCursoId(Long cursoId) {
            this.cursoId = cursoId;
        }

        public String getCurso() {
            return curso;
        }

        public void setCurso(String curso) {
            this.curso = curso;
        }

        public Long getDocenteId() {
            return docenteId;
        }

        public void setDocenteId(Long docenteId) {
            this.docenteId = docenteId;
        }

        public String getDocenteNombreCompleto() {
            return docenteNombreCompleto;
        }

        public void setDocenteNombreCompleto(String docenteNombreCompleto) {
            this.docenteNombreCompleto = docenteNombreCompleto;
        }

        public Integer getEvaluacionesRegistradas() {
            return evaluacionesRegistradas;
        }

        public void setEvaluacionesRegistradas(Integer evaluacionesRegistradas) {
            this.evaluacionesRegistradas = evaluacionesRegistradas;
        }

        public BigDecimal getPromedio() {
            return promedio;
        }

        public void setPromedio(BigDecimal promedio) {
            this.promedio = promedio;
        }

        public List<BigDecimal> getNotas() {
            return notas;
        }

        public void setNotas(List<BigDecimal> notas) {
            this.notas = notas;
        }
    }
}
