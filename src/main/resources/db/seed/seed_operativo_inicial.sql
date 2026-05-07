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
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_periodos_fechas CHECK (fecha_fin >= fecha_inicio)
);

CREATE TABLE bimestres (
    id BIGSERIAL PRIMARY KEY,
    periodo_academico_id BIGINT NOT NULL,
    nombre VARCHAR(50) NOT NULL,
    numero SMALLINT NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_bimestres_periodo FOREIGN KEY (periodo_academico_id) REFERENCES periodos_academicos(id),
    CONSTRAINT chk_bimestres_numero CHECK (numero BETWEEN 1 AND 4),
    CONSTRAINT chk_bimestres_fechas CHECK (fecha_fin >= fecha_inicio),
    CONSTRAINT uq_bimestres UNIQUE (periodo_academico_id, numero)
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
    bimestre_id BIGINT NOT NULL,
    curso_id BIGINT NOT NULL,
    grado_id BIGINT,
    tipo_evaluacion_id BIGINT NOT NULL,
    cantidad_evaluaciones INTEGER NOT NULL,
    calcular_en_promedio BOOLEAN NOT NULL DEFAULT TRUE,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_conf_eval_periodo FOREIGN KEY (periodo_academico_id) REFERENCES periodos_academicos(id),
    CONSTRAINT fk_conf_eval_bimestre FOREIGN KEY (bimestre_id) REFERENCES bimestres(id),
    CONSTRAINT fk_conf_eval_curso FOREIGN KEY (curso_id) REFERENCES cursos(id),
    CONSTRAINT fk_conf_eval_grado FOREIGN KEY (grado_id) REFERENCES grados(id),
    CONSTRAINT fk_conf_eval_tipo FOREIGN KEY (tipo_evaluacion_id) REFERENCES tipos_evaluacion(id),
    CONSTRAINT chk_conf_eval_cantidad CHECK (cantidad_evaluaciones > 0),
    CONSTRAINT uq_conf_eval UNIQUE (bimestre_id, curso_id, grado_id, tipo_evaluacion_id)
);

CREATE TABLE evaluaciones (
    id BIGSERIAL PRIMARY KEY,
    configuracion_evaluacion_id BIGINT NOT NULL,
    docente_curso_seccion_id BIGINT NOT NULL,
    bimestre_id BIGINT NOT NULL,
    tipo_evaluacion_id BIGINT NOT NULL,
    numero_evaluacion INTEGER NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    fecha_evaluacion DATE,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_evaluaciones_configuracion FOREIGN KEY (configuracion_evaluacion_id) REFERENCES configuraciones_evaluacion(id),
    CONSTRAINT fk_evaluaciones_dcs FOREIGN KEY (docente_curso_seccion_id) REFERENCES docente_curso_seccion(id),
    CONSTRAINT fk_evaluaciones_bimestre FOREIGN KEY (bimestre_id) REFERENCES bimestres(id),
    CONSTRAINT fk_evaluaciones_tipo FOREIGN KEY (tipo_evaluacion_id) REFERENCES tipos_evaluacion(id),
    CONSTRAINT chk_evaluaciones_numero CHECK (numero_evaluacion > 0),
    CONSTRAINT uq_evaluaciones UNIQUE (docente_curso_seccion_id, bimestre_id, tipo_evaluacion_id, numero_evaluacion)
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

CREATE TABLE notas_curso_bimestre (
    id BIGSERIAL PRIMARY KEY,
    matricula_id BIGINT NOT NULL,
    curso_id BIGINT NOT NULL,
    bimestre_id BIGINT NOT NULL,
    promedio_curso NUMERIC(5,2) NOT NULL,
    cantidad_evaluaciones_registradas INTEGER NOT NULL DEFAULT 0,
    observacion VARCHAR(255),
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_notas_cb_matricula FOREIGN KEY (matricula_id) REFERENCES matriculas(id),
    CONSTRAINT fk_notas_cb_curso FOREIGN KEY (curso_id) REFERENCES cursos(id),
    CONSTRAINT fk_notas_cb_bimestre FOREIGN KEY (bimestre_id) REFERENCES bimestres(id),
    CONSTRAINT chk_notas_cb_rango CHECK (promedio_curso >= 0 AND promedio_curso <= 20),
    CONSTRAINT uq_notas_cb UNIQUE (matricula_id, curso_id, bimestre_id)
);

CREATE TABLE asistencias_bimestre (
    id BIGSERIAL PRIMARY KEY,
    matricula_id BIGINT NOT NULL,
    bimestre_id BIGINT NOT NULL,
    clases_programadas INTEGER NOT NULL,
    clases_asistidas INTEGER NOT NULL,
    observacion VARCHAR(255),
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_asist_b_matricula FOREIGN KEY (matricula_id) REFERENCES matriculas(id),
    CONSTRAINT fk_asist_b_bimestre FOREIGN KEY (bimestre_id) REFERENCES bimestres(id),
    CONSTRAINT chk_asist_b_programadas CHECK (clases_programadas >= 0),
    CONSTRAINT chk_asist_b_asistidas CHECK (clases_asistidas >= 0),
    CONSTRAINT chk_asist_b_limite CHECK (clases_asistidas <= clases_programadas),
    CONSTRAINT uq_asist_b UNIQUE (matricula_id, bimestre_id)
);

-- =========================================================
-- CARGAS OPCIONALES
-- =========================================================

CREATE TABLE cargas_archivos (
    id BIGSERIAL PRIMARY KEY,
    docente_id BIGINT NOT NULL,
    periodo_academico_id BIGINT NOT NULL,
    bimestre_id BIGINT NOT NULL,
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
    CONSTRAINT fk_cargas_bimestre FOREIGN KEY (bimestre_id) REFERENCES bimestres(id),
    CONSTRAINT fk_cargas_seccion FOREIGN KEY (seccion_id) REFERENCES secciones(id)
);

-- =========================================================
-- PREDICCION Y ANALITICA
-- =========================================================

CREATE TABLE predicciones_riesgo_global (
    id BIGSERIAL PRIMARY KEY,
    matricula_id BIGINT NOT NULL,
    bimestre_id BIGINT NOT NULL,
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
    CONSTRAINT fk_pred_global_bimestre FOREIGN KEY (bimestre_id) REFERENCES bimestres(id),
    CONSTRAINT fk_pred_global_carga FOREIGN KEY (carga_archivo_id) REFERENCES cargas_archivos(id),
    CONSTRAINT chk_pred_global_puntaje CHECK (puntaje_riesgo >= 0 AND puntaje_riesgo <= 100),
    CONSTRAINT chk_pred_global_nivel CHECK (nivel_riesgo IN ('BAJO', 'MEDIO', 'ALTO')),
    CONSTRAINT uq_pred_global UNIQUE (matricula_id, bimestre_id)
);

CREATE TABLE predicciones_riesgo_curso (
    id BIGSERIAL PRIMARY KEY,
    matricula_id BIGINT NOT NULL,
    curso_id BIGINT NOT NULL,
    bimestre_id BIGINT NOT NULL,
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
    CONSTRAINT fk_pred_curso_bimestre FOREIGN KEY (bimestre_id) REFERENCES bimestres(id),
    CONSTRAINT fk_pred_curso_carga FOREIGN KEY (carga_archivo_id) REFERENCES cargas_archivos(id),
    CONSTRAINT chk_pred_curso_puntaje CHECK (puntaje_riesgo >= 0 AND puntaje_riesgo <= 100),
    CONSTRAINT chk_pred_curso_nivel CHECK (nivel_riesgo IN ('BAJO', 'MEDIO', 'ALTO')),
    CONSTRAINT uq_pred_curso UNIQUE (matricula_id, curso_id, bimestre_id)
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
    bimestre_id BIGINT,
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
    CONSTRAINT fk_hallazgos_bimestre FOREIGN KEY (bimestre_id) REFERENCES bimestres(id),
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
CREATE INDEX idx_conf_eval_bimestre_curso ON configuraciones_evaluacion(bimestre_id, curso_id);
CREATE INDEX idx_evaluaciones_dcs_bimestre ON evaluaciones(docente_curso_seccion_id, bimestre_id);
CREATE INDEX idx_detalle_eval_matricula ON detalle_notas_evaluacion(matricula_id);
CREATE INDEX idx_notas_cb_bimestre ON notas_curso_bimestre(bimestre_id);
CREATE INDEX idx_notas_cb_matricula ON notas_curso_bimestre(matricula_id);
CREATE INDEX idx_asist_b_bimestre ON asistencias_bimestre(bimestre_id);
CREATE INDEX idx_pred_global_bimestre ON predicciones_riesgo_global(bimestre_id);
CREATE INDEX idx_pred_curso_bimestre ON predicciones_riesgo_curso(bimestre_id);
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

CREATE TRIGGER trg_bimestres_mod
BEFORE UPDATE ON bimestres
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
BEFORE UPDATE ON notas_curso_bimestre
FOR EACH ROW EXECUTE FUNCTION actualizar_fecha_modificacion();

CREATE TRIGGER trg_asist_b_mod
BEFORE UPDATE ON asistencias_bimestre
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
    ab.bimestre_id,
    ab.clases_programadas,
    ab.clases_asistidas,
    CASE
        WHEN ab.clases_programadas = 0 THEN 0
        ELSE ROUND((ab.clases_asistidas::numeric / ab.clases_programadas::numeric) * 100, 2)
    END AS porcentaje_asistencia
FROM asistencias_bimestre ab;

CREATE OR REPLACE VIEW vw_predicciones_globales_legibles AS
SELECT
    prg.id,
    a.codigo AS codigo_alumno,
    a.nombres,
    a.apellidos,
    n.nombre AS nivel,
    g.nombre AS grado,
    s.nombre AS seccion,
    b.nombre AS bimestre,
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
JOIN bimestres b ON b.id = prg.bimestre_id;

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
-- =========================================================ok c

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
