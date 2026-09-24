ALTER TABLE db_tp1.asistencias_sesion
    ADD COLUMN horario_semanal_id BIGINT
        REFERENCES db_tp1.horarios_semanales(id);

ALTER TABLE db_tp1.asistencias_sesion
    DROP CONSTRAINT IF EXISTS uq_asistencia_sesion_alumno;

CREATE UNIQUE INDEX uq_asistencia_sesion_bloque_alumno
    ON db_tp1.asistencias_sesion (
        docente_curso_seccion_id,
        periodo_evaluacion_id,
        fecha_clase,
        horario_semanal_id,
        matricula_id
    ) WHERE horario_semanal_id IS NOT NULL;

CREATE UNIQUE INDEX uq_asistencia_sesion_legada_alumno
    ON db_tp1.asistencias_sesion (
        docente_curso_seccion_id,
        periodo_evaluacion_id,
        fecha_clase,
        matricula_id
    ) WHERE horario_semanal_id IS NULL;
