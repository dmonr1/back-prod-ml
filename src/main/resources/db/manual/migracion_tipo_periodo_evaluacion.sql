-- Migracion manual: guardar el esquema de evaluacion del periodo academico.
-- Ejecutar una sola vez si la base ya fue migrada de bimestres a periodos de evaluacion.

BEGIN;

SET search_path TO db_tp1;

ALTER TABLE IF EXISTS periodos_academicos
    ADD COLUMN IF NOT EXISTS tipo_periodo_evaluacion VARCHAR(30) NOT NULL DEFAULT 'BIMESTRAL';

ALTER TABLE IF EXISTS periodos_academicos DROP CONSTRAINT IF EXISTS chk_periodos_tipo_evaluacion;
ALTER TABLE IF EXISTS periodos_academicos
    ADD CONSTRAINT chk_periodos_tipo_evaluacion CHECK (tipo_periodo_evaluacion IN ('BIMESTRAL', 'TRIMESTRAL', 'SEMESTRAL', 'ANUAL'));

COMMIT;
