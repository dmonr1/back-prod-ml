
CREATE TABLE IF NOT EXISTS db_tp1.configuraciones_evaluacion_periodo (
    id BIGSERIAL PRIMARY KEY,
    periodo_academico_id BIGINT NOT NULL,
    tipo_evaluacion_id BIGINT NOT NULL,
    cantidad_evaluaciones INTEGER NOT NULL DEFAULT 0,
    calcular_en_promedio BOOLEAN NOT NULL DEFAULT TRUE,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_config_eval_periodo_periodo
        FOREIGN KEY (periodo_academico_id)
        REFERENCES db_tp1.periodos_academicos(id),
    CONSTRAINT fk_config_eval_periodo_tipo
        FOREIGN KEY (tipo_evaluacion_id)
        REFERENCES db_tp1.tipos_evaluacion(id),
    CONSTRAINT uq_config_eval_periodo
        UNIQUE (periodo_academico_id, tipo_evaluacion_id)
);

CREATE TABLE IF NOT EXISTS db_tp1.configuraciones_evaluacion_curso (
    id BIGSERIAL PRIMARY KEY,
    periodo_academico_id BIGINT NOT NULL,
    curso_id BIGINT NOT NULL,
    tipo_evaluacion_id BIGINT NOT NULL,
    cantidad_evaluaciones INTEGER NOT NULL DEFAULT 0,
    calcular_en_promedio BOOLEAN NOT NULL DEFAULT TRUE,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_config_eval_curso_periodo
        FOREIGN KEY (periodo_academico_id)
        REFERENCES db_tp1.periodos_academicos(id),
    CONSTRAINT fk_config_eval_curso_curso
        FOREIGN KEY (curso_id)
        REFERENCES db_tp1.cursos(id),
    CONSTRAINT fk_config_eval_curso_tipo
        FOREIGN KEY (tipo_evaluacion_id)
        REFERENCES db_tp1.tipos_evaluacion(id),
    CONSTRAINT uq_config_eval_curso
        UNIQUE (periodo_academico_id, curso_id, tipo_evaluacion_id)
);

COMMIT;


BEGIN;

CREATE TABLE IF NOT EXISTS db_tp1.cursos_periodo_academico (
    id BIGSERIAL PRIMARY KEY,
    periodo_academico_id BIGINT NOT NULL,
    curso_id BIGINT NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_cursos_periodo_periodo
        FOREIGN KEY (periodo_academico_id)
        REFERENCES db_tp1.periodos_academicos(id),
    CONSTRAINT fk_cursos_periodo_curso
        FOREIGN KEY (curso_id)
        REFERENCES db_tp1.cursos(id),
    CONSTRAINT uq_cursos_periodo
        UNIQUE (periodo_academico_id, curso_id)
);

COMMIT;



INSERT INTO db_tp1.cursos_periodo_academico
(periodo_academico_id, curso_id, estado, fecha_registro, fecha_modificacion)
SELECT DISTINCT
    dcs.periodo_academico_id,
    dcs.curso_id,
    'ACTIVO',
    NOW(),
    NOW()
FROM db_tp1.docente_curso_seccion dcs
WHERE dcs.periodo_academico_id = 2
ON CONFLICT (periodo_academico_id, curso_id) DO NOTHING;

INSERT INTO db_tp1.configuraciones_evaluacion_periodo
(periodo_academico_id, tipo_evaluacion_id, cantidad_evaluaciones, calcular_en_promedio, estado, fecha_registro, fecha_modificacion)
VALUES
(2, 1, 8, TRUE, 'ACTIVO', NOW(), NOW()),
(2, 2, 2, TRUE, 'ACTIVO', NOW(), NOW()),
(2, 3, 2, TRUE, 'ACTIVO', NOW(), NOW()),
(2, 4, 4, TRUE, 'ACTIVO', NOW(), NOW()),
(2, 5, 2, TRUE, 'ACTIVO', NOW(), NOW()),
(2, 6, 1, TRUE, 'ACTIVO', NOW(), NOW())
ON CONFLICT (periodo_academico_id, tipo_evaluacion_id) DO UPDATE
SET
  cantidad_evaluaciones = EXCLUDED.cantidad_evaluaciones,
  calcular_en_promedio = EXCLUDED.calcular_en_promedio,
  estado = EXCLUDED.estado,
  fecha_modificacion = NOW();

