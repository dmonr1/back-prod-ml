-- Datos sinteticos reproducibles para poblar asistencia y notas del periodo 2025.
-- No sobrescribe registros activos existentes y puede ejecutarse nuevamente.
\set ON_ERROR_STOP on

BEGIN;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM db_tp1.periodos_academicos WHERE id = 2 AND anio = 2025) THEN
        RAISE EXCEPTION 'No existe el periodo academico 2025 esperado (id=2).';
    END IF;
    IF NOT EXISTS (SELECT 1 FROM db_tp1.usuarios_roles ur JOIN db_tp1.roles r ON r.id = ur.rol_id
                   JOIN db_tp1.usuarios u ON u.id = ur.usuario_id
                   WHERE r.nombre = 'ADMIN' AND u.estado = 'ACTIVO') THEN
        RAISE EXCEPTION 'Se necesita un usuario administrador activo para registrar la autoria.';
    END IF;
    IF EXISTS (SELECT 1 FROM db_tp1.matriculas m
               WHERE m.periodo_academico_id = 2 AND m.estado = 'ACTIVO'
                 AND NOT EXISTS (SELECT 1 FROM db_tp1.docente_curso_seccion dc
                                 WHERE dc.periodo_academico_id = 2 AND dc.seccion_id = m.seccion_id
                                   AND dc.estado = 'ACTIVO')) THEN
        RAISE EXCEPTION 'Hay matriculas activas 2025 sin asignaciones docentes activas.';
    END IF;
END $$;

CREATE TEMP TABLE semilla_dias_2025 ON COMMIT DROP AS
SELECT pe.id AS periodo_evaluacion_id,
       gs::date AS fecha,
       EXTRACT(ISODOW FROM gs)::integer AS dia_iso
FROM db_tp1.periodos_evaluacion pe
CROSS JOIN LATERAL generate_series(pe.fecha_inicio::timestamp, pe.fecha_fin::timestamp, INTERVAL '1 day') gs
WHERE pe.periodo_academico_id = 2
  AND EXTRACT(ISODOW FROM gs) BETWEEN 1 AND 5
  AND gs::date NOT IN (
      DATE '2025-04-17', DATE '2025-04-18', DATE '2025-05-01', DATE '2025-07-23',
      DATE '2025-07-28', DATE '2025-07-29', DATE '2025-08-06', DATE '2025-10-08',
      DATE '2025-12-08', DATE '2025-12-09'
  );

CREATE TEMP TABLE semilla_perfil_2025 ON COMMIT DROP AS
WITH uniforms AS (
    SELECT m.id AS matricula_id,
           m.alumno_id,
           (
             (MOD(ABS(HASHTEXTEXTENDED('aptitud-a:' || m.alumno_id::text, 2025)), 10000)::numeric
              + MOD(ABS(HASHTEXTEXTENDED('aptitud-b:' || m.alumno_id::text, 2025)), 10000)::numeric
              + MOD(ABS(HASHTEXTEXTENDED('aptitud-c:' || m.alumno_id::text, 2025)), 10000)::numeric)
             / 30000.0
           ) AS aptitud_relativa
    FROM db_tp1.matriculas m
    WHERE m.periodo_academico_id = 2 AND m.estado = 'ACTIVO'
)
SELECT matricula_id,
       alumno_id,
       (7.5 + aptitud_relativa * 10.5)::numeric(5,2) AS rendimiento_base,
       ((MOD(ABS(HASHTEXTEXTENDED('tendencia:' || alumno_id::text, 2025)), 21)::numeric - 10) / 50.0)::numeric(5,3) AS tendencia
FROM uniforms;

CREATE TEMP TABLE semilla_asistencias_2025 ON COMMIT DROP AS
WITH clases AS (
    SELECT d.periodo_evaluacion_id,
           d.fecha,
           d.dia_iso,
           dc.id AS asignacion_id,
           dc.seccion_id,
           dc.periodo_academico_id,
           h.id AS horario_id,
           b.hora_inicio,
           m.id AS matricula_id,
           m.alumno_id,
           perfil.rendimiento_base,
           ROW_NUMBER() OVER (PARTITION BY m.id, d.fecha ORDER BY b.hora_inicio, h.id) AS numero_clase_dia
    FROM semilla_dias_2025 d
    JOIN db_tp1.horarios_semanales h ON h.estado = 'ACTIVO'
    JOIN db_tp1.bloques_horarios b ON b.id = h.bloque_horario_id AND b.estado = 'ACTIVO'
    JOIN db_tp1.docente_curso_seccion dc ON dc.id = h.docente_curso_seccion_id AND dc.estado = 'ACTIVO'
    JOIN db_tp1.matriculas m ON m.seccion_id = dc.seccion_id
                            AND m.periodo_academico_id = dc.periodo_academico_id
                            AND m.estado = 'ACTIVO'
    JOIN semilla_perfil_2025 perfil ON perfil.matricula_id = m.id
    WHERE dc.periodo_academico_id = 2
      AND CASE h.dia_semana
            WHEN 'LUNES' THEN 1 WHEN 'MARTES' THEN 2 WHEN 'MIERCOLES' THEN 3
            WHEN 'JUEVES' THEN 4 WHEN 'VIERNES' THEN 5 ELSE 0
          END = d.dia_iso
), estados AS (
    SELECT clases.*,
           MOD(ABS(HASHTEXTEXTENDED('ausencia:' || matricula_id::text || ':' || fecha::text, 2025)), 10000)::numeric / 100.0 AS azar_ausencia,
           MOD(ABS(HASHTEXTEXTENDED('justificada:' || matricula_id::text || ':' || fecha::text, 2025)), 100)::integer AS azar_justificada,
           MOD(ABS(HASHTEXTEXTENDED('tardanza:' || matricula_id::text || ':' || fecha::text, 2025)), 100)::integer AS azar_tardanza
    FROM clases
)
SELECT periodo_evaluacion_id,
       fecha,
       asignacion_id,
       horario_id,
       matricula_id,
       CASE
         WHEN azar_ausencia < CASE
              WHEN rendimiento_base < 10.5 THEN 13.0
              WHEN rendimiento_base < 12.5 THEN 8.5
              WHEN rendimiento_base < 15.0 THEN 5.0
              ELSE 2.5 END
           THEN CASE WHEN azar_justificada < 22 THEN 'JUSTIFICADO' ELSE 'AUSENTE' END
         WHEN numero_clase_dia = 1 AND azar_tardanza < CASE
              WHEN rendimiento_base < 10.5 THEN 7
              WHEN rendimiento_base < 13.5 THEN 5
              ELSE 3 END
           THEN 'TARDANZA'
         ELSE 'PRESENTE'
       END AS estado_asistencia
FROM estados;

INSERT INTO db_tp1.configuraciones_asistencia_periodo
    (docente_curso_seccion_id, periodo_evaluacion_id, clases_programadas, estado)
SELECT dc.id,
       d.periodo_evaluacion_id,
       COUNT(*)::integer,
       'ACTIVO'
FROM db_tp1.docente_curso_seccion dc
JOIN db_tp1.horarios_semanales h ON h.docente_curso_seccion_id = dc.id AND h.estado = 'ACTIVO'
JOIN semilla_dias_2025 d ON CASE h.dia_semana
    WHEN 'LUNES' THEN 1 WHEN 'MARTES' THEN 2 WHEN 'MIERCOLES' THEN 3
    WHEN 'JUEVES' THEN 4 WHEN 'VIERNES' THEN 5 ELSE 0 END = d.dia_iso
WHERE dc.periodo_academico_id = 2 AND dc.estado = 'ACTIVO'
GROUP BY dc.id, d.periodo_evaluacion_id
ON CONFLICT (docente_curso_seccion_id, periodo_evaluacion_id)
DO UPDATE SET clases_programadas = EXCLUDED.clases_programadas, estado = 'ACTIVO';

INSERT INTO db_tp1.asistencias_sesion
    (docente_curso_seccion_id, periodo_evaluacion_id, matricula_id, fecha_clase,
     estado_asistencia, observacion, usuario_registro_id, estado, horario_semanal_id)
SELECT registro.asignacion_id,
       registro.periodo_evaluacion_id,
       registro.matricula_id,
       registro.fecha,
       registro.estado_asistencia,
       CASE registro.estado_asistencia
         WHEN 'PRESENTE' THEN NULL
         WHEN 'TARDANZA' THEN 'Tardanza simulada para historial de prueba 2025'
         WHEN 'AUSENTE' THEN 'Inasistencia simulada para historial de prueba 2025'
         ELSE 'Inasistencia justificada simulada para historial de prueba 2025'
       END,
       (SELECT MIN(u.id) FROM db_tp1.usuarios u
        JOIN db_tp1.usuarios_roles ur ON ur.usuario_id = u.id
        JOIN db_tp1.roles r ON r.id = ur.rol_id
        WHERE r.nombre = 'ADMIN' AND u.estado = 'ACTIVO'),
       'ACTIVO',
       registro.horario_id
FROM semilla_asistencias_2025 registro
ON CONFLICT DO NOTHING;

INSERT INTO db_tp1.asistencias_periodo_evaluacion
    (matricula_id, periodo_evaluacion_id, clases_programadas, clases_asistidas, observacion, estado)
SELECT a.matricula_id,
       a.periodo_evaluacion_id,
       COUNT(*) FILTER (WHERE a.estado_asistencia <> 'JUSTIFICADO')::integer,
       COUNT(*) FILTER (WHERE a.estado_asistencia IN ('PRESENTE', 'TARDANZA'))::integer,
       'Consolidado de asistencias simuladas del historial 2025',
       'ACTIVO'
FROM db_tp1.asistencias_sesion a
JOIN db_tp1.matriculas m ON m.id = a.matricula_id AND m.periodo_academico_id = 2
WHERE a.estado = 'ACTIVO'
GROUP BY a.matricula_id, a.periodo_evaluacion_id
ON CONFLICT (matricula_id, periodo_evaluacion_id)
DO UPDATE SET clases_programadas = EXCLUDED.clases_programadas,
              clases_asistidas = EXCLUDED.clases_asistidas,
              observacion = EXCLUDED.observacion,
              estado = 'ACTIVO';

CREATE TEMP TABLE semilla_evaluaciones_2025 ON COMMIT DROP AS
WITH candidatas AS (
    SELECT ce.id AS configuracion_id,
           dc.id AS asignacion_id,
           pe.id AS periodo_evaluacion_id,
           pe.numero AS numero_periodo,
           ce.tipo_evaluacion_id,
           te.nombre AS tipo_nombre,
           te.orden AS tipo_orden,
           serie.numero_evaluacion,
           pe.fecha_inicio,
           pe.fecha_fin,
           CASE ce.tipo_evaluacion_id
             WHEN 1 THEN 0.35
             WHEN 5 THEN 0.55
             WHEN 6 THEN 0.78
             ELSE 0.50
           END AS fraccion_periodo,
           ROW_NUMBER() OVER (
             PARTITION BY dc.id, pe.id, ce.tipo_evaluacion_id, serie.numero_evaluacion
             ORDER BY CASE WHEN ce.grado_id = s.grado_id THEN 0 ELSE 1 END, ce.id
           ) AS prioridad
    FROM db_tp1.configuraciones_evaluacion ce
    JOIN db_tp1.periodos_evaluacion pe ON pe.id = ce.periodo_evaluacion_id
    JOIN db_tp1.docente_curso_seccion dc ON dc.periodo_academico_id = ce.periodo_academico_id
                                        AND dc.curso_id = ce.curso_id AND dc.estado = 'ACTIVO'
    JOIN db_tp1.secciones s ON s.id = dc.seccion_id
    JOIN db_tp1.tipos_evaluacion te ON te.id = ce.tipo_evaluacion_id AND te.estado = 'ACTIVO'
    CROSS JOIN LATERAL generate_series(1, ce.cantidad_evaluaciones) AS serie(numero_evaluacion)
    WHERE ce.periodo_academico_id = 2
      AND ce.periodo_evaluacion_id IN (SELECT periodo_evaluacion_id FROM semilla_dias_2025 GROUP BY periodo_evaluacion_id)
      AND ce.estado = 'ACTIVO'
      AND (ce.grado_id IS NULL OR ce.grado_id = s.grado_id)
), con_objetivo AS (
    SELECT candidatas.*,
           fecha_inicio + FLOOR((fecha_fin - fecha_inicio)::numeric * fraccion_periodo)::integer AS fecha_objetivo
    FROM candidatas
), con_fecha AS (
    SELECT objetivo.*,
           (
             SELECT d.fecha
             FROM semilla_dias_2025 d
             WHERE d.periodo_evaluacion_id = objetivo.periodo_evaluacion_id
               AND EXISTS (
                   SELECT 1
                   FROM db_tp1.horarios_semanales h
                   WHERE h.docente_curso_seccion_id = objetivo.asignacion_id
                     AND h.estado = 'ACTIVO'
                     AND CASE h.dia_semana
                           WHEN 'LUNES' THEN 1 WHEN 'MARTES' THEN 2 WHEN 'MIERCOLES' THEN 3
                           WHEN 'JUEVES' THEN 4 WHEN 'VIERNES' THEN 5 ELSE 0
                         END = d.dia_iso
               )
             ORDER BY ABS(d.fecha - objetivo.fecha_objetivo), d.fecha
             LIMIT 1
           ) AS fecha_evaluacion
    FROM con_objetivo objetivo
)
SELECT configuracion_id, asignacion_id, periodo_evaluacion_id, tipo_evaluacion_id,
       tipo_nombre, numero_evaluacion, fecha_evaluacion
FROM con_fecha
WHERE prioridad = 1 AND fecha_evaluacion IS NOT NULL;

INSERT INTO db_tp1.evaluaciones
    (configuracion_evaluacion_id, docente_curso_seccion_id, periodo_evaluacion_id,
     tipo_evaluacion_id, numero_evaluacion, nombre, fecha_evaluacion, estado,
     creado_por_usuario_id, modificado_por_usuario_id)
SELECT configuracion_id,
       asignacion_id,
       periodo_evaluacion_id,
       tipo_evaluacion_id,
       numero_evaluacion,
       tipo_nombre || ' ' || numero_evaluacion::text,
       fecha_evaluacion,
       'ACTIVO',
       (SELECT MIN(u.id) FROM db_tp1.usuarios u
        JOIN db_tp1.usuarios_roles ur ON ur.usuario_id = u.id
        JOIN db_tp1.roles r ON r.id = ur.rol_id
        WHERE r.nombre = 'ADMIN' AND u.estado = 'ACTIVO'),
       (SELECT MIN(u.id) FROM db_tp1.usuarios u
        JOIN db_tp1.usuarios_roles ur ON ur.usuario_id = u.id
        JOIN db_tp1.roles r ON r.id = ur.rol_id
        WHERE r.nombre = 'ADMIN' AND u.estado = 'ACTIVO')
FROM semilla_evaluaciones_2025
ON CONFLICT (docente_curso_seccion_id, periodo_evaluacion_id, tipo_evaluacion_id, numero_evaluacion)
DO NOTHING;

-- Las asignaciones desactivadas conservan su historial, pero no reciben estas notas simuladas.
DELETE FROM db_tp1.detalle_notas_evaluacion d
USING db_tp1.evaluaciones e,
      db_tp1.docente_curso_seccion dc
WHERE d.evaluacion_id = e.id
  AND e.docente_curso_seccion_id = dc.id
  AND dc.periodo_academico_id = 2
  AND dc.estado <> 'ACTIVO'
  AND e.periodo_evaluacion_id IN (SELECT id FROM db_tp1.periodos_evaluacion WHERE periodo_academico_id = 2)
  AND d.observacion = 'Nota simulada de historial 2025; variacion acotada por trayectoria del alumno';

INSERT INTO db_tp1.detalle_notas_evaluacion
    (evaluacion_id, matricula_id, nota, observacion, estado)
SELECT e.id,
       m.id,
       ROUND(LEAST(19.5, GREATEST(5.0,
           perfil.rendimiento_base
           + ((MOD(ABS(HASHTEXTEXTENDED('curso:' || m.alumno_id::text || ':' || dc.curso_id::text, 2025)), 41)::numeric / 20.0) - 1.0)
           + perfil.tendencia * (pe.numero::numeric - 2.5)
           + ((COALESCE(ape.clases_asistidas::numeric / NULLIF(ape.clases_programadas, 0), 0.94) - 0.94) * 4.0)
           + CASE e.tipo_evaluacion_id WHEN 5 THEN 0.25 WHEN 6 THEN -0.30 ELSE 0.0 END
           + (MOD(ABS(HASHTEXTEXTENDED('examen:' || m.alumno_id::text || ':' || e.id::text, 2025)), 1601)::numeric / 1000.0 - 0.8)
       )), 1),
       'Nota simulada de historial 2025; variacion acotada por trayectoria del alumno',
       'ACTIVO'
FROM db_tp1.evaluaciones e
JOIN db_tp1.docente_curso_seccion dc ON dc.id = e.docente_curso_seccion_id
JOIN db_tp1.periodos_evaluacion pe ON pe.id = e.periodo_evaluacion_id
JOIN db_tp1.matriculas m ON m.seccion_id = dc.seccion_id
                        AND m.periodo_academico_id = dc.periodo_academico_id
                        AND m.estado = 'ACTIVO'
JOIN semilla_perfil_2025 perfil ON perfil.matricula_id = m.id
LEFT JOIN db_tp1.asistencias_periodo_evaluacion ape ON ape.matricula_id = m.id
                                                   AND ape.periodo_evaluacion_id = e.periodo_evaluacion_id
WHERE dc.periodo_academico_id = 2
  AND dc.estado = 'ACTIVO'
  AND e.estado = 'ACTIVO'
  AND e.periodo_evaluacion_id IN (SELECT periodo_evaluacion_id FROM semilla_dias_2025 GROUP BY periodo_evaluacion_id)
ON CONFLICT (evaluacion_id, matricula_id) DO NOTHING;

INSERT INTO db_tp1.notas_curso_periodo_evaluacion
    (matricula_id, curso_id, periodo_evaluacion_id, promedio_curso,
     cantidad_evaluaciones_registradas, observacion, estado)
SELECT d.matricula_id,
       dc.curso_id,
       e.periodo_evaluacion_id,
       ROUND(AVG(d.nota), 2),
       COUNT(DISTINCT e.id)::integer,
       'Promedio consolidado desde evaluaciones simuladas del historial 2025',
       'ACTIVO'
FROM db_tp1.detalle_notas_evaluacion d
JOIN db_tp1.evaluaciones e ON e.id = d.evaluacion_id AND e.estado = 'ACTIVO'
JOIN db_tp1.docente_curso_seccion dc ON dc.id = e.docente_curso_seccion_id AND dc.periodo_academico_id = 2 AND dc.estado = 'ACTIVO'
WHERE d.estado = 'ACTIVO'
GROUP BY d.matricula_id, dc.curso_id, e.periodo_evaluacion_id
ON CONFLICT (matricula_id, curso_id, periodo_evaluacion_id)
DO UPDATE SET promedio_curso = EXCLUDED.promedio_curso,
              cantidad_evaluaciones_registradas = EXCLUDED.cantidad_evaluaciones_registradas,
              observacion = EXCLUDED.observacion,
              estado = 'ACTIVO';

DELETE FROM db_tp1.notas_curso_periodo_evaluacion n
USING db_tp1.matriculas m
WHERE n.matricula_id = m.id
  AND m.periodo_academico_id = 2
  AND n.estado = 'ACTIVO'
  AND n.observacion = 'Promedio consolidado desde evaluaciones simuladas del historial 2025'
  AND NOT EXISTS (
      SELECT 1 FROM db_tp1.docente_curso_seccion dc
      WHERE dc.periodo_academico_id = 2 AND dc.estado = 'ACTIVO'
        AND dc.seccion_id = m.seccion_id AND dc.curso_id = n.curso_id
  );

COMMIT;

SELECT 'asistencias_sesion_2025' AS registro, COUNT(*) AS cantidad
FROM db_tp1.asistencias_sesion a
JOIN db_tp1.matriculas m ON m.id = a.matricula_id
WHERE m.periodo_academico_id = 2 AND a.estado = 'ACTIVO'
UNION ALL
SELECT 'asistencias_resumen_2025', COUNT(*)
FROM db_tp1.asistencias_periodo_evaluacion ape
JOIN db_tp1.matriculas m ON m.id = ape.matricula_id
WHERE m.periodo_academico_id = 2 AND ape.estado = 'ACTIVO'
UNION ALL
SELECT 'evaluaciones_2025', COUNT(*)
FROM db_tp1.evaluaciones e
JOIN db_tp1.docente_curso_seccion dc ON dc.id = e.docente_curso_seccion_id
WHERE dc.periodo_academico_id = 2 AND dc.estado = 'ACTIVO' AND e.estado = 'ACTIVO'
UNION ALL
SELECT 'notas_detalle_2025', COUNT(*)
FROM db_tp1.detalle_notas_evaluacion d
JOIN db_tp1.evaluaciones e ON e.id = d.evaluacion_id
JOIN db_tp1.docente_curso_seccion dc ON dc.id = e.docente_curso_seccion_id
WHERE dc.periodo_academico_id = 2 AND dc.estado = 'ACTIVO' AND d.estado = 'ACTIVO'
UNION ALL
SELECT 'notas_consolidadas_2025', COUNT(*)
FROM db_tp1.notas_curso_periodo_evaluacion n
JOIN db_tp1.matriculas m ON m.id = n.matricula_id
WHERE m.periodo_academico_id = 2 AND n.estado = 'ACTIVO';
