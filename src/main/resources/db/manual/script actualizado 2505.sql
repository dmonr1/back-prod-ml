-- =========================================================
-- SCRIPT MAESTRO LIMPIO
-- SISTEMA TP1 - MODELO OPERATIVO ACTUAL
-- PostgreSQL / esquema: db_tp1
-- =========================================================

BEGIN;

DROP SCHEMA IF EXISTS db_tp1 CASCADE;
CREATE SCHEMA db_tp1;
SET search_path TO db_tp1;

-- =========================================================
-- FUNCION DE AUDITORIA
-- =========================================================

CREATE OR REPLACE FUNCTION db_tp1.actualizar_fecha_modificacion()
RETURNS TRIGGER AS $$
BEGIN
    NEW.fecha_modificacion = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- =========================================================
-- SEGURIDAD
-- =========================================================

CREATE TABLE db_tp1.roles (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(255),
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_roles_estado CHECK (estado IN ('ACTIVO', 'INACTIVO'))
);

CREATE TABLE db_tp1.usuarios (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    correo VARCHAR(120) NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    ultimo_login TIMESTAMP,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_usuarios_estado CHECK (estado IN ('ACTIVO', 'INACTIVO'))
);

CREATE TABLE db_tp1.usuarios_roles (
    usuario_id BIGINT NOT NULL,
    rol_id BIGINT NOT NULL,
    PRIMARY KEY (usuario_id, rol_id),
    CONSTRAINT fk_usuarios_roles_usuario
        FOREIGN KEY (usuario_id) REFERENCES db_tp1.usuarios(id) ON DELETE CASCADE,
    CONSTRAINT fk_usuarios_roles_rol
        FOREIGN KEY (rol_id) REFERENCES db_tp1.roles(id) ON DELETE CASCADE
);

CREATE TABLE db_tp1.docentes (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT UNIQUE,
    dni VARCHAR(8) UNIQUE,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    telefono VARCHAR(20),
    especialidad VARCHAR(100),
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_docentes_usuario FOREIGN KEY (usuario_id) REFERENCES db_tp1.usuarios(id),
    CONSTRAINT chk_docentes_estado CHECK (estado IN ('ACTIVO', 'INACTIVO'))
);

CREATE TABLE db_tp1.alumnos (
    id BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(30) NOT NULL UNIQUE,
    dni VARCHAR(8) UNIQUE,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    fecha_nacimiento DATE,
    sexo VARCHAR(20),
    direccion VARCHAR(255),
    nombre_apoderado VARCHAR(150),
    telefono_apoderado VARCHAR(20),
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_alumnos_estado CHECK (estado IN ('ACTIVO', 'INACTIVO'))
);

-- =========================================================
-- ESTRUCTURA ACADEMICA
-- =========================================================

CREATE TABLE db_tp1.niveles (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(255),
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_niveles_estado CHECK (estado IN ('ACTIVO', 'INACTIVO'))
);

CREATE TABLE db_tp1.grados (
    id BIGSERIAL PRIMARY KEY,
    nivel_id BIGINT NOT NULL,
    nombre VARCHAR(50) NOT NULL,
    orden SMALLINT NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_grados_nivel FOREIGN KEY (nivel_id) REFERENCES db_tp1.niveles(id),
    CONSTRAINT uq_grados UNIQUE (nivel_id, nombre),
    CONSTRAINT chk_grados_orden CHECK (orden > 0),
    CONSTRAINT chk_grados_estado CHECK (estado IN ('ACTIVO', 'INACTIVO'))
);

CREATE TABLE db_tp1.periodos_academicos (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    anio INTEGER NOT NULL UNIQUE,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    tipo_periodo_evaluacion VARCHAR(30) NOT NULL DEFAULT 'BIMESTRAL',
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_periodos_fechas CHECK (fecha_fin >= fecha_inicio),
    CONSTRAINT chk_periodos_tipo_evaluacion CHECK (
        tipo_periodo_evaluacion IN ('BIMESTRAL', 'TRIMESTRAL', 'SEMESTRAL', 'ANUAL')
    ),
    CONSTRAINT chk_periodos_estado CHECK (estado IN ('ACTIVO', 'INACTIVO'))
);

CREATE TABLE db_tp1.secciones (
    id BIGSERIAL PRIMARY KEY,
    grado_id BIGINT NOT NULL,
    periodo_academico_id BIGINT NOT NULL,
    nombre VARCHAR(20) NOT NULL,
    capacidad INTEGER,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_secciones_grado FOREIGN KEY (grado_id) REFERENCES db_tp1.grados(id),
    CONSTRAINT fk_secciones_periodo FOREIGN KEY (periodo_academico_id) REFERENCES db_tp1.periodos_academicos(id),
    CONSTRAINT uq_secciones_periodo UNIQUE (periodo_academico_id, grado_id, nombre),
    CONSTRAINT chk_secciones_capacidad CHECK (capacidad IS NULL OR capacidad > 0),
    CONSTRAINT chk_secciones_estado CHECK (estado IN ('ACTIVO', 'INACTIVO'))
);

CREATE TABLE db_tp1.periodos_evaluacion (
    id BIGSERIAL PRIMARY KEY,
    periodo_academico_id BIGINT NOT NULL,
    nombre VARCHAR(50) NOT NULL,
    numero SMALLINT NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_periodos_evaluacion_periodo FOREIGN KEY (periodo_academico_id) REFERENCES db_tp1.periodos_academicos(id),
    CONSTRAINT uq_periodos_evaluacion UNIQUE (periodo_academico_id, numero),
    CONSTRAINT chk_periodos_evaluacion_numero CHECK (numero > 0),
    CONSTRAINT chk_periodos_evaluacion_fechas CHECK (fecha_fin >= fecha_inicio),
    CONSTRAINT chk_periodos_evaluacion_estado CHECK (estado IN ('ACTIVO', 'INACTIVO'))
);

CREATE TABLE db_tp1.cursos (
    id BIGSERIAL PRIMARY KEY,
    nivel_id BIGINT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_cursos_nivel FOREIGN KEY (nivel_id) REFERENCES db_tp1.niveles(id),
    CONSTRAINT uq_cursos UNIQUE (nivel_id, nombre),
    CONSTRAINT chk_cursos_estado CHECK (estado IN ('ACTIVO', 'INACTIVO'))
);

CREATE TABLE db_tp1.cursos_periodo_academico (
    id BIGSERIAL PRIMARY KEY,
    periodo_academico_id BIGINT NOT NULL,
    curso_id BIGINT NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_cursos_periodo_periodo FOREIGN KEY (periodo_academico_id) REFERENCES db_tp1.periodos_academicos(id),
    CONSTRAINT fk_cursos_periodo_curso FOREIGN KEY (curso_id) REFERENCES db_tp1.cursos(id),
    CONSTRAINT uq_cursos_periodo UNIQUE (periodo_academico_id, curso_id),
    CONSTRAINT chk_cursos_periodo_estado CHECK (estado IN ('ACTIVO', 'INACTIVO'))
);

CREATE TABLE db_tp1.matriculas (
    id BIGSERIAL PRIMARY KEY,
    alumno_id BIGINT NOT NULL,
    grado_id BIGINT NOT NULL,
    seccion_id BIGINT NOT NULL,
    periodo_academico_id BIGINT NOT NULL,
    fecha_matricula DATE NOT NULL DEFAULT CURRENT_DATE,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_matriculas_alumno FOREIGN KEY (alumno_id) REFERENCES db_tp1.alumnos(id),
    CONSTRAINT fk_matriculas_grado FOREIGN KEY (grado_id) REFERENCES db_tp1.grados(id),
    CONSTRAINT fk_matriculas_seccion FOREIGN KEY (seccion_id) REFERENCES db_tp1.secciones(id),
    CONSTRAINT fk_matriculas_periodo FOREIGN KEY (periodo_academico_id) REFERENCES db_tp1.periodos_academicos(id),
    CONSTRAINT uq_matriculas UNIQUE (alumno_id, periodo_academico_id),
    CONSTRAINT chk_matriculas_estado CHECK (estado IN ('ACTIVO', 'INACTIVO'))
);

CREATE TABLE db_tp1.docente_curso_seccion (
    id BIGSERIAL PRIMARY KEY,
    docente_id BIGINT NOT NULL,
    curso_id BIGINT NOT NULL,
    seccion_id BIGINT NOT NULL,
    periodo_academico_id BIGINT NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_dcs_docente FOREIGN KEY (docente_id) REFERENCES db_tp1.docentes(id),
    CONSTRAINT fk_dcs_curso FOREIGN KEY (curso_id) REFERENCES db_tp1.cursos(id),
    CONSTRAINT fk_dcs_seccion FOREIGN KEY (seccion_id) REFERENCES db_tp1.secciones(id),
    CONSTRAINT fk_dcs_periodo FOREIGN KEY (periodo_academico_id) REFERENCES db_tp1.periodos_academicos(id),
    CONSTRAINT uq_dcs UNIQUE (docente_id, curso_id, seccion_id, periodo_academico_id),
    CONSTRAINT chk_dcs_estado CHECK (estado IN ('ACTIVO', 'INACTIVO'))
);

CREATE TABLE db_tp1.tutorias (
    id BIGSERIAL PRIMARY KEY,
    docente_id BIGINT NOT NULL,
    seccion_id BIGINT NOT NULL,
    periodo_academico_id BIGINT NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_tutorias_docente FOREIGN KEY (docente_id) REFERENCES db_tp1.docentes(id),
    CONSTRAINT fk_tutorias_seccion FOREIGN KEY (seccion_id) REFERENCES db_tp1.secciones(id),
    CONSTRAINT fk_tutorias_periodo FOREIGN KEY (periodo_academico_id) REFERENCES db_tp1.periodos_academicos(id),
    CONSTRAINT uq_tutorias UNIQUE (seccion_id, periodo_academico_id),
    CONSTRAINT chk_tutorias_estado CHECK (estado IN ('ACTIVO', 'INACTIVO'))
);

-- =========================================================
-- EVALUACION CONFIGURABLE
-- =========================================================

CREATE TABLE db_tp1.tipos_evaluacion (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(255),
    orden SMALLINT NOT NULL DEFAULT 1,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_tipos_evaluacion_orden CHECK (orden > 0),
    CONSTRAINT chk_tipos_evaluacion_estado CHECK (estado IN ('ACTIVO', 'INACTIVO'))
);

CREATE TABLE db_tp1.configuraciones_evaluacion_periodo (
    id BIGSERIAL PRIMARY KEY,
    periodo_academico_id BIGINT NOT NULL,
    tipo_evaluacion_id BIGINT NOT NULL,
    cantidad_evaluaciones INTEGER NOT NULL DEFAULT 0,
    calcular_en_promedio BOOLEAN NOT NULL DEFAULT TRUE,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_config_eval_periodo_periodo FOREIGN KEY (periodo_academico_id) REFERENCES db_tp1.periodos_academicos(id),
    CONSTRAINT fk_config_eval_periodo_tipo FOREIGN KEY (tipo_evaluacion_id) REFERENCES db_tp1.tipos_evaluacion(id),
    CONSTRAINT uq_config_eval_periodo UNIQUE (periodo_academico_id, tipo_evaluacion_id),
    CONSTRAINT chk_config_eval_periodo_cantidad CHECK (cantidad_evaluaciones >= 0),
    CONSTRAINT chk_config_eval_periodo_estado CHECK (estado IN ('ACTIVO', 'INACTIVO'))
);

CREATE TABLE db_tp1.configuraciones_evaluacion_curso (
    id BIGSERIAL PRIMARY KEY,
    periodo_academico_id BIGINT NOT NULL,
    curso_id BIGINT NOT NULL,
    tipo_evaluacion_id BIGINT NOT NULL,
    cantidad_evaluaciones INTEGER NOT NULL DEFAULT 0,
    calcular_en_promedio BOOLEAN NOT NULL DEFAULT TRUE,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_config_eval_curso_periodo FOREIGN KEY (periodo_academico_id) REFERENCES db_tp1.periodos_academicos(id),
    CONSTRAINT fk_config_eval_curso_curso FOREIGN KEY (curso_id) REFERENCES db_tp1.cursos(id),
    CONSTRAINT fk_config_eval_curso_tipo FOREIGN KEY (tipo_evaluacion_id) REFERENCES db_tp1.tipos_evaluacion(id),
    CONSTRAINT uq_config_eval_curso UNIQUE (periodo_academico_id, curso_id, tipo_evaluacion_id),
    CONSTRAINT chk_config_eval_curso_cantidad CHECK (cantidad_evaluaciones >= 0),
    CONSTRAINT chk_config_eval_curso_estado CHECK (estado IN ('ACTIVO', 'INACTIVO'))
);

CREATE TABLE db_tp1.configuraciones_evaluacion (
    id BIGSERIAL PRIMARY KEY,
    periodo_academico_id BIGINT NOT NULL,
    periodo_evaluacion_id BIGINT NOT NULL,
    curso_id BIGINT NOT NULL,
    grado_id BIGINT,
    tipo_evaluacion_id BIGINT NOT NULL,
    cantidad_evaluaciones INTEGER NOT NULL,
    calcular_en_promedio BOOLEAN NOT NULL DEFAULT TRUE,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_conf_eval_periodo FOREIGN KEY (periodo_academico_id) REFERENCES db_tp1.periodos_academicos(id),
    CONSTRAINT fk_conf_eval_periodo_evaluacion FOREIGN KEY (periodo_evaluacion_id) REFERENCES db_tp1.periodos_evaluacion(id),
    CONSTRAINT fk_conf_eval_curso FOREIGN KEY (curso_id) REFERENCES db_tp1.cursos(id),
    CONSTRAINT fk_conf_eval_grado FOREIGN KEY (grado_id) REFERENCES db_tp1.grados(id),
    CONSTRAINT fk_conf_eval_tipo FOREIGN KEY (tipo_evaluacion_id) REFERENCES db_tp1.tipos_evaluacion(id),
    CONSTRAINT chk_conf_eval_cantidad CHECK (cantidad_evaluaciones > 0),
    CONSTRAINT chk_conf_eval_estado CHECK (estado IN ('ACTIVO', 'INACTIVO'))
);

CREATE UNIQUE INDEX uq_conf_eval_derivada
ON db_tp1.configuraciones_evaluacion (
    periodo_evaluacion_id,
    curso_id,
    tipo_evaluacion_id,
    COALESCE(grado_id, 0)
);

CREATE TABLE db_tp1.configuraciones_asistencia_periodo (
    id BIGSERIAL PRIMARY KEY,
    docente_curso_seccion_id BIGINT NOT NULL,
    periodo_evaluacion_id BIGINT NOT NULL,
    clases_programadas INTEGER NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_config_asistencia_docente_curso_seccion
        FOREIGN KEY (docente_curso_seccion_id) REFERENCES db_tp1.docente_curso_seccion(id),
    CONSTRAINT fk_config_asistencia_periodo_evaluacion
        FOREIGN KEY (periodo_evaluacion_id) REFERENCES db_tp1.periodos_evaluacion(id),
    CONSTRAINT uq_config_asistencia_periodo UNIQUE (docente_curso_seccion_id, periodo_evaluacion_id),
    CONSTRAINT chk_config_asistencia_programadas CHECK (clases_programadas >= 0),
    CONSTRAINT chk_config_asistencia_estado CHECK (estado IN ('ACTIVO', 'INACTIVO'))
);

CREATE TABLE db_tp1.evaluaciones (
    id BIGSERIAL PRIMARY KEY,
    configuracion_evaluacion_id BIGINT NOT NULL,
    docente_curso_seccion_id BIGINT NOT NULL,
    periodo_evaluacion_id BIGINT NOT NULL,
    tipo_evaluacion_id BIGINT NOT NULL,
    numero_evaluacion INTEGER NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    fecha_evaluacion DATE,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_evaluaciones_configuracion FOREIGN KEY (configuracion_evaluacion_id) REFERENCES db_tp1.configuraciones_evaluacion(id),
    CONSTRAINT fk_evaluaciones_dcs FOREIGN KEY (docente_curso_seccion_id) REFERENCES db_tp1.docente_curso_seccion(id),
    CONSTRAINT fk_evaluaciones_periodo_evaluacion FOREIGN KEY (periodo_evaluacion_id) REFERENCES db_tp1.periodos_evaluacion(id),
    CONSTRAINT fk_evaluaciones_tipo FOREIGN KEY (tipo_evaluacion_id) REFERENCES db_tp1.tipos_evaluacion(id),
    CONSTRAINT uq_evaluaciones UNIQUE (docente_curso_seccion_id, periodo_evaluacion_id, tipo_evaluacion_id, numero_evaluacion),
    CONSTRAINT chk_evaluaciones_numero CHECK (numero_evaluacion > 0),
    CONSTRAINT chk_evaluaciones_estado CHECK (estado IN ('ACTIVO', 'INACTIVO'))
);

CREATE TABLE db_tp1.detalle_notas_evaluacion (
    id BIGSERIAL PRIMARY KEY,
    evaluacion_id BIGINT NOT NULL,
    matricula_id BIGINT NOT NULL,
    nota NUMERIC(4,2) NOT NULL,
    observacion VARCHAR(255),
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_det_notas_evaluacion FOREIGN KEY (evaluacion_id) REFERENCES db_tp1.evaluaciones(id),
    CONSTRAINT fk_det_notas_matricula FOREIGN KEY (matricula_id) REFERENCES db_tp1.matriculas(id),
    CONSTRAINT uq_det_notas UNIQUE (evaluacion_id, matricula_id),
    CONSTRAINT chk_det_notas_rango CHECK (nota >= 0 AND nota <= 20),
    CONSTRAINT chk_det_notas_estado CHECK (estado IN ('ACTIVO', 'INACTIVO'))
);

-- =========================================================
-- CONSOLIDADOS Y LEGACY
-- =========================================================

CREATE TABLE db_tp1.notas_curso_periodo_evaluacion (
    id BIGSERIAL PRIMARY KEY,
    matricula_id BIGINT NOT NULL,
    curso_id BIGINT NOT NULL,
    periodo_evaluacion_id BIGINT NOT NULL,
    promedio_curso NUMERIC(5,2) NOT NULL,
    cantidad_evaluaciones_registradas INTEGER NOT NULL DEFAULT 0,
    observacion VARCHAR(255),
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_notas_cb_matricula FOREIGN KEY (matricula_id) REFERENCES db_tp1.matriculas(id),
    CONSTRAINT fk_notas_cb_curso FOREIGN KEY (curso_id) REFERENCES db_tp1.cursos(id),
    CONSTRAINT fk_notas_cb_periodo_evaluacion FOREIGN KEY (periodo_evaluacion_id) REFERENCES db_tp1.periodos_evaluacion(id),
    CONSTRAINT uq_notas_cb UNIQUE (matricula_id, curso_id, periodo_evaluacion_id),
    CONSTRAINT chk_notas_cb_rango CHECK (promedio_curso >= 0 AND promedio_curso <= 20),
    CONSTRAINT chk_notas_cb_estado CHECK (estado IN ('ACTIVO', 'INACTIVO'))
);

CREATE TABLE db_tp1.asistencias_periodo_evaluacion (
    id BIGSERIAL PRIMARY KEY,
    matricula_id BIGINT NOT NULL,
    periodo_evaluacion_id BIGINT NOT NULL,
    clases_programadas INTEGER NOT NULL,
    clases_asistidas INTEGER NOT NULL,
    observacion VARCHAR(255),
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_asist_pe_matricula FOREIGN KEY (matricula_id) REFERENCES db_tp1.matriculas(id),
    CONSTRAINT fk_asist_pe_periodo FOREIGN KEY (periodo_evaluacion_id) REFERENCES db_tp1.periodos_evaluacion(id),
    CONSTRAINT uq_asist_pe UNIQUE (matricula_id, periodo_evaluacion_id),
    CONSTRAINT chk_asist_pe_programadas CHECK (clases_programadas >= 0),
    CONSTRAINT chk_asist_pe_asistidas CHECK (clases_asistidas >= 0),
    CONSTRAINT chk_asist_pe_limite CHECK (clases_asistidas <= clases_programadas),
    CONSTRAINT chk_asist_pe_estado CHECK (estado IN ('ACTIVO', 'INACTIVO'))
);

CREATE TABLE db_tp1.cargas_archivos (
    id BIGSERIAL PRIMARY KEY,
    docente_id BIGINT NOT NULL,
    periodo_academico_id BIGINT NOT NULL,
    periodo_evaluacion_id BIGINT NOT NULL,
    seccion_id BIGINT NOT NULL,
    nombre_archivo VARCHAR(255) NOT NULL,
    total_filas INTEGER NOT NULL DEFAULT 0,
    filas_validas INTEGER NOT NULL DEFAULT 0,
    filas_error INTEGER NOT NULL DEFAULT 0,
    estado_proceso VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE',
    observacion VARCHAR(255),
    fecha_carga TIMESTAMP NOT NULL DEFAULT NOW(),
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_cargas_docente FOREIGN KEY (docente_id) REFERENCES db_tp1.docentes(id),
    CONSTRAINT fk_cargas_periodo FOREIGN KEY (periodo_academico_id) REFERENCES db_tp1.periodos_academicos(id),
    CONSTRAINT fk_cargas_periodo_evaluacion FOREIGN KEY (periodo_evaluacion_id) REFERENCES db_tp1.periodos_evaluacion(id),
    CONSTRAINT fk_cargas_seccion FOREIGN KEY (seccion_id) REFERENCES db_tp1.secciones(id),
    CONSTRAINT chk_cargas_estado CHECK (estado IN ('ACTIVO', 'INACTIVO'))
);

CREATE TABLE db_tp1.notas (
    id BIGSERIAL PRIMARY KEY,
    matricula_id BIGINT NOT NULL,
    curso_id BIGINT NOT NULL,
    periodo_evaluacion_id BIGINT NOT NULL,
    carga_excel_id BIGINT,
    nota NUMERIC(4,2) NOT NULL,
    observacion VARCHAR(255),
    registrado_por BIGINT,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_notas_matricula FOREIGN KEY (matricula_id) REFERENCES db_tp1.matriculas(id),
    CONSTRAINT fk_notas_curso FOREIGN KEY (curso_id) REFERENCES db_tp1.cursos(id),
    CONSTRAINT fk_notas_periodo FOREIGN KEY (periodo_evaluacion_id) REFERENCES db_tp1.periodos_evaluacion(id),
    CONSTRAINT fk_notas_carga FOREIGN KEY (carga_excel_id) REFERENCES db_tp1.cargas_archivos(id),
    CONSTRAINT fk_notas_docente FOREIGN KEY (registrado_por) REFERENCES db_tp1.docentes(id),
    CONSTRAINT chk_notas_rango CHECK (nota >= 0 AND nota <= 20),
    CONSTRAINT chk_notas_estado CHECK (estado IN ('ACTIVO', 'INACTIVO'))
);

CREATE TABLE db_tp1.asistencias (
    id BIGSERIAL PRIMARY KEY,
    matricula_id BIGINT NOT NULL,
    periodo_evaluacion_id BIGINT NOT NULL,
    clases_programadas INTEGER NOT NULL,
    clases_asistidas INTEGER NOT NULL,
    observacion VARCHAR(255),
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_asistencias_matricula FOREIGN KEY (matricula_id) REFERENCES db_tp1.matriculas(id),
    CONSTRAINT fk_asistencias_periodo FOREIGN KEY (periodo_evaluacion_id) REFERENCES db_tp1.periodos_evaluacion(id),
    CONSTRAINT chk_asistencias_programadas CHECK (clases_programadas >= 0),
    CONSTRAINT chk_asistencias_asistidas CHECK (clases_asistidas >= 0),
    CONSTRAINT chk_asistencias_limite CHECK (clases_asistidas <= clases_programadas),
    CONSTRAINT chk_asistencias_estado CHECK (estado IN ('ACTIVO', 'INACTIVO'))
);

-- =========================================================
-- PREDICCION, ALERTAS Y HALLAZGOS
-- =========================================================

CREATE TABLE db_tp1.predicciones_riesgo_global (
    id BIGSERIAL PRIMARY KEY,
    matricula_id BIGINT NOT NULL,
    periodo_evaluacion_id BIGINT NOT NULL,
    carga_archivo_id BIGINT,
    puntaje_riesgo NUMERIC(5,2) NOT NULL,
    nivel_riesgo VARCHAR(20) NOT NULL,
    modelo_version VARCHAR(50),
    variables_entrada JSONB,
    fecha_prediccion TIMESTAMP NOT NULL DEFAULT NOW(),
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_pred_global_matricula FOREIGN KEY (matricula_id) REFERENCES db_tp1.matriculas(id),
    CONSTRAINT fk_pred_global_periodo FOREIGN KEY (periodo_evaluacion_id) REFERENCES db_tp1.periodos_evaluacion(id),
    CONSTRAINT fk_pred_global_carga FOREIGN KEY (carga_archivo_id) REFERENCES db_tp1.cargas_archivos(id),
    CONSTRAINT uq_pred_global UNIQUE (matricula_id, periodo_evaluacion_id),
    CONSTRAINT chk_pred_global_puntaje CHECK (puntaje_riesgo >= 0 AND puntaje_riesgo <= 100),
    CONSTRAINT chk_pred_global_nivel CHECK (nivel_riesgo IN ('BAJO', 'MEDIO', 'ALTO')),
    CONSTRAINT chk_pred_global_estado CHECK (estado IN ('ACTIVO', 'INACTIVO'))
);

CREATE TABLE db_tp1.predicciones_riesgo_curso (
    id BIGSERIAL PRIMARY KEY,
    matricula_id BIGINT NOT NULL,
    curso_id BIGINT NOT NULL,
    periodo_evaluacion_id BIGINT NOT NULL,
    carga_archivo_id BIGINT,
    puntaje_riesgo NUMERIC(5,2) NOT NULL,
    nivel_riesgo VARCHAR(20) NOT NULL,
    modelo_version VARCHAR(50),
    variables_entrada JSONB,
    fecha_prediccion TIMESTAMP NOT NULL DEFAULT NOW(),
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_pred_curso_matricula FOREIGN KEY (matricula_id) REFERENCES db_tp1.matriculas(id),
    CONSTRAINT fk_pred_curso_curso FOREIGN KEY (curso_id) REFERENCES db_tp1.cursos(id),
    CONSTRAINT fk_pred_curso_periodo FOREIGN KEY (periodo_evaluacion_id) REFERENCES db_tp1.periodos_evaluacion(id),
    CONSTRAINT fk_pred_curso_carga FOREIGN KEY (carga_archivo_id) REFERENCES db_tp1.cargas_archivos(id),
    CONSTRAINT uq_pred_curso UNIQUE (matricula_id, curso_id, periodo_evaluacion_id),
    CONSTRAINT chk_pred_curso_puntaje CHECK (puntaje_riesgo >= 0 AND puntaje_riesgo <= 100),
    CONSTRAINT chk_pred_curso_nivel CHECK (nivel_riesgo IN ('BAJO', 'MEDIO', 'ALTO')),
    CONSTRAINT chk_pred_curso_estado CHECK (estado IN ('ACTIVO', 'INACTIVO'))
);

CREATE TABLE db_tp1.alertas (
    id BIGSERIAL PRIMARY KEY,
    matricula_id BIGINT NOT NULL,
    curso_id BIGINT,
    prediccion_global_id BIGINT,
    prediccion_curso_id BIGINT,
    tipo_alerta VARCHAR(50) NOT NULL,
    nivel_riesgo VARCHAR(20) NOT NULL,
    mensaje VARCHAR(255) NOT NULL,
    atendida BOOLEAN NOT NULL DEFAULT FALSE,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_alertas_matricula FOREIGN KEY (matricula_id) REFERENCES db_tp1.matriculas(id),
    CONSTRAINT fk_alertas_curso FOREIGN KEY (curso_id) REFERENCES db_tp1.cursos(id),
    CONSTRAINT fk_alertas_pred_global FOREIGN KEY (prediccion_global_id) REFERENCES db_tp1.predicciones_riesgo_global(id),
    CONSTRAINT fk_alertas_pred_curso FOREIGN KEY (prediccion_curso_id) REFERENCES db_tp1.predicciones_riesgo_curso(id),
    CONSTRAINT chk_alertas_nivel CHECK (nivel_riesgo IN ('BAJO', 'MEDIO', 'ALTO')),
    CONSTRAINT chk_alertas_estado CHECK (estado IN ('ACTIVO', 'INACTIVO'))
);

CREATE TABLE db_tp1.recomendaciones (
    id BIGSERIAL PRIMARY KEY,
    matricula_id BIGINT NOT NULL,
    curso_id BIGINT,
    prediccion_global_id BIGINT,
    prediccion_curso_id BIGINT,
    titulo VARCHAR(150) NOT NULL,
    descripcion TEXT NOT NULL,
    fuente VARCHAR(50) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_recom_matricula FOREIGN KEY (matricula_id) REFERENCES db_tp1.matriculas(id),
    CONSTRAINT fk_recom_curso FOREIGN KEY (curso_id) REFERENCES db_tp1.cursos(id),
    CONSTRAINT fk_recom_pred_global FOREIGN KEY (prediccion_global_id) REFERENCES db_tp1.predicciones_riesgo_global(id),
    CONSTRAINT fk_recom_pred_curso FOREIGN KEY (prediccion_curso_id) REFERENCES db_tp1.predicciones_riesgo_curso(id),
    CONSTRAINT chk_recom_fuente CHECK (fuente IN ('REGLA', 'MODELO_ML', 'MANUAL')),
    CONSTRAINT chk_recom_estado CHECK (estado IN ('ACTIVO', 'INACTIVO'))
);

CREATE TABLE db_tp1.hallazgos_data_mining (
    id BIGSERIAL PRIMARY KEY,
    periodo_academico_id BIGINT NOT NULL,
    periodo_evaluacion_id BIGINT,
    seccion_id BIGINT,
    curso_id BIGINT,
    codigo VARCHAR(50),
    tipo VARCHAR(50) NOT NULL,
    titulo VARCHAR(150) NOT NULL,
    descripcion TEXT NOT NULL,
    nivel_relevancia VARCHAR(20) NOT NULL DEFAULT 'MEDIO',
    fuente VARCHAR(30) NOT NULL DEFAULT 'AGREGACION',
    resultado JSONB,
    fecha_generacion TIMESTAMP NOT NULL DEFAULT NOW(),
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_hallazgos_periodo FOREIGN KEY (periodo_academico_id) REFERENCES db_tp1.periodos_academicos(id),
    CONSTRAINT fk_hallazgos_periodo_evaluacion FOREIGN KEY (periodo_evaluacion_id) REFERENCES db_tp1.periodos_evaluacion(id),
    CONSTRAINT fk_hallazgos_seccion FOREIGN KEY (seccion_id) REFERENCES db_tp1.secciones(id),
    CONSTRAINT fk_hallazgos_curso FOREIGN KEY (curso_id) REFERENCES db_tp1.cursos(id),
    CONSTRAINT chk_hallazgos_estado CHECK (estado IN ('ACTIVO', 'INACTIVO')),
    CONSTRAINT chk_hallazgos_relevancia CHECK (nivel_relevancia IN ('ALTO', 'MEDIO', 'BAJO')),
    CONSTRAINT chk_hallazgos_fuente CHECK (fuente IN ('ML', 'REGLA', 'AGREGACION', 'PATRON'))
);

-- =========================================================
-- INDICES
-- =========================================================

CREATE INDEX idx_matriculas_seccion_periodo ON db_tp1.matriculas(seccion_id, periodo_academico_id);
CREATE INDEX idx_dcs_periodo ON db_tp1.docente_curso_seccion(periodo_academico_id);
CREATE INDEX idx_eval_asignacion_periodo_estado ON db_tp1.evaluaciones(docente_curso_seccion_id, periodo_evaluacion_id, estado);
CREATE INDEX idx_det_notas_eval_matricula ON db_tp1.detalle_notas_evaluacion(evaluacion_id, matricula_id);
CREATE INDEX idx_notas_curso_periodo_estado ON db_tp1.notas_curso_periodo_evaluacion(matricula_id, periodo_evaluacion_id, estado);
CREATE INDEX idx_asistencia_periodo_estado ON db_tp1.asistencias_periodo_evaluacion(matricula_id, periodo_evaluacion_id, estado);
CREATE INDEX idx_pred_global_periodo ON db_tp1.predicciones_riesgo_global(periodo_evaluacion_id);
CREATE INDEX idx_pred_curso_periodo ON db_tp1.predicciones_riesgo_curso(periodo_evaluacion_id);
CREATE INDEX idx_alertas_matricula_estado ON db_tp1.alertas(matricula_id, estado);
CREATE INDEX idx_recomendaciones_matricula_estado ON db_tp1.recomendaciones(matricula_id, estado);
CREATE INDEX idx_hallazgos_periodo_seccion ON db_tp1.hallazgos_data_mining(periodo_evaluacion_id, seccion_id, estado);

-- =========================================================
-- TRIGGERS DE AUDITORIA
-- =========================================================

CREATE TRIGGER trg_roles_mod BEFORE UPDATE ON db_tp1.roles FOR EACH ROW EXECUTE FUNCTION db_tp1.actualizar_fecha_modificacion();
CREATE TRIGGER trg_usuarios_mod BEFORE UPDATE ON db_tp1.usuarios FOR EACH ROW EXECUTE FUNCTION db_tp1.actualizar_fecha_modificacion();
CREATE TRIGGER trg_docentes_mod BEFORE UPDATE ON db_tp1.docentes FOR EACH ROW EXECUTE FUNCTION db_tp1.actualizar_fecha_modificacion();
CREATE TRIGGER trg_alumnos_mod BEFORE UPDATE ON db_tp1.alumnos FOR EACH ROW EXECUTE FUNCTION db_tp1.actualizar_fecha_modificacion();
CREATE TRIGGER trg_niveles_mod BEFORE UPDATE ON db_tp1.niveles FOR EACH ROW EXECUTE FUNCTION db_tp1.actualizar_fecha_modificacion();
CREATE TRIGGER trg_grados_mod BEFORE UPDATE ON db_tp1.grados FOR EACH ROW EXECUTE FUNCTION db_tp1.actualizar_fecha_modificacion();
CREATE TRIGGER trg_periodos_mod BEFORE UPDATE ON db_tp1.periodos_academicos FOR EACH ROW EXECUTE FUNCTION db_tp1.actualizar_fecha_modificacion();
CREATE TRIGGER trg_secciones_mod BEFORE UPDATE ON db_tp1.secciones FOR EACH ROW EXECUTE FUNCTION db_tp1.actualizar_fecha_modificacion();
CREATE TRIGGER trg_periodos_eval_mod BEFORE UPDATE ON db_tp1.periodos_evaluacion FOR EACH ROW EXECUTE FUNCTION db_tp1.actualizar_fecha_modificacion();
CREATE TRIGGER trg_cursos_mod BEFORE UPDATE ON db_tp1.cursos FOR EACH ROW EXECUTE FUNCTION db_tp1.actualizar_fecha_modificacion();
CREATE TRIGGER trg_cursos_periodo_mod BEFORE UPDATE ON db_tp1.cursos_periodo_academico FOR EACH ROW EXECUTE FUNCTION db_tp1.actualizar_fecha_modificacion();
CREATE TRIGGER trg_matriculas_mod BEFORE UPDATE ON db_tp1.matriculas FOR EACH ROW EXECUTE FUNCTION db_tp1.actualizar_fecha_modificacion();
CREATE TRIGGER trg_dcs_mod BEFORE UPDATE ON db_tp1.docente_curso_seccion FOR EACH ROW EXECUTE FUNCTION db_tp1.actualizar_fecha_modificacion();
CREATE TRIGGER trg_tutorias_mod BEFORE UPDATE ON db_tp1.tutorias FOR EACH ROW EXECUTE FUNCTION db_tp1.actualizar_fecha_modificacion();
CREATE TRIGGER trg_tipos_eval_mod BEFORE UPDATE ON db_tp1.tipos_evaluacion FOR EACH ROW EXECUTE FUNCTION db_tp1.actualizar_fecha_modificacion();
CREATE TRIGGER trg_conf_eval_periodo_mod BEFORE UPDATE ON db_tp1.configuraciones_evaluacion_periodo FOR EACH ROW EXECUTE FUNCTION db_tp1.actualizar_fecha_modificacion();
CREATE TRIGGER trg_conf_eval_curso_mod BEFORE UPDATE ON db_tp1.configuraciones_evaluacion_curso FOR EACH ROW EXECUTE FUNCTION db_tp1.actualizar_fecha_modificacion();
CREATE TRIGGER trg_conf_eval_mod BEFORE UPDATE ON db_tp1.configuraciones_evaluacion FOR EACH ROW EXECUTE FUNCTION db_tp1.actualizar_fecha_modificacion();
CREATE TRIGGER trg_conf_asistencia_mod BEFORE UPDATE ON db_tp1.configuraciones_asistencia_periodo FOR EACH ROW EXECUTE FUNCTION db_tp1.actualizar_fecha_modificacion();
CREATE TRIGGER trg_evaluaciones_mod BEFORE UPDATE ON db_tp1.evaluaciones FOR EACH ROW EXECUTE FUNCTION db_tp1.actualizar_fecha_modificacion();
CREATE TRIGGER trg_det_notas_mod BEFORE UPDATE ON db_tp1.detalle_notas_evaluacion FOR EACH ROW EXECUTE FUNCTION db_tp1.actualizar_fecha_modificacion();
CREATE TRIGGER trg_notas_cb_mod BEFORE UPDATE ON db_tp1.notas_curso_periodo_evaluacion FOR EACH ROW EXECUTE FUNCTION db_tp1.actualizar_fecha_modificacion();
CREATE TRIGGER trg_asistencias_pe_mod BEFORE UPDATE ON db_tp1.asistencias_periodo_evaluacion FOR EACH ROW EXECUTE FUNCTION db_tp1.actualizar_fecha_modificacion();
CREATE TRIGGER trg_cargas_mod BEFORE UPDATE ON db_tp1.cargas_archivos FOR EACH ROW EXECUTE FUNCTION db_tp1.actualizar_fecha_modificacion();
CREATE TRIGGER trg_notas_mod BEFORE UPDATE ON db_tp1.notas FOR EACH ROW EXECUTE FUNCTION db_tp1.actualizar_fecha_modificacion();
CREATE TRIGGER trg_asistencias_mod BEFORE UPDATE ON db_tp1.asistencias FOR EACH ROW EXECUTE FUNCTION db_tp1.actualizar_fecha_modificacion();
CREATE TRIGGER trg_pred_global_mod BEFORE UPDATE ON db_tp1.predicciones_riesgo_global FOR EACH ROW EXECUTE FUNCTION db_tp1.actualizar_fecha_modificacion();
CREATE TRIGGER trg_pred_curso_mod BEFORE UPDATE ON db_tp1.predicciones_riesgo_curso FOR EACH ROW EXECUTE FUNCTION db_tp1.actualizar_fecha_modificacion();
CREATE TRIGGER trg_alertas_mod BEFORE UPDATE ON db_tp1.alertas FOR EACH ROW EXECUTE FUNCTION db_tp1.actualizar_fecha_modificacion();
CREATE TRIGGER trg_recomendaciones_mod BEFORE UPDATE ON db_tp1.recomendaciones FOR EACH ROW EXECUTE FUNCTION db_tp1.actualizar_fecha_modificacion();
CREATE TRIGGER trg_hallazgos_mod BEFORE UPDATE ON db_tp1.hallazgos_data_mining FOR EACH ROW EXECUTE FUNCTION db_tp1.actualizar_fecha_modificacion();

-- =========================================================
-- VISTAS DE APOYO
-- =========================================================

CREATE OR REPLACE VIEW db_tp1.vw_asistencia_porcentaje AS
SELECT
    ape.id,
    ape.matricula_id,
    ape.periodo_evaluacion_id,
    ape.clases_programadas,
    ape.clases_asistidas,
    CASE
        WHEN ape.clases_programadas = 0 THEN 0
        ELSE ROUND((ape.clases_asistidas::numeric / ape.clases_programadas::numeric) * 100, 2)
    END AS porcentaje_asistencia
FROM db_tp1.asistencias_periodo_evaluacion ape;

CREATE OR REPLACE VIEW db_tp1.vw_predicciones_globales_legibles AS
SELECT
    prg.id,
    a.codigo AS codigo_alumno,
    a.nombres,
    a.apellidos,
    n.nombre AS nivel,
    g.nombre AS grado,
    s.nombre AS seccion,
    pe.nombre AS periodo_evaluacion,
    prg.puntaje_riesgo,
    prg.nivel_riesgo,
    prg.modelo_version,
    prg.fecha_prediccion
FROM db_tp1.predicciones_riesgo_global prg
JOIN db_tp1.matriculas m ON m.id = prg.matricula_id
JOIN db_tp1.alumnos a ON a.id = m.alumno_id
JOIN db_tp1.grados g ON g.id = m.grado_id
JOIN db_tp1.niveles n ON n.id = g.nivel_id
JOIN db_tp1.secciones s ON s.id = m.seccion_id
JOIN db_tp1.periodos_evaluacion pe ON pe.id = prg.periodo_evaluacion_id;

CREATE OR REPLACE VIEW db_tp1.vw_docente_asignaciones AS
SELECT
    dcs.id,
    d.id AS docente_id,
    d.nombres,
    d.apellidos,
    c.id AS curso_id,
    c.nombre AS curso,
    s.id AS seccion_id,
    s.nombre AS seccion,
    g.nombre AS grado,
    pa.id AS periodo_academico_id,
    pa.nombre AS periodo_academico
FROM db_tp1.docente_curso_seccion dcs
JOIN db_tp1.docentes d ON d.id = dcs.docente_id
JOIN db_tp1.cursos c ON c.id = dcs.curso_id
JOIN db_tp1.secciones s ON s.id = dcs.seccion_id
JOIN db_tp1.grados g ON g.id = s.grado_id
JOIN db_tp1.periodos_academicos pa ON pa.id = dcs.periodo_academico_id;

-- =========================================================
-- INSERTS BASE
-- =========================================================

INSERT INTO db_tp1.roles (nombre, descripcion) VALUES
('ADMIN', 'Administrador del sistema'),
('DOCENTE', 'Docente de curso'),
('DOCENTE_TUTOR', 'Docente tutor de seccion');

INSERT INTO db_tp1.niveles (nombre, descripcion) VALUES
('PRIMARIA', 'Nivel primaria'),
('SECUNDARIA', 'Nivel secundaria');

INSERT INTO db_tp1.tipos_evaluacion (nombre, descripcion, orden) VALUES
('EXAMEN_DIARIO', 'Evaluaciones cortas o controles diarios', 1),
('REVISION_CUADERNO', 'Revision de cuaderno', 2),
('REVISION_LIBRO', 'Revision de libro', 3),
('TAREA_TRABAJO', 'Tareas y trabajos', 4),
('EXPOSICION_PARTICIPACION', 'Exposicion y participacion', 5),
('EXAMEN', 'Evaluacion formal', 6);

COMMIT;

-- =========================================================
-- SELECTS DE APOYO
-- =========================================================

SELECT * FROM db_tp1.roles ORDER BY id;
SELECT * FROM db_tp1.usuarios ORDER BY id;
SELECT * FROM db_tp1.usuarios_roles ORDER BY usuario_id, rol_id;
SELECT * FROM db_tp1.docentes ORDER BY id;
SELECT * FROM db_tp1.alumnos ORDER BY id;
SELECT * FROM db_tp1.niveles ORDER BY id;
SELECT * FROM db_tp1.grados ORDER BY id;
SELECT * FROM db_tp1.periodos_academicos ORDER BY id;
SELECT * FROM db_tp1.secciones ORDER BY id;
SELECT * FROM db_tp1.periodos_evaluacion ORDER BY id;
SELECT * FROM db_tp1.cursos ORDER BY id;
SELECT * FROM db_tp1.cursos_periodo_academico ORDER BY id;
SELECT * FROM db_tp1.matriculas ORDER BY id;
SELECT * FROM db_tp1.docente_curso_seccion ORDER BY id;
SELECT * FROM db_tp1.tutorias ORDER BY id;
SELECT * FROM db_tp1.tipos_evaluacion ORDER BY orden, id;
SELECT * FROM db_tp1.configuraciones_evaluacion_periodo ORDER BY id;
SELECT * FROM db_tp1.configuraciones_evaluacion_curso ORDER BY id;
SELECT * FROM db_tp1.configuraciones_evaluacion ORDER BY id;
SELECT * FROM db_tp1.configuraciones_asistencia_periodo ORDER BY id;
SELECT * FROM db_tp1.evaluaciones ORDER BY id;
SELECT * FROM db_tp1.detalle_notas_evaluacion ORDER BY id;
SELECT * FROM db_tp1.notas_curso_periodo_evaluacion ORDER BY id;
SELECT * FROM db_tp1.asistencias_periodo_evaluacion ORDER BY id;
SELECT * FROM db_tp1.cargas_archivos ORDER BY id;
SELECT * FROM db_tp1.notas ORDER BY id;
SELECT * FROM db_tp1.asistencias ORDER BY id;
SELECT * FROM db_tp1.predicciones_riesgo_global ORDER BY id;
SELECT * FROM db_tp1.predicciones_riesgo_curso ORDER BY id;
SELECT * FROM db_tp1.alertas ORDER BY id;
SELECT * FROM db_tp1.recomendaciones ORDER BY id;
SELECT * FROM db_tp1.hallazgos_data_mining ORDER BY id;



- =========================================================
-- INSERTS BASE
-- INSERTS BASE + SEED OPERATIVO
-- =========================================================

-- =========================================================
-- USUARIOS Y DOCENTES DEMO
-- Credenciales referenciales:
-- admin / password
-- jperez / password
-- mgarcia / password
-- =========================================================

INSERT INTO db_tp1.usuarios (username, correo, password_hash, estado) VALUES
('admin', 'admin@colegio.edu.pe', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ACTIVO'),
('jperez', 'jperez@colegio.edu.pe', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ACTIVO'),
('mgarcia', 'mgarcia@colegio.edu.pe', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ACTIVO');

INSERT INTO db_tp1.usuarios_roles (usuario_id, rol_id)
SELECT u.id, r.id
FROM db_tp1.usuarios u
JOIN db_tp1.roles r ON
    (u.username = 'admin' AND r.nombre = 'ADMIN')
    OR (u.username = 'jperez' AND r.nombre IN ('DOCENTE', 'DOCENTE_TUTOR'))
    OR (u.username = 'mgarcia' AND r.nombre = 'DOCENTE');

INSERT INTO db_tp1.docentes (usuario_id, dni, nombres, apellidos, telefono, especialidad, estado) VALUES
((SELECT id FROM db_tp1.usuarios WHERE username = 'jperez'), '12345678', 'JUAN', 'PEREZ LOPEZ', '999111222', 'PRIMARIA', 'ACTIVO'),
((SELECT id FROM db_tp1.usuarios WHERE username = 'mgarcia'), '87654321', 'MARIA', 'GARCIA TORRES', '999333444', 'PRIMARIA', 'ACTIVO');

-- =========================================================
-- ESTRUCTURA ACADEMICA OPERATIVA 2026
-- =========================================================

INSERT INTO db_tp1.grados (nivel_id, nombre, orden, estado) VALUES
((SELECT id FROM db_tp1.niveles WHERE nombre = 'PRIMARIA'), '1RO PRIMARIA', 1, 'ACTIVO'),
((SELECT id FROM db_tp1.niveles WHERE nombre = 'PRIMARIA'), '2DO PRIMARIA', 2, 'ACTIVO'),
((SELECT id FROM db_tp1.niveles WHERE nombre = 'PRIMARIA'), '3RO PRIMARIA', 3, 'ACTIVO'),
((SELECT id FROM db_tp1.niveles WHERE nombre = 'PRIMARIA'), '4TO PRIMARIA', 4, 'ACTIVO'),
((SELECT id FROM db_tp1.niveles WHERE nombre = 'PRIMARIA'), '5TO PRIMARIA', 5, 'ACTIVO'),
((SELECT id FROM db_tp1.niveles WHERE nombre = 'PRIMARIA'), '6TO PRIMARIA', 6, 'ACTIVO');

INSERT INTO db_tp1.grados (nivel_id, nombre, orden, estado) VALUES
((SELECT id FROM db_tp1.niveles WHERE nombre = 'SECUNDARIA'), '1RO SECUNDARIA', 7, 'ACTIVO'),
((SELECT id FROM db_tp1.niveles WHERE nombre = 'SECUNDARIA'), '2DO SECUNDARIA', 8, 'ACTIVO'),
((SELECT id FROM db_tp1.niveles WHERE nombre = 'SECUNDARIA'), '3RO SECUNDARIA', 9, 'ACTIVO'),
((SELECT id FROM db_tp1.niveles WHERE nombre = 'SECUNDARIA'), '4TO SECUNDARIA', 10, 'ACTIVO'),
((SELECT id FROM db_tp1.niveles WHERE nombre = 'SECUNDARIA'), '5TO SECUNDARIA', 11, 'ACTIVO');

INSERT INTO db_tp1.periodos_academicos (nombre, anio, fecha_inicio, fecha_fin, tipo_periodo_evaluacion, estado) VALUES
('PERIODO 2026', 2026, DATE '2026-03-01', DATE '2026-12-20', 'BIMESTRAL', 'ACTIVO');

INSERT INTO db_tp1.periodos_evaluacion (periodo_academico_id, nombre, numero, fecha_inicio, fecha_fin, estado) VALUES
((SELECT id FROM db_tp1.periodos_academicos WHERE anio = 2026), 'BIMESTRE I', 1, DATE '2026-03-01', DATE '2026-04-30', 'ACTIVO'),
((SELECT id FROM db_tp1.periodos_academicos WHERE anio = 2026), 'BIMESTRE II', 2, DATE '2026-05-01', DATE '2026-06-30', 'ACTIVO'),
((SELECT id FROM db_tp1.periodos_academicos WHERE anio = 2026), 'BIMESTRE III', 3, DATE '2026-07-01', DATE '2026-09-15', 'ACTIVO'),
((SELECT id FROM db_tp1.periodos_academicos WHERE anio = 2026), 'BIMESTRE IV', 4, DATE '2026-09-16', DATE '2026-12-20', 'ACTIVO');

INSERT INTO db_tp1.secciones (grado_id, periodo_academico_id, nombre, capacidad, estado) VALUES
((SELECT id FROM db_tp1.grados WHERE nombre = '1RO PRIMARIA'), (SELECT id FROM db_tp1.periodos_academicos WHERE anio = 2026), 'A', 30, 'ACTIVO'),
((SELECT id FROM db_tp1.grados WHERE nombre = '6TO PRIMARIA'), (SELECT id FROM db_tp1.periodos_academicos WHERE anio = 2026), 'UNICA', 30, 'ACTIVO');

INSERT INTO db_tp1.cursos (nivel_id, nombre, descripcion, estado) VALUES
((SELECT id FROM db_tp1.niveles WHERE nombre = 'PRIMARIA'), 'ARITMETICA', 'Curso de matematica basica', 'ACTIVO'),
((SELECT id FROM db_tp1.niveles WHERE nombre = 'PRIMARIA'), 'RELIGION', 'Curso de formacion religiosa', 'ACTIVO'),
((SELECT id FROM db_tp1.niveles WHERE nombre = 'PRIMARIA'), 'COMUNICACION', 'Curso de comunicacion integral', 'ACTIVO');

INSERT INTO db_tp1.cursos_periodo_academico (periodo_academico_id, curso_id, estado)
SELECT pa.id, c.id, 'ACTIVO'
FROM db_tp1.periodos_academicos pa
JOIN db_tp1.cursos c ON c.nombre IN ('ARITMETICA', 'RELIGION', 'COMUNICACION')
WHERE pa.anio = 2026;

INSERT INTO db_tp1.docente_curso_seccion (docente_id, curso_id, seccion_id, periodo_academico_id, estado) VALUES
((SELECT id FROM db_tp1.docentes WHERE dni = '12345678'), (SELECT id FROM db_tp1.cursos WHERE nombre = 'ARITMETICA'), (SELECT id FROM db_tp1.secciones WHERE nombre = 'A' AND grado_id = (SELECT id FROM db_tp1.grados WHERE nombre = '1RO PRIMARIA')), (SELECT id FROM db_tp1.periodos_academicos WHERE anio = 2026), 'ACTIVO'),
((SELECT id FROM db_tp1.docentes WHERE dni = '12345678'), (SELECT id FROM db_tp1.cursos WHERE nombre = 'RELIGION'), (SELECT id FROM db_tp1.secciones WHERE nombre = 'A' AND grado_id = (SELECT id FROM db_tp1.grados WHERE nombre = '1RO PRIMARIA')), (SELECT id FROM db_tp1.periodos_academicos WHERE anio = 2026), 'ACTIVO'),
((SELECT id FROM db_tp1.docentes WHERE dni = '87654321'), (SELECT id FROM db_tp1.cursos WHERE nombre = 'COMUNICACION'), (SELECT id FROM db_tp1.secciones WHERE nombre = 'A' AND grado_id = (SELECT id FROM db_tp1.grados WHERE nombre = '1RO PRIMARIA')), (SELECT id FROM db_tp1.periodos_academicos WHERE anio = 2026), 'ACTIVO'),
((SELECT id FROM db_tp1.docentes WHERE dni = '87654321'), (SELECT id FROM db_tp1.cursos WHERE nombre = 'RELIGION'), (SELECT id FROM db_tp1.secciones WHERE nombre = 'UNICA' AND grado_id = (SELECT id FROM db_tp1.grados WHERE nombre = '6TO PRIMARIA')), (SELECT id FROM db_tp1.periodos_academicos WHERE anio = 2026), 'ACTIVO');

INSERT INTO db_tp1.tutorias (docente_id, seccion_id, periodo_academico_id, estado) VALUES
((SELECT id FROM db_tp1.docentes WHERE dni = '12345678'), (SELECT id FROM db_tp1.secciones WHERE nombre = 'A' AND grado_id = (SELECT id FROM db_tp1.grados WHERE nombre = '1RO PRIMARIA')), (SELECT id FROM db_tp1.periodos_academicos WHERE anio = 2026), 'ACTIVO'),
((SELECT id FROM db_tp1.docentes WHERE dni = '87654321'), (SELECT id FROM db_tp1.secciones WHERE nombre = 'UNICA' AND grado_id = (SELECT id FROM db_tp1.grados WHERE nombre = '6TO PRIMARIA')), (SELECT id FROM db_tp1.periodos_academicos WHERE anio = 2026), 'INACTIVO');



BEGIN;

ALTER TABLE db_tp1.usuarios
ADD COLUMN IF NOT EXISTS debe_cambiar_password BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE db_tp1.usuarios
SET debe_cambiar_password = FALSE
WHERE debe_cambiar_password IS NULL;

COMMIT;

UPDATE db_tp1.cursos
SET
  portada_color = CASE id
    WHEN 1 THEN '#F97316'
    WHEN 2 THEN '#0F766E'
    WHEN 3 THEN '#2563EB'
    ELSE portada_color
  END,
  portada_icono = CASE id
    WHEN 1 THEN 'fa-solid fa-calculator'
    WHEN 2 THEN 'fa-solid fa-book-open'
    WHEN 3 THEN 'fa-solid fa-language'
    ELSE portada_icono
  END,
  portada_imagen = CASE id
    WHEN 1 THEN 'assets/course-covers/cover-numbers.svg'
    WHEN 2 THEN 'assets/course-covers/cover-reading.svg'
    WHEN 3 THEN 'assets/course-covers/cover-geography.svg'
    ELSE portada_imagen
  END
WHERE id IN (1, 2, 3);


ALTER TABLE db_tp1.cursos
ADD COLUMN IF NOT EXISTS portada_color VARCHAR(30),
ADD COLUMN IF NOT EXISTS portada_icono VARCHAR(80);

ALTER TABLE db_tp1.cursos
ADD COLUMN IF NOT EXISTS portada_imagen VARCHAR(255);

INSERT INTO db_tp1.cursos (
    nivel_id,
    nombre,
    descripcion,
    portada_color,
    portada_icono,
    portada_imagen,
    estado,
    fecha_registro,
    fecha_modificacion
)
VALUES
(1, 'ALGEBRA', 'Curso de algebra', '#F97316', 'fa-solid fa-calculator', 'assets/course-covers/cover-numbers.svg', 'ACTIVO', NOW(), NOW()),
(1, 'GEOMETRIA', 'Curso de geometria', '#2563EB', 'fa-solid fa-shapes', 'assets/course-covers/cover-geography.svg', 'ACTIVO', NOW(), NOW()),
(1, 'RAZONAMIENTO MATEMATICO', 'Curso de razonamiento matematico', '#F59E0B', 'fa-solid fa-square-root-variable', 'assets/course-covers/cover-numbers.svg', 'ACTIVO', NOW(), NOW()),
(1, 'LENGUAJE', 'Curso de lenguaje', '#0F766E', 'fa-solid fa-book-open', 'assets/course-covers/cover-reading.svg', 'ACTIVO', NOW(), NOW()),
(1, 'RAZONAMIENTO VERBAL', 'Curso de razonamiento verbal', '#14B8A6', 'fa-solid fa-comments', 'assets/course-covers/cover-reading.svg', 'ACTIVO', NOW(), NOW()),
(1, 'PLAN LECTOR', 'Curso de plan lector', '#06B6D4', 'fa-solid fa-book', 'assets/course-covers/cover-reading.svg', 'ACTIVO', NOW(), NOW()),
(1, 'CALIGRAFIA', 'Curso de caligrafia', '#EC4899', 'fa-solid fa-pen-nib', 'assets/course-covers/cover-art.svg', 'ACTIVO', NOW(), NOW()),
(1, 'CIENCIA Y TECNOLOGIA', 'Curso de ciencia y tecnologia', '#4F46E5', 'fa-solid fa-microscope', 'assets/course-covers/cover-science.svg', 'ACTIVO', NOW(), NOW()),
(1, 'BIOLOGIA', 'Curso de biologia', '#22C55E', 'fa-solid fa-dna', 'assets/course-covers/cover-nature.svg', 'ACTIVO', NOW(), NOW()),
(1, 'QUIMICA', 'Curso de quimica', '#8B5CF6', 'fa-solid fa-flask', 'assets/course-covers/cover-science.svg', 'ACTIVO', NOW(), NOW()),
(1, 'FISICA', 'Curso de fisica', '#3B82F6', 'fa-solid fa-atom', 'assets/course-covers/cover-science.svg', 'ACTIVO', NOW(), NOW()),
(1, 'PERSONAL SOCIAL', 'Curso de personal social', '#0EA5E9', 'fa-solid fa-users', 'assets/course-covers/cover-geography.svg', 'ACTIVO', NOW(), NOW()),
(1, 'HISTORIA', 'Curso de historia', '#A16207', 'fa-solid fa-landmark', 'assets/course-covers/cover-geography.svg', 'ACTIVO', NOW(), NOW()),
(1, 'GEOGRAFIA', 'Curso de geografia', '#0284C7', 'fa-solid fa-globe', 'assets/course-covers/cover-geography.svg', 'ACTIVO', NOW(), NOW()),
(1, 'CIVICA', 'Curso de civica', '#64748B', 'fa-solid fa-scale-balanced', 'assets/course-covers/cover-geography.svg', 'ACTIVO', NOW(), NOW()),
(1, 'ORATORIA Y LIDERAZGO', 'Curso de oratoria y liderazgo', '#F97316', 'fa-solid fa-microphone-lines', 'assets/course-covers/cover-art.svg', 'ACTIVO', NOW(), NOW()),
(1, 'ORATORIA Y VALORES', 'Curso de oratoria y valores', '#E11D48', 'fa-solid fa-heart', 'assets/course-covers/cover-art.svg', 'ACTIVO', NOW(), NOW()),
(1, 'EDUCACION FISICA', 'Curso de educacion fisica', '#16A34A', 'fa-solid fa-dumbbell', 'assets/course-covers/cover-nature.svg', 'ACTIVO', NOW(), NOW()),
(1, 'INGLES', 'Curso de ingles', '#2563EB', 'fa-solid fa-language', 'assets/course-covers/cover-reading.svg', 'ACTIVO', NOW(), NOW()),
(1, 'TALLERES', 'Curso de talleres', '#D946EF', 'fa-solid fa-screwdriver-wrench', 'assets/course-covers/cover-art.svg', 'ACTIVO', NOW(), NOW()),

(2, 'ARITMETICA', 'Curso de aritmetica', '#F97316', 'fa-solid fa-calculator', 'assets/course-covers/cover-numbers.svg', 'ACTIVO', NOW(), NOW()),
(2, 'ALGEBRA', 'Curso de algebra', '#F59E0B', 'fa-solid fa-square-root-variable', 'assets/course-covers/cover-numbers.svg', 'ACTIVO', NOW(), NOW()),
(2, 'HABILIDAD MATEMATICA', 'Curso de habilidad matematica', '#EAB308', 'fa-solid fa-brain', 'assets/course-covers/cover-numbers.svg', 'ACTIVO', NOW(), NOW()),
(2, 'GEOMETRIA', 'Curso de geometria', '#2563EB', 'fa-solid fa-shapes', 'assets/course-covers/cover-geography.svg', 'ACTIVO', NOW(), NOW()),
(2, 'TRIGONOMETRIA', 'Curso de trigonometria', '#7C3AED', 'fa-solid fa-chart-line', 'assets/course-covers/cover-numbers.svg', 'ACTIVO', NOW(), NOW()),
(2, 'LENGUAJE', 'Curso de lenguaje', '#0F766E', 'fa-solid fa-book-open', 'assets/course-covers/cover-reading.svg', 'ACTIVO', NOW(), NOW()),
(2, 'HABILIDAD VERBAL', 'Curso de habilidad verbal', '#14B8A6', 'fa-solid fa-comments', 'assets/course-covers/cover-reading.svg', 'ACTIVO', NOW(), NOW()),
(2, 'LITERATURA', 'Curso de literatura', '#9333EA', 'fa-solid fa-feather-pointed', 'assets/course-covers/cover-reading.svg', 'ACTIVO', NOW(), NOW()),
(2, 'BIOLOGIA', 'Curso de biologia', '#22C55E', 'fa-solid fa-dna', 'assets/course-covers/cover-nature.svg', 'ACTIVO', NOW(), NOW()),
(2, 'FISICA', 'Curso de fisica', '#3B82F6', 'fa-solid fa-atom', 'assets/course-covers/cover-science.svg', 'ACTIVO', NOW(), NOW()),
(2, 'QUIMICA', 'Curso de quimica', '#8B5CF6', 'fa-solid fa-flask', 'assets/course-covers/cover-science.svg', 'ACTIVO', NOW(), NOW()),
(2, 'GEOGRAFIA', 'Curso de geografia', '#0284C7', 'fa-solid fa-globe', 'assets/course-covers/cover-geography.svg', 'ACTIVO', NOW(), NOW()),
(2, 'HISTORIA DEL PERU', 'Curso de historia del Peru', '#A16207', 'fa-solid fa-landmark', 'assets/course-covers/cover-geography.svg', 'ACTIVO', NOW(), NOW()),
(2, 'HISTORIA UNIVERSAL', 'Curso de historia universal', '#92400E', 'fa-solid fa-monument', 'assets/course-covers/cover-geography.svg', 'ACTIVO', NOW(), NOW()),
(2, 'FILOSOFIA / PSICOLOGIA', 'Curso integrado de filosofia y psicologia', '#6366F1', 'fa-solid fa-brain', 'assets/course-covers/cover-art.svg', 'ACTIVO', NOW(), NOW()),
(2, 'CIVICA / ECONOMIA', 'Curso integrado de civica y economia', '#64748B', 'fa-solid fa-scale-balanced', 'assets/course-covers/cover-geography.svg', 'ACTIVO', NOW(), NOW()),
(2, 'COMPUTACION', 'Curso de computacion', '#0F766E', 'fa-solid fa-laptop-code', 'assets/course-covers/cover-science.svg', 'ACTIVO', NOW(), NOW()),
(2, 'INGLES', 'Curso de ingles', '#2563EB', 'fa-solid fa-language', 'assets/course-covers/cover-reading.svg', 'ACTIVO', NOW(), NOW()),
(2, 'LIDERAZGO Y GESTION EMPRESARIAL', 'Curso de liderazgo y gestion empresarial', '#EA580C', 'fa-solid fa-briefcase', 'assets/course-covers/cover-art.svg', 'ACTIVO', NOW(), NOW()),
(2, 'EDUCACION FISICA', 'Curso de educacion fisica', '#16A34A', 'fa-solid fa-dumbbell', 'assets/course-covers/cover-nature.svg', 'ACTIVO', NOW(), NOW());