CREATE TABLE db_tp1.bloques_horarios (
    id BIGSERIAL PRIMARY KEY,
    periodo_academico_id BIGINT NOT NULL REFERENCES db_tp1.periodos_academicos(id),
    nivel_id BIGINT NOT NULL REFERENCES db_tp1.niveles(id),
    nombre VARCHAR(60) NOT NULL,
    orden SMALLINT NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_bloque_horario_rango CHECK (hora_inicio < hora_fin),
    CONSTRAINT chk_bloque_horario_estado CHECK (estado IN ('ACTIVO', 'INACTIVO'))
);

CREATE TABLE db_tp1.horarios_semanales (
    id BIGSERIAL PRIMARY KEY,
    docente_curso_seccion_id BIGINT NOT NULL REFERENCES db_tp1.docente_curso_seccion(id),
    bloque_horario_id BIGINT NOT NULL REFERENCES db_tp1.bloques_horarios(id),
    dia_semana VARCHAR(15) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_horario_semanal_dia CHECK (dia_semana IN ('LUNES', 'MARTES', 'MIERCOLES', 'JUEVES', 'VIERNES', 'SABADO', 'DOMINGO')),
    CONSTRAINT chk_horario_semanal_estado CHECK (estado IN ('ACTIVO', 'INACTIVO'))
);

CREATE INDEX idx_bloques_horarios_periodo_nivel ON db_tp1.bloques_horarios (periodo_academico_id, nivel_id, estado);
CREATE INDEX idx_horarios_semanales_bloque_dia ON db_tp1.horarios_semanales (bloque_horario_id, dia_semana, estado);
CREATE INDEX idx_horarios_semanales_asignacion ON db_tp1.horarios_semanales (docente_curso_seccion_id, estado);
CREATE UNIQUE INDEX uq_bloque_horario_periodo_nivel_orden_activo
    ON db_tp1.bloques_horarios (periodo_academico_id, nivel_id, orden) WHERE estado = 'ACTIVO';
CREATE UNIQUE INDEX uq_horario_semanal_asignacion_dia_bloque_activo
    ON db_tp1.horarios_semanales (docente_curso_seccion_id, dia_semana, bloque_horario_id) WHERE estado = 'ACTIVO';
