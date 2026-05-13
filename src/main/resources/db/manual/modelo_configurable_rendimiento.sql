-- =========================================================
-- SCRIPT MAESTRO COMPLETO
-- SISTEMA DE RENDIMIENTO ACADEMICO CONFIGURABLE
-- Base sugerida: db_rendimiento
-- Esquema: db_tp1
-- PostgreSQL
-- =========================================================

DROP SCHEMA IF EXISTS db_tp1 CASCADE;
CREATE SCHEMA db_tp1;
SET search_path TO db_tp1;

-- =========================================================
-- FUNCION GENERICA DE AUDITORIA
-- =========================================================

CREATE OR REPLACE FUNCTION actualizar_fecha_modificacion()
RETURNS TRIGGER AS $$
BEGIN
    NEW.fecha_modificacion = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- =========================================================
-- SEGURIDAD Y PERSONAS
-- =========================================================

CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(255),
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    rol_id BIGINT NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    correo VARCHAR(120) NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    ultimo_login TIMESTAMP,
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_usuarios_rol FOREIGN KEY (rol_id) REFERENCES roles(id)
);

CREATE TABLE docentes (
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
    CONSTRAINT fk_docentes_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

CREATE TABLE alumnos (
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
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW()
);

-- =========================================================
-- ESTRUCTURA ACADEMICA
-- =========================================================

CREATE TABLE niveles (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(255),
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE grados (
    id BIGSERIAL PRIMARY KEY,
    nivel_id BIGINT NOT NULL,
    nombre VARCHAR(50) NOT NULL,
    orden SMALLINT NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_grados_nivel FOREIGN KEY (nivel_id) REFERENCES niveles(id),
    CONSTRAINT uq_grados UNIQUE (nivel_id, nombre)
);

CREATE TABLE secciones (
    id BIGSERIAL PRIMARY KEY,
    grado_id BIGINT NOT NULL,
    nombre VARCHAR(20) NOT NULL,
    capacidad INTEGER,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_secciones_grado FOREIGN KEY (grado_id) REFERENCES grados(id),
    CONSTRAINT uq_secciones UNIQUE (grado_id, nombre)
);

CREATE TABLE periodos_academicos (
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
    CONSTRAINT chk_periodos_tipo_evaluacion CHECK (tipo_periodo_evaluacion IN ('BIMESTRAL', 'TRIMESTRAL', 'SEMESTRAL', 'ANUAL'))
);

CREATE TABLE periodos_evaluacion (
    id BIGSERIAL PRIMARY KEY,
    periodo_academico_id BIGINT NOT NULL,
    nombre VARCHAR(50) NOT NULL,
    numero SMALLINT NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_periodos_evaluacion_periodo FOREIGN KEY (periodo_academico_id) REFERENCES periodos_academicos(id),
    CONSTRAINT chk_periodos_evaluacion_numero CHECK (numero > 0),
    CONSTRAINT chk_periodos_evaluacion_fechas CHECK (fecha_fin >= fecha_inicio),
    CONSTRAINT uq_periodos_evaluacion UNIQUE (periodo_academico_id, numero)
);

CREATE TABLE cursos (
    id BIGSERIAL PRIMARY KEY,
    nivel_id BIGINT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_cursos_nivel FOREIGN KEY (nivel_id) REFERENCES niveles(id),
    CONSTRAINT uq_cursos UNIQUE (nivel_id, nombre)
);

CREATE TABLE matriculas (
    id BIGSERIAL PRIMARY KEY,
    alumno_id BIGINT NOT NULL,
    grado_id BIGINT NOT NULL,
    seccion_id BIGINT NOT NULL,
    periodo_academico_id BIGINT NOT NULL,
    fecha_matricula DATE NOT NULL DEFAULT CURRENT_DATE,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_matriculas_alumno FOREIGN KEY (alumno_id) REFERENCES alumnos(id),
    CONSTRAINT fk_matriculas_grado FOREIGN KEY (grado_id) REFERENCES grados(id),
    CONSTRAINT fk_matriculas_seccion FOREIGN KEY (seccion_id) REFERENCES secciones(id),
    CONSTRAINT fk_matriculas_periodo FOREIGN KEY (periodo_academico_id) REFERENCES periodos_academicos(id),
    CONSTRAINT uq_matriculas UNIQUE (alumno_id, periodo_academico_id)
);

CREATE TABLE docente_curso_seccion (
    id BIGSERIAL PRIMARY KEY,
    docente_id BIGINT NOT NULL,
    curso_id BIGINT NOT NULL,
    seccion_id BIGINT NOT NULL,
    periodo_academico_id BIGINT NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_dcs_docente FOREIGN KEY (docente_id) REFERENCES docentes(id),
    CONSTRAINT fk_dcs_curso FOREIGN KEY (curso_id) REFERENCES cursos(id),
    CONSTRAINT fk_dcs_seccion FOREIGN KEY (seccion_id) REFERENCES secciones(id),
    CONSTRAINT fk_dcs_periodo FOREIGN KEY (periodo_academico_id) REFERENCES periodos_academicos(id),
    CONSTRAINT uq_dcs UNIQUE (docente_id, curso_id, seccion_id, periodo_academico_id)
);

CREATE TABLE tutorias (
    id BIGSERIAL PRIMARY KEY,
    docente_id BIGINT NOT NULL,
    seccion_id BIGINT NOT NULL,
    periodo_academico_id BIGINT NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_tutorias_docente FOREIGN KEY (docente_id) REFERENCES docentes(id),
    CONSTRAINT fk_tutorias_seccion FOREIGN KEY (seccion_id) REFERENCES secciones(id),
    CONSTRAINT fk_tutorias_periodo FOREIGN KEY (periodo_academico_id) REFERENCES periodos_academicos(id),
    CONSTRAINT uq_tutorias UNIQUE (seccion_id, periodo_academico_id)
);

-- =========================================================
-- EVALUACION CONFIGURABLE
-- PROMEDIO ARITMETICO
-- =========================================================

CREATE TABLE tipos_evaluacion (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(255),
    orden SMALLINT NOT NULL DEFAULT 1,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE configuraciones_evaluacion (
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
    CONSTRAINT fk_conf_eval_periodo FOREIGN KEY (periodo_academico_id) REFERENCES periodos_academicos(id),
    CONSTRAINT fk_conf_eval_periodo_evaluacion FOREIGN KEY (periodo_evaluacion_id) REFERENCES periodos_evaluacion(id),
    CONSTRAINT fk_conf_eval_curso FOREIGN KEY (curso_id) REFERENCES cursos(id),
    CONSTRAINT fk_conf_eval_grado FOREIGN KEY (grado_id) REFERENCES grados(id),
    CONSTRAINT fk_conf_eval_tipo FOREIGN KEY (tipo_evaluacion_id) REFERENCES tipos_evaluacion(id),
    CONSTRAINT chk_conf_eval_cantidad CHECK (cantidad_evaluaciones > 0),
    CONSTRAINT uq_conf_eval UNIQUE (periodo_evaluacion_id, curso_id, grado_id, tipo_evaluacion_id)
);

CREATE TABLE evaluaciones (
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
    CONSTRAINT fk_evaluaciones_configuracion FOREIGN KEY (configuracion_evaluacion_id) REFERENCES configuraciones_evaluacion(id),
    CONSTRAINT fk_evaluaciones_dcs FOREIGN KEY (docente_curso_seccion_id) REFERENCES docente_curso_seccion(id),
    CONSTRAINT fk_evaluaciones_periodo_evaluacion FOREIGN KEY (periodo_evaluacion_id) REFERENCES periodos_evaluacion(id),
    CONSTRAINT fk_evaluaciones_tipo FOREIGN KEY (tipo_evaluacion_id) REFERENCES tipos_evaluacion(id),
    CONSTRAINT chk_evaluaciones_numero CHECK (numero_evaluacion > 0),
    CONSTRAINT uq_evaluaciones UNIQUE (docente_curso_seccion_id, periodo_evaluacion_id, tipo_evaluacion_id, numero_evaluacion)
);

CREATE TABLE detalle_notas_evaluacion (
    id BIGSERIAL PRIMARY KEY,
    evaluacion_id BIGINT NOT NULL,
    matricula_id BIGINT NOT NULL,
    nota NUMERIC(4,2) NOT NULL,
    observacion VARCHAR(255),
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_det_notas_evaluacion FOREIGN KEY (evaluacion_id) REFERENCES evaluaciones(id),
    CONSTRAINT fk_det_notas_matricula FOREIGN KEY (matricula_id) REFERENCES matriculas(id),
    CONSTRAINT chk_det_notas_rango CHECK (nota >= 0 AND nota <= 20),
    CONSTRAINT uq_det_notas UNIQUE (evaluacion_id, matricula_id)
);

-- =========================================================
-- CONSOLIDADOS
-- =========================================================

CREATE TABLE notas_curso_periodo_evaluacion (
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
    CONSTRAINT fk_notas_cb_matricula FOREIGN KEY (matricula_id) REFERENCES matriculas(id),
    CONSTRAINT fk_notas_cb_curso FOREIGN KEY (curso_id) REFERENCES cursos(id),
    CONSTRAINT fk_notas_cb_periodo_evaluacion FOREIGN KEY (periodo_evaluacion_id) REFERENCES periodos_evaluacion(id),
    CONSTRAINT chk_notas_cb_rango CHECK (promedio_curso >= 0 AND promedio_curso <= 20),
    CONSTRAINT uq_notas_cb UNIQUE (matricula_id, curso_id, periodo_evaluacion_id)
);

CREATE TABLE asistencias_periodo_evaluacion (
    id BIGSERIAL PRIMARY KEY,
    matricula_id BIGINT NOT NULL,
    periodo_evaluacion_id BIGINT NOT NULL,
    clases_programadas INTEGER NOT NULL,
    clases_asistidas INTEGER NOT NULL,
    observacion VARCHAR(255),
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_asist_b_matricula FOREIGN KEY (matricula_id) REFERENCES matriculas(id),
    CONSTRAINT fk_asist_b_periodo_evaluacion FOREIGN KEY (periodo_evaluacion_id) REFERENCES periodos_evaluacion(id),
    CONSTRAINT chk_asist_b_programadas CHECK (clases_programadas >= 0),
    CONSTRAINT chk_asist_b_asistidas CHECK (clases_asistidas >= 0),
    CONSTRAINT chk_asist_b_limite CHECK (clases_asistidas <= clases_programadas),
    CONSTRAINT uq_asist_b UNIQUE (matricula_id, periodo_evaluacion_id)
);

-- =========================================================
-- CARGAS OPCIONALES
-- =========================================================

CREATE TABLE cargas_archivos (
    id BIGSERIAL PRIMARY KEY,
    docente_id BIGINT NOT NULL,
    periodo_academico_id BIGINT NOT NULL,
    periodo_evaluacion_id BIGINT NOT NULL,
    seccion_id BIGINT NOT NULL,
    nombre_archivo VARCHAR(255) NOT NULL,
    tipo_carga VARCHAR(50) NOT NULL,
    total_filas INTEGER NOT NULL DEFAULT 0,
    filas_validas INTEGER NOT NULL DEFAULT 0,
    filas_error INTEGER NOT NULL DEFAULT 0,
    estado_proceso VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE',
    observacion VARCHAR(255),
    fecha_carga TIMESTAMP NOT NULL DEFAULT NOW(),
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_cargas_docente FOREIGN KEY (docente_id) REFERENCES docentes(id),
    CONSTRAINT fk_cargas_periodo FOREIGN KEY (periodo_academico_id) REFERENCES periodos_academicos(id),
    CONSTRAINT fk_cargas_periodo_evaluacion FOREIGN KEY (periodo_evaluacion_id) REFERENCES periodos_evaluacion(id),
    CONSTRAINT fk_cargas_seccion FOREIGN KEY (seccion_id) REFERENCES secciones(id)
);

-- =========================================================
-- PREDICCION Y ANALITICA
-- =========================================================

CREATE TABLE predicciones_riesgo_global (
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
    CONSTRAINT fk_pred_global_matricula FOREIGN KEY (matricula_id) REFERENCES matriculas(id),
    CONSTRAINT fk_pred_global_periodo_evaluacion FOREIGN KEY (periodo_evaluacion_id) REFERENCES periodos_evaluacion(id),
    CONSTRAINT fk_pred_global_carga FOREIGN KEY (carga_archivo_id) REFERENCES cargas_archivos(id),
    CONSTRAINT chk_pred_global_puntaje CHECK (puntaje_riesgo >= 0 AND puntaje_riesgo <= 100),
    CONSTRAINT chk_pred_global_nivel CHECK (nivel_riesgo IN ('BAJO', 'MEDIO', 'ALTO')),
    CONSTRAINT uq_pred_global UNIQUE (matricula_id, periodo_evaluacion_id)
);

CREATE TABLE predicciones_riesgo_curso (
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
    CONSTRAINT fk_pred_curso_matricula FOREIGN KEY (matricula_id) REFERENCES matriculas(id),
    CONSTRAINT fk_pred_curso_curso FOREIGN KEY (curso_id) REFERENCES cursos(id),
    CONSTRAINT fk_pred_curso_periodo_evaluacion FOREIGN KEY (periodo_evaluacion_id) REFERENCES periodos_evaluacion(id),
    CONSTRAINT fk_pred_curso_carga FOREIGN KEY (carga_archivo_id) REFERENCES cargas_archivos(id),
    CONSTRAINT chk_pred_curso_puntaje CHECK (puntaje_riesgo >= 0 AND puntaje_riesgo <= 100),
    CONSTRAINT chk_pred_curso_nivel CHECK (nivel_riesgo IN ('BAJO', 'MEDIO', 'ALTO')),
    CONSTRAINT uq_pred_curso UNIQUE (matricula_id, curso_id, periodo_evaluacion_id)
);

CREATE TABLE alertas (
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
    CONSTRAINT fk_alertas_matricula FOREIGN KEY (matricula_id) REFERENCES matriculas(id),
    CONSTRAINT fk_alertas_curso FOREIGN KEY (curso_id) REFERENCES cursos(id),
    CONSTRAINT fk_alertas_pred_global FOREIGN KEY (prediccion_global_id) REFERENCES predicciones_riesgo_global(id),
    CONSTRAINT fk_alertas_pred_curso FOREIGN KEY (prediccion_curso_id) REFERENCES predicciones_riesgo_curso(id),
    CONSTRAINT chk_alertas_nivel CHECK (nivel_riesgo IN ('BAJO', 'MEDIO', 'ALTO'))
);

CREATE TABLE recomendaciones (
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
    CONSTRAINT fk_recom_matricula FOREIGN KEY (matricula_id) REFERENCES matriculas(id),
    CONSTRAINT fk_recom_curso FOREIGN KEY (curso_id) REFERENCES cursos(id),
    CONSTRAINT fk_recom_pred_global FOREIGN KEY (prediccion_global_id) REFERENCES predicciones_riesgo_global(id),
    CONSTRAINT fk_recom_pred_curso FOREIGN KEY (prediccion_curso_id) REFERENCES predicciones_riesgo_curso(id),
    CONSTRAINT chk_recom_fuente CHECK (fuente IN ('REGLA', 'MODELO_ML', 'MANUAL'))
);

CREATE TABLE hallazgos_data_mining (
    id BIGSERIAL PRIMARY KEY,
    periodo_academico_id BIGINT NOT NULL,
    periodo_evaluacion_id BIGINT,
    curso_id BIGINT,
    tipo VARCHAR(50) NOT NULL,
    titulo VARCHAR(150) NOT NULL,
    descripcion TEXT NOT NULL,
    resultado JSONB,
    fecha_generacion TIMESTAMP NOT NULL DEFAULT NOW(),
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_hallazgos_periodo FOREIGN KEY (periodo_academico_id) REFERENCES periodos_academicos(id),
    CONSTRAINT fk_hallazgos_periodo_evaluacion FOREIGN KEY (periodo_evaluacion_id) REFERENCES periodos_evaluacion(id),
    CONSTRAINT fk_hallazgos_curso FOREIGN KEY (curso_id) REFERENCES cursos(id)
);

-- =========================================================
-- INDICES
-- =========================================================

CREATE INDEX idx_usuarios_rol ON usuarios(rol_id);
CREATE INDEX idx_docentes_usuario ON docentes(usuario_id);
CREATE INDEX idx_grados_nivel ON grados(nivel_id);
CREATE INDEX idx_secciones_grado ON secciones(grado_id);
CREATE INDEX idx_cursos_nivel ON cursos(nivel_id);
CREATE INDEX idx_matriculas_periodo ON matriculas(periodo_academico_id);
CREATE INDEX idx_matriculas_seccion ON matriculas(seccion_id);
CREATE INDEX idx_dcs_docente ON docente_curso_seccion(docente_id);
CREATE INDEX idx_dcs_periodo ON docente_curso_seccion(periodo_academico_id);
CREATE INDEX idx_tutorias_docente ON tutorias(docente_id);
CREATE INDEX idx_conf_eval_periodo_evaluacion_curso ON configuraciones_evaluacion(periodo_evaluacion_id, curso_id);
CREATE INDEX idx_evaluaciones_dcs_periodo_evaluacion ON evaluaciones(docente_curso_seccion_id, periodo_evaluacion_id);
CREATE INDEX idx_detalle_eval_matricula ON detalle_notas_evaluacion(matricula_id);
CREATE INDEX idx_notas_cb_periodo_evaluacion ON notas_curso_periodo_evaluacion(periodo_evaluacion_id);
CREATE INDEX idx_notas_cb_matricula ON notas_curso_periodo_evaluacion(matricula_id);
CREATE INDEX idx_asist_b_periodo_evaluacion ON asistencias_periodo_evaluacion(periodo_evaluacion_id);
CREATE INDEX idx_pred_global_periodo_evaluacion ON predicciones_riesgo_global(periodo_evaluacion_id);
CREATE INDEX idx_pred_curso_periodo_evaluacion ON predicciones_riesgo_curso(periodo_evaluacion_id);
CREATE INDEX idx_alertas_matricula ON alertas(matricula_id);
CREATE INDEX idx_recom_matricula ON recomendaciones(matricula_id);

-- =========================================================
-- TRIGGERS
-- =========================================================

CREATE TRIGGER trg_roles_mod
BEFORE UPDATE ON roles
FOR EACH ROW EXECUTE FUNCTION actualizar_fecha_modificacion();

CREATE TRIGGER trg_usuarios_mod
BEFORE UPDATE ON usuarios
FOR EACH ROW EXECUTE FUNCTION actualizar_fecha_modificacion();

CREATE TRIGGER trg_docentes_mod
BEFORE UPDATE ON docentes
FOR EACH ROW EXECUTE FUNCTION actualizar_fecha_modificacion();

CREATE TRIGGER trg_alumnos_mod
BEFORE UPDATE ON alumnos
FOR EACH ROW EXECUTE FUNCTION actualizar_fecha_modificacion();

CREATE TRIGGER trg_niveles_mod
BEFORE UPDATE ON niveles
FOR EACH ROW EXECUTE FUNCTION actualizar_fecha_modificacion();

CREATE TRIGGER trg_grados_mod
BEFORE UPDATE ON grados
FOR EACH ROW EXECUTE FUNCTION actualizar_fecha_modificacion();

CREATE TRIGGER trg_secciones_mod
BEFORE UPDATE ON secciones
FOR EACH ROW EXECUTE FUNCTION actualizar_fecha_modificacion();

CREATE TRIGGER trg_periodos_mod
BEFORE UPDATE ON periodos_academicos
FOR EACH ROW EXECUTE FUNCTION actualizar_fecha_modificacion();

CREATE TRIGGER trg_periodos_evaluacion_mod
BEFORE UPDATE ON periodos_evaluacion
FOR EACH ROW EXECUTE FUNCTION actualizar_fecha_modificacion();

CREATE TRIGGER trg_cursos_mod
BEFORE UPDATE ON cursos
FOR EACH ROW EXECUTE FUNCTION actualizar_fecha_modificacion();

CREATE TRIGGER trg_matriculas_mod
BEFORE UPDATE ON matriculas
FOR EACH ROW EXECUTE FUNCTION actualizar_fecha_modificacion();

CREATE TRIGGER trg_dcs_mod
BEFORE UPDATE ON docente_curso_seccion
FOR EACH ROW EXECUTE FUNCTION actualizar_fecha_modificacion();

CREATE TRIGGER trg_tutorias_mod
BEFORE UPDATE ON tutorias
FOR EACH ROW EXECUTE FUNCTION actualizar_fecha_modificacion();

CREATE TRIGGER trg_tipos_eval_mod
BEFORE UPDATE ON tipos_evaluacion
FOR EACH ROW EXECUTE FUNCTION actualizar_fecha_modificacion();

CREATE TRIGGER trg_conf_eval_mod
BEFORE UPDATE ON configuraciones_evaluacion
FOR EACH ROW EXECUTE FUNCTION actualizar_fecha_modificacion();

CREATE TRIGGER trg_evaluaciones_mod
BEFORE UPDATE ON evaluaciones
FOR EACH ROW EXECUTE FUNCTION actualizar_fecha_modificacion();

CREATE TRIGGER trg_det_notas_mod
BEFORE UPDATE ON detalle_notas_evaluacion
FOR EACH ROW EXECUTE FUNCTION actualizar_fecha_modificacion();

CREATE TRIGGER trg_notas_cb_mod
BEFORE UPDATE ON notas_curso_periodo_evaluacion
FOR EACH ROW EXECUTE FUNCTION actualizar_fecha_modificacion();

CREATE TRIGGER trg_asist_b_mod
BEFORE UPDATE ON asistencias_periodo_evaluacion
FOR EACH ROW EXECUTE FUNCTION actualizar_fecha_modificacion();

CREATE TRIGGER trg_cargas_mod
BEFORE UPDATE ON cargas_archivos
FOR EACH ROW EXECUTE FUNCTION actualizar_fecha_modificacion();

CREATE TRIGGER trg_pred_global_mod
BEFORE UPDATE ON predicciones_riesgo_global
FOR EACH ROW EXECUTE FUNCTION actualizar_fecha_modificacion();

CREATE TRIGGER trg_pred_curso_mod
BEFORE UPDATE ON predicciones_riesgo_curso
FOR EACH ROW EXECUTE FUNCTION actualizar_fecha_modificacion();

CREATE TRIGGER trg_alertas_mod
BEFORE UPDATE ON alertas
FOR EACH ROW EXECUTE FUNCTION actualizar_fecha_modificacion();

CREATE TRIGGER trg_recom_mod
BEFORE UPDATE ON recomendaciones
FOR EACH ROW EXECUTE FUNCTION actualizar_fecha_modificacion();

CREATE TRIGGER trg_hallazgos_mod
BEFORE UPDATE ON hallazgos_data_mining
FOR EACH ROW EXECUTE FUNCTION actualizar_fecha_modificacion();

-- =========================================================
-- VISTAS
-- =========================================================

CREATE OR REPLACE VIEW vw_asistencia_porcentaje AS
SELECT
    ab.id,
    ab.matricula_id,
    ab.periodo_evaluacion_id,
    ab.clases_programadas,
    ab.clases_asistidas,
    CASE
        WHEN ab.clases_programadas = 0 THEN 0
        ELSE ROUND((ab.clases_asistidas::numeric / ab.clases_programadas::numeric) * 100, 2)
    END AS porcentaje_asistencia
FROM asistencias_periodo_evaluacion ab;

CREATE OR REPLACE VIEW vw_predicciones_globales_legibles AS
SELECT
    prg.id,
    a.codigo AS codigo_alumno,
    a.nombres,
    a.apellidos,
    n.nombre AS nivel,
    g.nombre AS grado,
    s.nombre AS seccion,
    b.nombre AS periodo_evaluacion,
    prg.puntaje_riesgo,
    prg.nivel_riesgo,
    prg.modelo_version,
    prg.fecha_prediccion
FROM predicciones_riesgo_global prg
JOIN matriculas m ON m.id = prg.matricula_id
JOIN alumnos a ON a.id = m.alumno_id
JOIN grados g ON g.id = m.grado_id
JOIN niveles n ON n.id = g.nivel_id
JOIN secciones s ON s.id = m.seccion_id
JOIN periodos_evaluacion b ON b.id = prg.periodo_evaluacion_id;

CREATE OR REPLACE VIEW vw_docente_asignaciones AS
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
FROM docente_curso_seccion dcs
JOIN docentes d ON d.id = dcs.docente_id
JOIN cursos c ON c.id = dcs.curso_id
JOIN secciones s ON s.id = dcs.seccion_id
JOIN grados g ON g.id = s.grado_id
JOIN periodos_academicos pa ON pa.id = dcs.periodo_academico_id;

-- =========================================================
-- INSERTS MINIMOS
-- =========================================================

INSERT INTO roles (nombre, descripcion) VALUES
('ADMIN', 'Administrador del sistema'),
('DOCENTE', 'Docente de curso'),
('DOCENTE_TUTOR', 'Docente tutor de seccion')
ON CONFLICT (nombre) DO NOTHING;

INSERT INTO niveles (nombre, descripcion) VALUES
('PRIMARIA', 'Nivel primaria'),
('SECUNDARIA', 'Nivel secundaria')
ON CONFLICT (nombre) DO NOTHING;

INSERT INTO tipos_evaluacion (nombre, descripcion, orden) VALUES
('EXAMEN_DIARIO', 'Evaluaciones cortas o controles diarios', 1),
('REVISION_CUADERNO', 'Revision de cuaderno', 2),
('REVISION_LIBRO', 'Revision de libro', 3),
('TAREA_TRABAJO', 'Tareas y trabajos', 4),
('EXPOSICION_PARTICIPACION', 'Exposicion y participacion', 5),
('EXAMEN', 'Evaluacion formal', 6)
ON CONFLICT (nombre) DO NOTHING;

-- =========================================================
-- FIN
-- =========================================================


CREATE TABLE db_tp1.usuarios_roles (
    usuario_id BIGINT NOT NULL,
    rol_id BIGINT NOT NULL,
    PRIMARY KEY (usuario_id, rol_id),
    CONSTRAINT fk_usuarios_roles_usuario
        FOREIGN KEY (usuario_id) REFERENCES db_tp1.usuarios(id) ON DELETE CASCADE,
    CONSTRAINT fk_usuarios_roles_rol
        FOREIGN KEY (rol_id) REFERENCES db_tp1.roles(id) ON DELETE CASCADE
);

INSERT INTO db_tp1.usuarios_roles (usuario_id, rol_id)
SELECT id, rol_id
FROM db_tp1.usuarios
WHERE rol_id IS NOT NULL;

SELECT conname
FROM pg_constraint
WHERE conrelid = 'db_tp1.usuarios'::regclass;

ALTER TABLE db_tp1.usuarios
DROP CONSTRAINT fk_usuarios_rol;

ALTER TABLE db_tp1.usuarios
DROP COLUMN rol_id;

SELECT *
FROM db_tp1.usuarios_roles
ORDER BY usuario_id, rol_id;


INSERT INTO db_tp1.usuarios (username, correo, password_hash, estado, fecha_registro, fecha_modificacion)
VALUES
('admin', 'admin@colegio.edu.pe', '$2a$10$ADMIN_HASH_AQUI', 'ACTIVO', NOW(), NOW()),
('jperez', 'juan.perez@colegio.edu.pe', '$2a$10$DOCENTE_HASH_AQUI', 'ACTIVO', NOW(), NOW()),
('mlopez', 'maria.lopez@colegio.edu.pe', '$2a$10$TUTOR_HASH_AQUI', 'ACTIVO', NOW(), NOW()),
('director', 'director@colegio.edu.pe', '$2a$10$DIRECTOR_HASH_AQUI', 'ACTIVO', NOW(), NOW());


INSERT INTO db_tp1.usuarios_roles (usuario_id, rol_id)
SELECT u.id, r.id
FROM db_tp1.usuarios u
JOIN db_tp1.roles r ON r.nombre = 'ADMIN'
WHERE u.username = 'admin';

INSERT INTO db_tp1.usuarios_roles (usuario_id, rol_id)
SELECT u.id, r.id
FROM db_tp1.usuarios u
JOIN db_tp1.roles r ON r.nombre = 'DOCENTE'
WHERE u.username = 'jperez';

INSERT INTO db_tp1.usuarios_roles (usuario_id, rol_id)
SELECT u.id, r.id
FROM db_tp1.usuarios u
JOIN db_tp1.roles r ON r.nombre IN ('DOCENTE', 'DOCENTE_TUTOR')
WHERE u.username = 'mlopez';

INSERT INTO db_tp1.usuarios_roles (usuario_id, rol_id)
SELECT u.id, r.id
FROM db_tp1.usuarios u
JOIN db_tp1.roles r ON r.nombre IN ('ADMIN', 'DOCENTE')
WHERE u.username = 'director';



SELECT * FROM db_tp1.docentes

SELECT * FROM db_tp1.docente_curso_seccion

SELECT * FROM db_tp1.periodos_academicos
SELECT * FROM db_tp1.periodos_evaluacion
SELECT * FROM db_tp1.secciones
SELECT * FROM db_tp1.notas
SELECT * FROM db_tp1.asistencias
SELECT * FROM db_tp1.matriculas


SELECT * FROM db_tp1.roles

SELECT * FROM db_tp1.grados


SELECT * FROM db_tp1.niveles

SELECT * FROM db_tp1.cursos
SELECT * FROM db_tp1.usuarios

SELECT * FROM db_tp1.usuarios_roles

SELECT * FROM db_tp1.alumnos

SELECT * FROM db_tp1.predicciones_riesgo


SELECT * FROM db_tp1.cargas_archivos



INSERT INTO db_tp1.cursos (nivel_id, nombre, descripcion, estado, fecha_registro, fecha_modificacion)
VALUES
(1, 'ARITMETICA', 'Curso de aritmetica', 'ACTIVO', NOW(), NOW()),
(1, 'ALGEBRA', 'Curso de algebra', 'ACTIVO', NOW(), NOW()),
(1, 'GEOMETRIA', 'Curso de geometria', 'ACTIVO', NOW(), NOW()),
(1, 'RAZONAMIENTO MATEMATICO', 'Curso de razonamiento matematico', 'ACTIVO', NOW(), NOW()),
(1, 'COMUNICACION', 'Curso de comunicacion', 'ACTIVO', NOW(), NOW()),
(1, 'LENGUAJE', 'Curso de lenguaje', 'ACTIVO', NOW(), NOW()),
(1, 'RAZONAMIENTO VERBAL', 'Curso de razonamiento verbal', 'ACTIVO', NOW(), NOW()),
(1, 'PLAN LECTOR', 'Curso de plan lector', 'ACTIVO', NOW(), NOW()),
(1, 'CALIGRAFIA', 'Curso de caligrafia', 'ACTIVO', NOW(), NOW()),
(1, 'CIENCIA Y TECNOLOGIA', 'Curso de ciencia y tecnologia', 'ACTIVO', NOW(), NOW()),
(1, 'BIOLOGIA', 'Curso de biologia', 'ACTIVO', NOW(), NOW()),
(1, 'QUIMICA', 'Curso de quimica', 'ACTIVO', NOW(), NOW()),
(1, 'FISICA', 'Curso de fisica', 'ACTIVO', NOW(), NOW()),
(1, 'PERSONAL SOCIAL', 'Curso de personal social', 'ACTIVO', NOW(), NOW()),
(1, 'HISTORIA', 'Curso de historia', 'ACTIVO', NOW(), NOW()),
(1, 'GEOGRAFIA', 'Curso de geografia', 'ACTIVO', NOW(), NOW()),
(1, 'CIVICA', 'Curso de civica', 'ACTIVO', NOW(), NOW()),
(1, 'RELIGION', 'Curso de religion', 'ACTIVO', NOW(), NOW()),
(1, 'ORATORIA Y LIDERAZGO', 'Curso de oratoria y liderazgo', 'ACTIVO', NOW(), NOW()),
(1, 'ORATORIA Y VALORES', 'Curso de oratoria y valores', 'ACTIVO', NOW(), NOW()),
(1, 'EDUCACION FISICA', 'Curso de educacion fisica', 'ACTIVO', NOW(), NOW()),
(1, 'INGLES', 'Curso de ingles', 'ACTIVO', NOW(), NOW()),
(1, 'COMPUTACION', 'Curso de computacion', 'ACTIVO', NOW(), NOW()),
(1, 'TALLERES', 'Curso de talleres', 'ACTIVO', NOW(), NOW()),

(2, 'ARITMETICA', 'Curso de aritmetica', 'ACTIVO', NOW(), NOW()),
(2, 'ALGEBRA', 'Curso de algebra', 'ACTIVO', NOW(), NOW()),
(2, 'HABILIDAD MATEMATICA', 'Curso de habilidad matematica', 'ACTIVO', NOW(), NOW()),
(2, 'GEOMETRIA', 'Curso de geometria', 'ACTIVO', NOW(), NOW()),
(2, 'TRIGONOMETRIA', 'Curso de trigonometria', 'ACTIVO', NOW(), NOW()),
(2, 'LENGUAJE', 'Curso de lenguaje', 'ACTIVO', NOW(), NOW()),
(2, 'HABILIDAD VERBAL', 'Curso de habilidad verbal', 'ACTIVO', NOW(), NOW()),
(2, 'LITERATURA', 'Curso de literatura', 'ACTIVO', NOW(), NOW()),
(2, 'BIOLOGIA', 'Curso de biologia', 'ACTIVO', NOW(), NOW()),
(2, 'FISICA', 'Curso de fisica', 'ACTIVO', NOW(), NOW()),
(2, 'QUIMICA', 'Curso de quimica', 'ACTIVO', NOW(), NOW()),
(2, 'GEOGRAFIA', 'Curso de geografia', 'ACTIVO', NOW(), NOW()),
(2, 'HISTORIA DEL PERU', 'Curso de historia del Peru', 'ACTIVO', NOW(), NOW()),
(2, 'HISTORIA UNIVERSAL', 'Curso de historia universal', 'ACTIVO', NOW(), NOW()),
(2, 'FILOSOFIA / PSICOLOGIA', 'Curso integrado de filosofia y psicologia', 'ACTIVO', NOW(), NOW()),
(2, 'CIVICA / ECONOMIA', 'Curso integrado de civica y economia', 'ACTIVO', NOW(), NOW()),
(2, 'COMPUTACION', 'Curso de computacion', 'ACTIVO', NOW(), NOW()),
(2, 'INGLES', 'Curso de ingles', 'ACTIVO', NOW(), NOW()),
(2, 'LIDERAZGO Y GESTION EMPRESARIAL', 'Curso de liderazgo y gestion empresarial', 'ACTIVO', NOW(), NOW()),
(2, 'EDUCACION FISICA', 'Curso de educacion fisica', 'ACTIVO', NOW(), NOW());


INSERT INTO db_tp1.docentes
(usuario_id, dni, nombres, apellidos, telefono, especialidad, estado, fecha_registro, fecha_modificacion)
VALUES
((SELECT id FROM db_tp1.usuarios WHERE username = 'jperez'),   '73451268', 'JUAN',   'PEREZ LOPEZ',     '987451236', 'MATEMATICA',   'ACTIVO', NOW(), NOW()),
((SELECT id FROM db_tp1.usuarios WHERE username = 'mlopez'),   '71896543', 'MARIA',  'LOPEZ GARCIA',    '986741255', 'COMUNICACION', 'ACTIVO', NOW(), NOW()),
((SELECT id FROM db_tp1.usuarios WHERE username = 'director'), '70123456', 'CARLOS', 'RAMIREZ TORRES',  '985112233', 'DIRECCION',    'ACTIVO', NOW(), NOW());

INSERT INTO db_tp1.usuarios
(username, correo, password_hash, estado, ultimo_login, fecha_registro, fecha_modificacion)
VALUES
('arojas',   'ana.rojas@colegio.edu.pe',     '$2a$12$eyeUHAm5GCTkTQNJ/zklWO23UDrmyjvQ0kfN8o73rWwgaTiZMVf4C', 'ACTIVO', NULL, NOW(), NOW()),
('lcastro',  'luis.castro@colegio.edu.pe',   '$2a$12$eyeUHAm5GCTkTQNJ/zklWO23UDrmyjvQ0kfN8o73rWwgaTiZMVf4C', 'ACTIVO', NULL, NOW(), NOW()),
('rquiroz',  'rosa.quiroz@colegio.edu.pe',   '$2a$12$eyeUHAm5GCTkTQNJ/zklWO23UDrmyjvQ0kfN8o73rWwgaTiZMVf4C', 'ACTIVO', NULL, NOW(), NOW()),
('hvega',    'hugo.vega@colegio.edu.pe',     '$2a$12$eyeUHAm5GCTkTQNJ/zklWO23UDrmyjvQ0kfN8o73rWwgaTiZMVf4C', 'ACTIVO', NULL, NOW(), NOW()),
('csalazar', 'carla.salazar@colegio.edu.pe', '$2a$12$eyeUHAm5GCTkTQNJ/zklWO23UDrmyjvQ0kfN8o73rWwgaTiZMVf4C', 'ACTIVO', NULL, NOW(), NOW()),
('eparedes', 'elena.paredes@colegio.edu.pe', '$2a$12$eyeUHAm5GCTkTQNJ/zklWO23UDrmyjvQ0kfN8o73rWwgaTiZMVf4C', 'ACTIVO', NULL, NOW(), NOW());


INSERT INTO db_tp1.usuarios_roles (usuario_id, rol_id)
SELECT u.id, 2
FROM db_tp1.usuarios u
WHERE u.username IN ('arojas', 'lcastro', 'rquiroz', 'hvega', 'csalazar', 'eparedes');

INSERT INTO db_tp1.docentes
(usuario_id, dni, nombres, apellidos, telefono, especialidad, estado, fecha_registro, fecha_modificacion)
VALUES
((SELECT id FROM db_tp1.usuarios WHERE username = 'arojas'),   '74561234', 'ANA',   'ROJAS MENDOZA',  '987111222', 'INGLES',               'ACTIVO', NOW(), NOW()),
((SELECT id FROM db_tp1.usuarios WHERE username = 'lcastro'),  '75672345', 'LUIS',  'CASTRO VARGAS',  '987222333', 'CIENCIA Y TECNOLOGIA', 'ACTIVO', NOW(), NOW()),
((SELECT id FROM db_tp1.usuarios WHERE username = 'rquiroz'),  '76783456', 'ROSA',  'QUIROZ FLORES',  '987333444', 'PERSONAL SOCIAL',      'ACTIVO', NOW(), NOW()),
((SELECT id FROM db_tp1.usuarios WHERE username = 'hvega'),    '77894567', 'HUGO',  'VEGA SOTO',      '987444555', 'EDUCACION FISICA',     'ACTIVO', NOW(), NOW()),
((SELECT id FROM db_tp1.usuarios WHERE username = 'csalazar'), '78905678', 'CARLA', 'SALAZAR RUIZ',   '987555666', 'COMPUTACION',          'ACTIVO', NOW(), NOW()),
((SELECT id FROM db_tp1.usuarios WHERE username = 'eparedes'), '79016789', 'ELENA', 'PAREDES HUAMAN', '987666777', 'RELIGION',             'ACTIVO', NOW(), NOW());


SELECT
    d.id,
    u.username,
    d.dni,
    d.nombres,
    d.apellidos,
    d.especialidad,
    d.estado
FROM db_tp1.docentes d
LEFT JOIN db_tp1.usuarios u ON u.id = d.usuario_id
ORDER BY d.apellidos, d.nombres;


INSERT INTO db_tp1.grados (nivel_id, nombre, orden, estado, fecha_registro, fecha_modificacion)
VALUES
(1, '1RO PRIMARIA', 1, 'ACTIVO', NOW(), NOW()),
(1, '2DO PRIMARIA', 2, 'ACTIVO', NOW(), NOW()),
(1, '3RO PRIMARIA', 3, 'ACTIVO', NOW(), NOW()),
(1, '4TO PRIMARIA', 4, 'ACTIVO', NOW(), NOW()),
(1, '5TO PRIMARIA', 5, 'ACTIVO', NOW(), NOW()),
(1, '6TO PRIMARIA', 6, 'ACTIVO', NOW(), NOW()),

(2, '1RO SECUNDARIA', 1, 'ACTIVO', NOW(), NOW()),
(2, '2DO SECUNDARIA', 2, 'ACTIVO', NOW(), NOW()),
(2, '3RO SECUNDARIA', 3, 'ACTIVO', NOW(), NOW()),
(2, '4TO SECUNDARIA', 4, 'ACTIVO', NOW(), NOW()),
(2, '5TO SECUNDARIA', 5, 'ACTIVO', NOW(), NOW());



ALTER TABLE db_tp1.secciones
ADD COLUMN periodo_academico_id BIGINT;

ALTER TABLE db_tp1.secciones
ADD CONSTRAINT fk_secciones_periodo
FOREIGN KEY (periodo_academico_id)
REFERENCES db_tp1.periodos_academicos(id);


UPDATE db_tp1.secciones
SET periodo_academico_id = (
  SELECT id
  FROM db_tp1.periodos_academicos
  WHERE anio = 2026
)
WHERE periodo_academico_id IS NULL;


ALTER TABLE db_tp1.secciones
ALTER COLUMN periodo_academico_id SET NOT NULL;


ALTER TABLE db_tp1.secciones
DROP CONSTRAINT uq_secciones;


ALTER TABLE db_tp1.secciones
ADD CONSTRAINT uq_secciones_periodo
UNIQUE (periodo_academico_id, grado_id, nombre);


INSERT INTO db_tp1.secciones
(grado_id, periodo_academico_id, nombre, capacidad, estado, fecha_registro, fecha_modificacion)
VALUES
((SELECT id FROM db_tp1.grados WHERE nombre = '1RO PRIMARIA' AND nivel_id = 1), (SELECT id FROM db_tp1.periodos_academicos WHERE anio = 2025), 'A', 30, 'ACTIVO', NOW(), NOW()),
((SELECT id FROM db_tp1.grados WHERE nombre = '1RO PRIMARIA' AND nivel_id = 1), (SELECT id FROM db_tp1.periodos_academicos WHERE anio = 2025), 'B', 30, 'ACTIVO', NOW(), NOW()),

((SELECT id FROM db_tp1.grados WHERE nombre = '2DO PRIMARIA' AND nivel_id = 1), (SELECT id FROM db_tp1.periodos_academicos WHERE anio = 2025), 'A', 30, 'ACTIVO', NOW(), NOW()),
((SELECT id FROM db_tp1.grados WHERE nombre = '2DO PRIMARIA' AND nivel_id = 1), (SELECT id FROM db_tp1.periodos_academicos WHERE anio = 2025), 'B', 30, 'ACTIVO', NOW(), NOW()),

((SELECT id FROM db_tp1.grados WHERE nombre = '3RO PRIMARIA' AND nivel_id = 1), (SELECT id FROM db_tp1.periodos_academicos WHERE anio = 2025), 'UNICA', 28, 'ACTIVO', NOW(), NOW()),
((SELECT id FROM db_tp1.grados WHERE nombre = '4TO PRIMARIA' AND nivel_id = 1), (SELECT id FROM db_tp1.periodos_academicos WHERE anio = 2025), 'UNICA', 28, 'ACTIVO', NOW(), NOW()),

((SELECT id FROM db_tp1.grados WHERE nombre = '5TO PRIMARIA' AND nivel_id = 1), (SELECT id FROM db_tp1.periodos_academicos WHERE anio = 2025), 'A', 30, 'ACTIVO', NOW(), NOW()),
((SELECT id FROM db_tp1.grados WHERE nombre = '5TO PRIMARIA' AND nivel_id = 1), (SELECT id FROM db_tp1.periodos_academicos WHERE anio = 2025), 'B', 30, 'ACTIVO', NOW(), NOW()),

((SELECT id FROM db_tp1.grados WHERE nombre = '6TO PRIMARIA' AND nivel_id = 1), (SELECT id FROM db_tp1.periodos_academicos WHERE anio = 2025), 'UNICA', 26, 'ACTIVO', NOW(), NOW()),

((SELECT id FROM db_tp1.grados WHERE nombre = '1RO SECUNDARIA' AND nivel_id = 2), (SELECT id FROM db_tp1.periodos_academicos WHERE anio = 2025), 'A', 32, 'ACTIVO', NOW(), NOW()),
((SELECT id FROM db_tp1.grados WHERE nombre = '1RO SECUNDARIA' AND nivel_id = 2), (SELECT id FROM db_tp1.periodos_academicos WHERE anio = 2025), 'B', 32, 'ACTIVO', NOW(), NOW()),

((SELECT id FROM db_tp1.grados WHERE nombre = '2DO SECUNDARIA' AND nivel_id = 2), (SELECT id FROM db_tp1.periodos_academicos WHERE anio = 2025), 'A', 32, 'ACTIVO', NOW(), NOW()),
((SELECT id FROM db_tp1.grados WHERE nombre = '2DO SECUNDARIA' AND nivel_id = 2), (SELECT id FROM db_tp1.periodos_academicos WHERE anio = 2025), 'B', 32, 'ACTIVO', NOW(), NOW()),

((SELECT id FROM db_tp1.grados WHERE nombre = '3RO SECUNDARIA' AND nivel_id = 2), (SELECT id FROM db_tp1.periodos_academicos WHERE anio = 2025), 'UNICA', 30, 'ACTIVO', NOW(), NOW()),

((SELECT id FROM db_tp1.grados WHERE nombre = '4TO SECUNDARIA' AND nivel_id = 2), (SELECT id FROM db_tp1.periodos_academicos WHERE anio = 2025), 'A', 30, 'ACTIVO', NOW(), NOW()),
((SELECT id FROM db_tp1.grados WHERE nombre = '4TO SECUNDARIA' AND nivel_id = 2), (SELECT id FROM db_tp1.periodos_academicos WHERE anio = 2025), 'B', 30, 'ACTIVO', NOW(), NOW()),

((SELECT id FROM db_tp1.grados WHERE nombre = '5TO SECUNDARIA' AND nivel_id = 2), (SELECT id FROM db_tp1.periodos_academicos WHERE anio = 2025), 'UNICA', 28, 'ACTIVO', NOW(), NOW());


BEGIN;

DELETE FROM db_tp1.matriculas;
DELETE FROM db_tp1.docente_curso_seccion;
DELETE FROM db_tp1.tutorias;
DELETE FROM db_tp1.periodos_evaluacion;
DELETE FROM db_tp1.secciones;
DELETE FROM db_tp1.alumnos;
DELETE FROM db_tp1.periodos_academicos;

COMMIT;

ALTER SEQUENCE db_tp1.alumnos_id_seq RESTART WITH 1;
ALTER SEQUENCE db_tp1.secciones_id_seq RESTART WITH 1;
ALTER SEQUENCE db_tp1.periodos_academicos_id_seq RESTART WITH 1;
ALTER SEQUENCE db_tp1.periodos_evaluacion_id_seq RESTART WITH 1;
ALTER SEQUENCE db_tp1.matriculas_id_seq RESTART WITH 1;
ALTER SEQUENCE db_tp1.docente_curso_seccion_id_seq RESTART WITH 1;
ALTER SEQUENCE db_tp1.tutorias_id_seq RESTART WITH 1;
