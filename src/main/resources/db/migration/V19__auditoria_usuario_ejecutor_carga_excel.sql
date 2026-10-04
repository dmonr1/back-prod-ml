ALTER TABLE db_tp1.cargas_archivos
    ADD COLUMN usuario_ejecutor_id BIGINT;

ALTER TABLE db_tp1.cargas_archivos
    ADD CONSTRAINT fk_cargas_archivos_usuario_ejecutor
        FOREIGN KEY (usuario_ejecutor_id) REFERENCES db_tp1.usuarios(id);
