ALTER TABLE db_tp1.evaluaciones
    ADD COLUMN creado_por_usuario_id BIGINT,
    ADD COLUMN modificado_por_usuario_id BIGINT;

ALTER TABLE db_tp1.evaluaciones
    ADD CONSTRAINT fk_evaluaciones_creado_por
        FOREIGN KEY (creado_por_usuario_id) REFERENCES db_tp1.usuarios(id),
    ADD CONSTRAINT fk_evaluaciones_modificado_por
        FOREIGN KEY (modificado_por_usuario_id) REFERENCES db_tp1.usuarios(id);
