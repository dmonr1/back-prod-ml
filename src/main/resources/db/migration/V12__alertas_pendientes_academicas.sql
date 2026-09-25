CREATE TABLE db_tp1.alertas_academicas (
    id BIGSERIAL PRIMARY KEY,
    clave_origen VARCHAR(120) NOT NULL UNIQUE,
    tipo VARCHAR(30) NOT NULL,
    docente_curso_seccion_id BIGINT NOT NULL,
    horario_semanal_id BIGINT,
    evaluacion_id BIGINT,
    fecha_referencia DATE NOT NULL,
    fecha_limite TIMESTAMP NOT NULL,
    cantidad_pendiente INTEGER NOT NULL DEFAULT 0,
    estado_alerta VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_alerta_asignacion FOREIGN KEY (docente_curso_seccion_id)
        REFERENCES db_tp1.docente_curso_seccion(id),
    CONSTRAINT fk_alerta_horario FOREIGN KEY (horario_semanal_id)
        REFERENCES db_tp1.horarios_semanales(id),
    CONSTRAINT fk_alerta_evaluacion FOREIGN KEY (evaluacion_id)
        REFERENCES db_tp1.evaluaciones(id),
    CONSTRAINT chk_alerta_tipo CHECK (tipo IN ('ASISTENCIA_PENDIENTE', 'NOTAS_PENDIENTES')),
    CONSTRAINT chk_alerta_estado CHECK (estado_alerta IN ('PENDIENTE', 'ATENDIDA')),
    CONSTRAINT chk_alerta_registro_estado CHECK (estado IN ('ACTIVO', 'INACTIVO')),
    CONSTRAINT chk_alerta_cantidad CHECK (cantidad_pendiente >= 0)
);

CREATE INDEX ix_alertas_academicas_estado_fecha
    ON db_tp1.alertas_academicas (estado_alerta, fecha_limite DESC);

CREATE INDEX ix_alertas_academicas_asignacion
    ON db_tp1.alertas_academicas (docente_curso_seccion_id, estado_alerta);
