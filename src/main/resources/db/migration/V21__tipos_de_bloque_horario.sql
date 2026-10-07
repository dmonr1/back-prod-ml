ALTER TABLE db_tp1.bloques_horarios
    ADD COLUMN tipo_bloque VARCHAR(20) NOT NULL DEFAULT 'CLASE';

UPDATE db_tp1.bloques_horarios
SET tipo_bloque = 'RECREO'
WHERE es_recreo = TRUE;

ALTER TABLE db_tp1.bloques_horarios
    ADD CONSTRAINT chk_bloque_horario_tipo
        CHECK (tipo_bloque IN ('CLASE', 'RECREO', 'TUTORIA')),
    ADD CONSTRAINT chk_bloque_horario_es_recreo
        CHECK (es_recreo = (tipo_bloque = 'RECREO'));
