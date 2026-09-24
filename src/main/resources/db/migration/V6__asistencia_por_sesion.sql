CREATE TABLE db_tp1.asistencias_sesion (
    id BIGSERIAL PRIMARY KEY,
    docente_curso_seccion_id BIGINT NOT NULL,
    periodo_evaluacion_id BIGINT NOT NULL,
    matricula_id BIGINT NOT NULL,
    fecha_clase DATE NOT NULL,
    estado_asistencia VARCHAR(20) NOT NULL,
    observacion VARCHAR(255),
    usuario_registro_id BIGINT NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_asistencia_sesion_asignacion FOREIGN KEY (docente_curso_seccion_id)
        REFERENCES db_tp1.docente_curso_seccion(id),
    CONSTRAINT fk_asistencia_sesion_periodo FOREIGN KEY (periodo_evaluacion_id)
        REFERENCES db_tp1.periodos_evaluacion(id),
    CONSTRAINT fk_asistencia_sesion_matricula FOREIGN KEY (matricula_id)
        REFERENCES db_tp1.matriculas(id),
    CONSTRAINT fk_asistencia_sesion_usuario FOREIGN KEY (usuario_registro_id)
        REFERENCES db_tp1.usuarios(id),
    CONSTRAINT uq_asistencia_sesion_alumno UNIQUE (
        docente_curso_seccion_id,
        periodo_evaluacion_id,
        fecha_clase,
        matricula_id
    ),
    CONSTRAINT chk_asistencia_sesion_estado CHECK (
        estado_asistencia IN ('PRESENTE', 'AUSENTE', 'TARDANZA', 'JUSTIFICADO')
    ),
    CONSTRAINT chk_asistencia_sesion_registro_estado CHECK (estado IN ('ACTIVO', 'INACTIVO'))
);

CREATE INDEX idx_asistencia_sesion_asignacion_fecha
    ON db_tp1.asistencias_sesion (docente_curso_seccion_id, periodo_evaluacion_id, fecha_clase);

CREATE INDEX idx_asistencia_sesion_matricula_fecha
    ON db_tp1.asistencias_sesion (matricula_id, fecha_clase);
