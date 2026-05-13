-- Migracion manual: Bimestres -> Periodos de evaluacion
-- Ejecutar una sola vez sobre una base que fue creada con la nomenclatura antigua.
-- El proyecto tiene Flyway desactivado, por eso este script queda en db/manual.

BEGIN;

SET search_path TO db_tp1;

ALTER TABLE IF EXISTS bimestres RENAME TO periodos_evaluacion;
ALTER SEQUENCE IF EXISTS bimestres_id_seq RENAME TO periodos_evaluacion_id_seq;

ALTER TABLE IF EXISTS notas_curso_bimestre RENAME TO notas_curso_periodo_evaluacion;
ALTER TABLE IF EXISTS asistencias_bimestre RENAME TO asistencias_periodo_evaluacion;
ALTER TABLE IF EXISTS cargas_excel RENAME TO cargas_archivos;

ALTER TABLE IF EXISTS configuraciones_evaluacion RENAME COLUMN bimestre_id TO periodo_evaluacion_id;
ALTER TABLE IF EXISTS evaluaciones RENAME COLUMN bimestre_id TO periodo_evaluacion_id;
ALTER TABLE IF EXISTS notas_curso_periodo_evaluacion RENAME COLUMN bimestre_id TO periodo_evaluacion_id;
ALTER TABLE IF EXISTS asistencias_periodo_evaluacion RENAME COLUMN bimestre_id TO periodo_evaluacion_id;
ALTER TABLE IF EXISTS cargas_archivos RENAME COLUMN bimestre_id TO periodo_evaluacion_id;
ALTER TABLE IF EXISTS predicciones_riesgo_global RENAME COLUMN bimestre_id TO periodo_evaluacion_id;
ALTER TABLE IF EXISTS predicciones_riesgo_curso RENAME COLUMN bimestre_id TO periodo_evaluacion_id;
ALTER TABLE IF EXISTS hallazgos_data_mining RENAME COLUMN bimestre_id TO periodo_evaluacion_id;

ALTER TABLE IF EXISTS periodos_evaluacion DROP CONSTRAINT IF EXISTS chk_bimestres_numero;
ALTER TABLE IF EXISTS periodos_evaluacion DROP CONSTRAINT IF EXISTS chk_periodos_evaluacion_numero;
ALTER TABLE IF EXISTS periodos_evaluacion
    ADD CONSTRAINT chk_periodos_evaluacion_numero CHECK (numero > 0);

ALTER TABLE IF EXISTS periodos_academicos
    ADD COLUMN IF NOT EXISTS tipo_periodo_evaluacion VARCHAR(30) NOT NULL DEFAULT 'BIMESTRAL';

ALTER TABLE IF EXISTS periodos_academicos DROP CONSTRAINT IF EXISTS chk_periodos_tipo_evaluacion;
ALTER TABLE IF EXISTS periodos_academicos
    ADD CONSTRAINT chk_periodos_tipo_evaluacion CHECK (tipo_periodo_evaluacion IN ('BIMESTRAL', 'TRIMESTRAL', 'SEMESTRAL', 'ANUAL'));

ALTER INDEX IF EXISTS idx_conf_eval_bimestre_curso RENAME TO idx_conf_eval_periodo_evaluacion_curso;
ALTER INDEX IF EXISTS idx_evaluaciones_dcs_bimestre RENAME TO idx_evaluaciones_dcs_periodo_evaluacion;
ALTER INDEX IF EXISTS idx_notas_cb_bimestre RENAME TO idx_notas_cb_periodo_evaluacion;
ALTER INDEX IF EXISTS idx_asist_b_bimestre RENAME TO idx_asist_b_periodo_evaluacion;
ALTER INDEX IF EXISTS idx_pred_global_bimestre RENAME TO idx_pred_global_periodo_evaluacion;
ALTER INDEX IF EXISTS idx_pred_curso_bimestre RENAME TO idx_pred_curso_periodo_evaluacion;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM pg_trigger
        WHERE tgname = 'trg_bimestres_mod'
    ) THEN
        ALTER TRIGGER trg_bimestres_mod ON periodos_evaluacion RENAME TO trg_periodos_evaluacion_mod;
    END IF;
END $$;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM pg_trigger
        WHERE tgname = 'trg_notas_cb_mod'
    ) THEN
        ALTER TRIGGER trg_notas_cb_mod ON notas_curso_periodo_evaluacion RENAME TO trg_notas_curso_periodo_evaluacion_mod;
    END IF;
END $$;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM pg_trigger
        WHERE tgname = 'trg_asist_b_mod'
    ) THEN
        ALTER TRIGGER trg_asist_b_mod ON asistencias_periodo_evaluacion RENAME TO trg_asistencias_periodo_evaluacion_mod;
    END IF;
END $$;

COMMIT;
