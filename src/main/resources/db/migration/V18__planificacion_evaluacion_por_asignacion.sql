ALTER TABLE db_tp1.tipos_evaluacion
    DROP CONSTRAINT IF EXISTS tipos_evaluacion_nombre_key;

ALTER TABLE db_tp1.tipos_evaluacion
    ADD COLUMN docente_curso_seccion_id BIGINT REFERENCES db_tp1.docente_curso_seccion(id);

CREATE UNIQUE INDEX uq_tipo_evaluacion_global_nombre
    ON db_tp1.tipos_evaluacion (UPPER(nombre))
    WHERE docente_curso_seccion_id IS NULL;

CREATE UNIQUE INDEX uq_tipo_evaluacion_asignacion_nombre
    ON db_tp1.tipos_evaluacion (docente_curso_seccion_id, UPPER(nombre))
    WHERE docente_curso_seccion_id IS NOT NULL;

CREATE TABLE db_tp1.planes_evaluacion_asignacion (
    id BIGSERIAL PRIMARY KEY,
    docente_curso_seccion_id BIGINT NOT NULL REFERENCES db_tp1.docente_curso_seccion(id),
    periodo_evaluacion_id BIGINT NOT NULL REFERENCES db_tp1.periodos_evaluacion(id),
    CONSTRAINT uq_plan_evaluacion_asignacion UNIQUE (docente_curso_seccion_id, periodo_evaluacion_id)
);
