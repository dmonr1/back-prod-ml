ALTER TABLE db_tp1.cortes_seguimiento
    DROP CONSTRAINT uq_corte_seguimiento_semana,
    ADD CONSTRAINT uq_corte_seguimiento_periodo_semana_fecha
        UNIQUE (periodo_academico_id, semana, fecha_corte);

WITH semanas_configuradas AS (
    SELECT DISTINCT periodo_academico_id, semana
    FROM db_tp1.cortes_seguimiento
    WHERE estado = 'ACTIVO'
), cortes_por_periodo AS (
    SELECT
        pe.periodo_academico_id,
        semanas.semana,
        LEAST(pe.fecha_inicio + (semanas.semana * 7 - 1), pe.fecha_fin) AS fecha_corte
    FROM db_tp1.periodos_evaluacion pe
    JOIN semanas_configuradas semanas
        ON semanas.periodo_academico_id = pe.periodo_academico_id
    WHERE pe.fecha_inicio + (semanas.semana * 7 - 1) <= pe.fecha_fin
)
INSERT INTO db_tp1.cortes_seguimiento (periodo_academico_id, semana, fecha_corte, estado)
SELECT nuevos.periodo_academico_id, nuevos.semana, nuevos.fecha_corte, 'ACTIVO'
FROM cortes_por_periodo nuevos
WHERE NOT EXISTS (
    SELECT 1
    FROM db_tp1.cortes_seguimiento existente
    WHERE existente.periodo_academico_id = nuevos.periodo_academico_id
      AND existente.semana = nuevos.semana
      AND existente.fecha_corte = nuevos.fecha_corte
);
