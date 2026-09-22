-- Solo puede existir un docente activo por curso, seccion y periodo academico.
-- Se conserva la asignacion activa mas antigua y las duplicadas pasan a inactivas.
WITH asignaciones_duplicadas AS (
    SELECT
        id,
        ROW_NUMBER() OVER (
            PARTITION BY curso_id, seccion_id, periodo_academico_id
            ORDER BY fecha_registro, id
        ) AS posicion
    FROM db_tp1.docente_curso_seccion
    WHERE estado = 'ACTIVO'
)
UPDATE db_tp1.docente_curso_seccion asignacion
SET estado = 'INACTIVO', fecha_modificacion = NOW()
FROM asignaciones_duplicadas duplicada
WHERE asignacion.id = duplicada.id
  AND duplicada.posicion > 1;

CREATE UNIQUE INDEX uq_dcs_curso_seccion_periodo_activo
ON db_tp1.docente_curso_seccion (curso_id, seccion_id, periodo_academico_id)
WHERE estado = 'ACTIVO';
