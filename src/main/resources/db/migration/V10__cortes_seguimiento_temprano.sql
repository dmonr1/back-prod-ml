CREATE TABLE db_tp1.cortes_seguimiento (
    id BIGSERIAL PRIMARY KEY,
    periodo_academico_id BIGINT NOT NULL,
    semana INTEGER NOT NULL,
    fecha_corte DATE NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_corte_seguimiento_periodo FOREIGN KEY (periodo_academico_id)
        REFERENCES db_tp1.periodos_academicos(id),
    CONSTRAINT uq_corte_seguimiento_semana UNIQUE (periodo_academico_id, semana),
    CONSTRAINT chk_corte_seguimiento_semana CHECK (semana > 0),
    CONSTRAINT chk_corte_seguimiento_estado CHECK (estado IN ('ACTIVO', 'INACTIVO'))
);

ALTER TABLE db_tp1.predicciones_riesgo_global
    ALTER COLUMN periodo_evaluacion_id DROP NOT NULL,
    ADD COLUMN corte_seguimiento_id BIGINT,
    ADD CONSTRAINT fk_pred_global_corte FOREIGN KEY (corte_seguimiento_id)
        REFERENCES db_tp1.cortes_seguimiento(id);

ALTER TABLE db_tp1.predicciones_riesgo_curso
    ALTER COLUMN periodo_evaluacion_id DROP NOT NULL,
    ADD COLUMN corte_seguimiento_id BIGINT,
    ADD CONSTRAINT fk_pred_curso_corte FOREIGN KEY (corte_seguimiento_id)
        REFERENCES db_tp1.cortes_seguimiento(id);

ALTER TABLE db_tp1.predicciones_riesgo_global DROP CONSTRAINT uq_pred_global;
ALTER TABLE db_tp1.predicciones_riesgo_curso DROP CONSTRAINT uq_pred_curso;

CREATE UNIQUE INDEX uq_pred_global_corte
    ON db_tp1.predicciones_riesgo_global (matricula_id, corte_seguimiento_id)
    WHERE corte_seguimiento_id IS NOT NULL;
CREATE UNIQUE INDEX uq_pred_curso_corte
    ON db_tp1.predicciones_riesgo_curso (matricula_id, curso_id, corte_seguimiento_id)
    WHERE corte_seguimiento_id IS NOT NULL;

ALTER TABLE db_tp1.hallazgos_data_mining
    ADD COLUMN corte_seguimiento_id BIGINT,
    ADD CONSTRAINT fk_hallazgos_corte FOREIGN KEY (corte_seguimiento_id)
        REFERENCES db_tp1.cortes_seguimiento(id);

CREATE INDEX idx_corte_seguimiento_periodo_fecha
    ON db_tp1.cortes_seguimiento (periodo_academico_id, fecha_corte, estado);
CREATE INDEX idx_pred_global_corte ON db_tp1.predicciones_riesgo_global (corte_seguimiento_id);
CREATE INDEX idx_pred_curso_corte ON db_tp1.predicciones_riesgo_curso (corte_seguimiento_id);
CREATE INDEX idx_hallazgos_corte_seccion
    ON db_tp1.hallazgos_data_mining (corte_seguimiento_id, seccion_id, estado);
