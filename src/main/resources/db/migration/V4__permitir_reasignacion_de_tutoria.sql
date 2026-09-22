-- Conserva el historial de tutorias y restringe a un unico tutor activo por seccion y periodo.
ALTER TABLE db_tp1.tutorias
    DROP CONSTRAINT IF EXISTS uq_tutorias;

CREATE UNIQUE INDEX uq_tutorias_seccion_periodo_activo
    ON db_tp1.tutorias (seccion_id, periodo_academico_id)
    WHERE estado = 'ACTIVO';
