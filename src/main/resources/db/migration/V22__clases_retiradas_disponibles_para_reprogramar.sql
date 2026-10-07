ALTER TABLE db_tp1.horarios_semanales
    ADD COLUMN pendiente_reprogramacion BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE db_tp1.horarios_semanales
    ADD CONSTRAINT chk_horario_pendiente_reprogramacion
        CHECK (NOT pendiente_reprogramacion OR estado = 'INACTIVO');
