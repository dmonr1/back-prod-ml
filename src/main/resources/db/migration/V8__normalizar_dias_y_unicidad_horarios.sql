ALTER TABLE db_tp1.horarios_semanales
    DROP CONSTRAINT IF EXISTS chk_horario_semanal_dia;

ALTER TABLE db_tp1.horarios_semanales
    ALTER COLUMN dia_semana TYPE VARCHAR(15)
    USING CASE dia_semana::TEXT
        WHEN '0' THEN 'LUNES'
        WHEN '1' THEN 'MARTES'
        WHEN '2' THEN 'MIERCOLES'
        WHEN '3' THEN 'JUEVES'
        WHEN '4' THEN 'VIERNES'
        WHEN '5' THEN 'SABADO'
        WHEN '6' THEN 'DOMINGO'
        WHEN '7' THEN 'DOMINGO'
        ELSE dia_semana::TEXT
    END;

ALTER TABLE db_tp1.horarios_semanales
    ADD CONSTRAINT chk_horario_semanal_dia
    CHECK (dia_semana IN ('LUNES', 'MARTES', 'MIERCOLES', 'JUEVES', 'VIERNES', 'SABADO', 'DOMINGO'));

ALTER TABLE db_tp1.bloques_horarios
    DROP CONSTRAINT IF EXISTS uq_bloque_horario_periodo_nivel_orden;
ALTER TABLE db_tp1.horarios_semanales
    DROP CONSTRAINT IF EXISTS uq_horario_semanal_asignacion_dia_bloque;

DROP INDEX IF EXISTS db_tp1.uq_bloque_horario_periodo_nivel_orden_activo;
DROP INDEX IF EXISTS db_tp1.uq_horario_semanal_asignacion_dia_bloque_activo;

CREATE UNIQUE INDEX uq_bloque_horario_periodo_nivel_orden_activo
    ON db_tp1.bloques_horarios (periodo_academico_id, nivel_id, orden) WHERE estado = 'ACTIVO';
CREATE UNIQUE INDEX uq_horario_semanal_asignacion_dia_bloque_activo
    ON db_tp1.horarios_semanales (docente_curso_seccion_id, dia_semana, bloque_horario_id) WHERE estado = 'ACTIVO';
