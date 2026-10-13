-- Completa evaluaciones, notas y asistencias sinteticas del periodo 2026.
-- Las evaluaciones se calendarizan para los cuatro periodos; notas y asistencias
-- se generan solo hasta el 7 de octubre de 2026. Los datos existentes se conservan.
-- Script idempotente: puede volver a ejecutarse sin duplicar registros.
\set ON_ERROR_STOP on

BEGIN;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM db_tp1.periodos_academicos
        WHERE id = 1 AND anio = 2026
    ) THEN
        RAISE EXCEPTION 'No existe el periodo academico 2026 esperado (id=1).';
    END IF;
    IF NOT EXISTS (
        SELECT 1 FROM db_tp1.usuarios_roles ur
        JOIN db_tp1.roles r ON r.id = ur.rol_id
        JOIN db_tp1.usuarios u ON u.id = ur.usuario_id
        WHERE r.nombre = 'ADMIN' AND u.estado = 'ACTIVO'
    ) THEN
        RAISE EXCEPTION 'Se necesita un usuario administrador activo para registrar la autoria.';
    END IF;
    IF EXISTS (
        SELECT 1 FROM db_tp1.matriculas m
        WHERE m.periodo_academico_id = 1 AND m.estado = 'ACTIVO'
          AND NOT EXISTS (
              SELECT 1 FROM db_tp1.docente_curso_seccion dc
              WHERE dc.periodo_academico_id = 1 AND dc.seccion_id = m.seccion_id
                AND dc.estado = 'ACTIVO'
          )
    ) THEN
        RAISE EXCEPTION 'Hay matriculas activas 2026 sin asignaciones docentes activas.';
    END IF;
END $$;

CREATE TEMP TABLE semilla_dias_2026 ON COMMIT DROP AS
SELECT pe.id AS periodo_evaluacion_id,
       gs::date AS fecha,
       EXTRACT(ISODOW FROM gs)::integer AS dia_iso
FROM db_tp1.periodos_evaluacion pe
CROSS JOIN LATERAL generate_series(pe.fecha_inicio::timestamp, pe.fecha_fin::timestamp, INTERVAL '1 day') gs
WHERE pe.periodo_academico_id = 1
  AND EXTRACT(ISODOW FROM gs) BETWEEN 1 AND 5
  AND gs::date NOT IN (
      DATE '2026-04-02', DATE '2026-04-03', DATE '2026-05-01',
      DATE '2026-06-29', DATE '2026-07-23', DATE '2026-07-28',
      DATE '2026-07-29', DATE '2026-08-06', DATE '2026-10-08',
      DATE '2026-11-01', DATE '2026-12-08', DATE '2026-12-09'
  );

CREATE TEMP TABLE semilla_perfil_alumno_2026 ON COMMIT DROP AS
WITH historial_alumno AS (
    SELECT m25.alumno_id,
           AVG(n25.promedio_curso)::numeric AS promedio_2025
    FROM db_tp1.matriculas m25
    JOIN db_tp1.notas_curso_periodo_evaluacion n25
      ON n25.matricula_id = m25.id AND n25.estado = 'ACTIVO'
    WHERE m25.periodo_academico_id = 2 AND m25.estado = 'ACTIVO'
    GROUP BY m25.alumno_id
)
SELECT m.id AS matricula_id,
       m.alumno_id,
       COALESCE(h.promedio_2025,
           10.0 + MOD(ABS(HASHTEXTEXTENDED('perfil-alumno-2026:' || m.alumno_id::text, 2026)), 700)::numeric / 100.0
       ) AS rendimiento_base,
       ((MOD(ABS(HASHTEXTEXTENDED('tendencia-alumno-2026:' || m.alumno_id::text, 2026)), 121)::numeric - 60) / 100.0)::numeric(5,3) AS tendencia
FROM db_tp1.matriculas m
LEFT JOIN historial_alumno h ON h.alumno_id = m.alumno_id
WHERE m.periodo_academico_id = 1 AND m.estado = 'ACTIVO';

-- Genera evaluaciones que falten a partir de la configuración activa de cada curso.
CREATE TEMP TABLE semilla_evaluaciones_2026 ON COMMIT DROP AS
WITH candidatas AS (
    SELECT ce.id AS configuracion_id,
           dc.id AS asignacion_id,
           pe.id AS periodo_evaluacion_id,
           ce.tipo_evaluacion_id,
           te.nombre AS tipo_nombre,
           ce.cantidad_evaluaciones,
           serie.numero_evaluacion,
           pe.fecha_inicio,
           pe.fecha_fin,
           CASE
             WHEN ce.tipo_evaluacion_id = 1 AND ce.cantidad_evaluaciones > 1
               THEN 0.10 + 0.80 * serie.numero_evaluacion::numeric / (ce.cantidad_evaluaciones + 1)
             WHEN ce.tipo_evaluacion_id = 1 THEN 0.35
             WHEN ce.tipo_evaluacion_id = 5 THEN 0.55
             WHEN ce.tipo_evaluacion_id = 6 THEN 0.78
             ELSE 0.50
           END AS fraccion_periodo,
           ROW_NUMBER() OVER (
               PARTITION BY dc.id, pe.id, ce.tipo_evaluacion_id, serie.numero_evaluacion
               ORDER BY CASE WHEN ce.grado_id = s.grado_id THEN 0 ELSE 1 END, ce.id
           ) AS prioridad
    FROM db_tp1.configuraciones_evaluacion ce
    JOIN db_tp1.periodos_evaluacion pe ON pe.id = ce.periodo_evaluacion_id
    JOIN db_tp1.docente_curso_seccion dc
      ON dc.periodo_academico_id = ce.periodo_academico_id
     AND dc.curso_id = ce.curso_id AND dc.estado = 'ACTIVO'
    JOIN db_tp1.secciones s ON s.id = dc.seccion_id
    JOIN db_tp1.tipos_evaluacion te ON te.id = ce.tipo_evaluacion_id AND te.estado = 'ACTIVO'
    CROSS JOIN LATERAL generate_series(1, ce.cantidad_evaluaciones) AS serie(numero_evaluacion)
    WHERE ce.periodo_academico_id = 1
      AND pe.periodo_academico_id = 1
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
               FROM semilla_dias_2026 d
               WHERE d.periodo_evaluacion_id = objetivo.periodo_evaluacion_id
                 AND EXISTS (
                     SELECT 1 FROM db_tp1.horarios_semanales h
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
SELECT p.configuracion_id, p.asignacion_id, p.periodo_evaluacion_id,
       p.tipo_evaluacion_id, p.numero_evaluacion,
       p.tipo_nombre || ' ' || p.numero_evaluacion::text,
       p.fecha_evaluacion, 'ACTIVO',
       (SELECT MIN(u.id) FROM db_tp1.usuarios u
        JOIN db_tp1.usuarios_roles ur ON ur.usuario_id = u.id
        JOIN db_tp1.roles r ON r.id = ur.rol_id
        WHERE r.nombre = 'ADMIN' AND u.estado = 'ACTIVO'),
       (SELECT MIN(u.id) FROM db_tp1.usuarios u
        JOIN db_tp1.usuarios_roles ur ON ur.usuario_id = u.id
        JOIN db_tp1.roles r ON r.id = ur.rol_id
        WHERE r.nombre = 'ADMIN' AND u.estado = 'ACTIVO')
FROM semilla_evaluaciones_2026 p
ON CONFLICT (docente_curso_seccion_id, periodo_evaluacion_id, tipo_evaluacion_id, numero_evaluacion)
DO NOTHING;

-- Completa las fechas pendientes sin cambiar las fechas que ya estaban registradas.
UPDATE db_tp1.evaluaciones e
SET fecha_evaluacion = p.fecha_evaluacion
FROM semilla_evaluaciones_2026 p
WHERE e.docente_curso_seccion_id = p.asignacion_id
  AND e.periodo_evaluacion_id = p.periodo_evaluacion_id
  AND e.tipo_evaluacion_id = p.tipo_evaluacion_id
  AND e.numero_evaluacion = p.numero_evaluacion
  AND e.estado = 'ACTIVO'
  AND e.fecha_evaluacion IS NULL;

-- Respaldo para evaluaciones activas antiguas que no tengan una fila de configuración coincidente.
WITH pendientes AS (
    SELECT e.id, e.docente_curso_seccion_id, e.periodo_evaluacion_id,
           pe.fecha_inicio, pe.fecha_fin,
           ROW_NUMBER() OVER (PARTITION BY e.docente_curso_seccion_id, e.periodo_evaluacion_id ORDER BY e.id) AS orden,
           COUNT(*) OVER (PARTITION BY e.docente_curso_seccion_id, e.periodo_evaluacion_id) AS cantidad
    FROM db_tp1.evaluaciones e
    JOIN db_tp1.periodos_evaluacion pe ON pe.id = e.periodo_evaluacion_id
    JOIN db_tp1.docente_curso_seccion dc ON dc.id = e.docente_curso_seccion_id AND dc.estado = 'ACTIVO'
    WHERE dc.periodo_academico_id = 1 AND e.estado = 'ACTIVO' AND e.fecha_evaluacion IS NULL
), con_fecha AS (
    SELECT p.id,
           (
               SELECT d.fecha
               FROM semilla_dias_2026 d
               WHERE d.periodo_evaluacion_id = p.periodo_evaluacion_id
                 AND EXISTS (
                     SELECT 1 FROM db_tp1.horarios_semanales h
                     WHERE h.docente_curso_seccion_id = p.docente_curso_seccion_id
                       AND h.estado = 'ACTIVO'
                       AND CASE h.dia_semana
                             WHEN 'LUNES' THEN 1 WHEN 'MARTES' THEN 2 WHEN 'MIERCOLES' THEN 3
                             WHEN 'JUEVES' THEN 4 WHEN 'VIERNES' THEN 5 ELSE 0
                           END = d.dia_iso
                 )
               ORDER BY ABS(d.fecha - (p.fecha_inicio + FLOOR((p.fecha_fin - p.fecha_inicio)::numeric * p.orden / (p.cantidad + 1))::integer)), d.fecha
               LIMIT 1
           ) AS fecha_evaluacion
    FROM pendientes p
)
UPDATE db_tp1.evaluaciones e
SET fecha_evaluacion = f.fecha_evaluacion
FROM con_fecha f
WHERE e.id = f.id AND f.fecha_evaluacion IS NOT NULL;

-- Las asistencias se calculan por alumno y fecha: su estado se repite en sus clases del día.
CREATE TEMP TABLE semilla_asistencias_2026 ON COMMIT DROP AS
WITH clases AS (
    SELECT d.periodo_evaluacion_id, d.fecha, d.dia_iso,
           dc.id AS asignacion_id, h.id AS horario_id, b.hora_inicio,
           m.id AS matricula_id, perfil.rendimiento_base,
           ROW_NUMBER() OVER (PARTITION BY m.id, d.fecha ORDER BY b.hora_inicio, h.id) AS numero_clase_dia
    FROM semilla_dias_2026 d
    JOIN db_tp1.horarios_semanales h ON h.estado = 'ACTIVO'
    JOIN db_tp1.bloques_horarios b ON b.id = h.bloque_horario_id AND b.estado = 'ACTIVO'
    JOIN db_tp1.docente_curso_seccion dc ON dc.id = h.docente_curso_seccion_id AND dc.estado = 'ACTIVO'
    JOIN db_tp1.matriculas m ON m.seccion_id = dc.seccion_id
                            AND m.periodo_academico_id = dc.periodo_academico_id
                            AND m.estado = 'ACTIVO'
    JOIN semilla_perfil_alumno_2026 perfil ON perfil.matricula_id = m.id
    WHERE dc.periodo_academico_id = 1
      AND d.fecha <= DATE '2026-10-07'
      AND CASE h.dia_semana
            WHEN 'LUNES' THEN 1 WHEN 'MARTES' THEN 2 WHEN 'MIERCOLES' THEN 3
            WHEN 'JUEVES' THEN 4 WHEN 'VIERNES' THEN 5 ELSE 0
          END = d.dia_iso
), deterministas AS (
    SELECT clases.*,
           MOD(ABS(HASHTEXTEXTENDED('ausencia-2026:' || matricula_id::text || ':' || fecha::text, 2026)), 10000)::numeric / 100.0 AS azar_ausencia,
           MOD(ABS(HASHTEXTEXTENDED('justificada-2026:' || matricula_id::text || ':' || fecha::text, 2026)), 100)::integer AS azar_justificada,
           MOD(ABS(HASHTEXTEXTENDED('tardanza-2026:' || matricula_id::text || ':' || fecha::text, 2026)), 100)::integer AS azar_tardanza
    FROM clases
), propuesta AS (
    SELECT d.*,
           CASE
             WHEN azar_ausencia < CASE
                    WHEN rendimiento_base < 10.5 THEN 11.0
                    WHEN rendimiento_base < 12.5 THEN 7.0
                    WHEN rendimiento_base < 15.0 THEN 4.0
                    ELSE 2.0
                  END
               THEN CASE WHEN azar_justificada < 22 THEN 'JUSTIFICADO' ELSE 'AUSENTE' END
             WHEN numero_clase_dia = 1 AND azar_tardanza < CASE
                    WHEN rendimiento_base < 10.5 THEN 7
                    WHEN rendimiento_base < 13.5 THEN 5
                    ELSE 3
                  END
               THEN 'TARDANZA'
             ELSE 'PRESENTE'
           END AS estado_propuesto
    FROM deterministas d
)
SELECT p.periodo_evaluacion_id, p.fecha, p.asignacion_id, p.horario_id, p.matricula_id,
       COALESCE(
           (SELECT a.estado_asistencia
            FROM db_tp1.asistencias_sesion a
            WHERE a.matricula_id = p.matricula_id AND a.fecha_clase = p.fecha AND a.estado = 'ACTIVO'
            ORDER BY a.id LIMIT 1),
           p.estado_propuesto
       ) AS estado_asistencia
FROM propuesta p;

INSERT INTO db_tp1.configuraciones_asistencia_periodo
    (docente_curso_seccion_id, periodo_evaluacion_id, clases_programadas, estado)
SELECT dc.id, d.periodo_evaluacion_id, COUNT(*)::integer, 'ACTIVO'
FROM db_tp1.docente_curso_seccion dc
JOIN db_tp1.horarios_semanales h ON h.docente_curso_seccion_id = dc.id AND h.estado = 'ACTIVO'
JOIN semilla_dias_2026 d ON d.fecha <= DATE '2026-10-07'
  AND CASE h.dia_semana
        WHEN 'LUNES' THEN 1 WHEN 'MARTES' THEN 2 WHEN 'MIERCOLES' THEN 3
        WHEN 'JUEVES' THEN 4 WHEN 'VIERNES' THEN 5 ELSE 0
      END = d.dia_iso
WHERE dc.periodo_academico_id = 1 AND dc.estado = 'ACTIVO'
GROUP BY dc.id, d.periodo_evaluacion_id
ON CONFLICT (docente_curso_seccion_id, periodo_evaluacion_id)
DO UPDATE SET clases_programadas = EXCLUDED.clases_programadas, estado = 'ACTIVO';

INSERT INTO db_tp1.asistencias_sesion
    (docente_curso_seccion_id, periodo_evaluacion_id, matricula_id, fecha_clase,
     estado_asistencia, observacion, usuario_registro_id, estado, horario_semanal_id)
SELECT a.asignacion_id, a.periodo_evaluacion_id, a.matricula_id, a.fecha,
       a.estado_asistencia,
       CASE a.estado_asistencia
         WHEN 'PRESENTE' THEN NULL
         WHEN 'TARDANZA' THEN 'Tardanza simulada para historial 2026 hasta 2026-10-07'
         WHEN 'AUSENTE' THEN 'Inasistencia simulada para historial 2026 hasta 2026-10-07'
         ELSE 'Inasistencia justificada simulada para historial 2026 hasta 2026-10-07'
       END,
       (SELECT MIN(u.id) FROM db_tp1.usuarios u
        JOIN db_tp1.usuarios_roles ur ON ur.usuario_id = u.id
        JOIN db_tp1.roles r ON r.id = ur.rol_id
        WHERE r.nombre = 'ADMIN' AND u.estado = 'ACTIVO'),
       'ACTIVO', a.horario_id
FROM semilla_asistencias_2026 a
ON CONFLICT DO NOTHING;

INSERT INTO db_tp1.asistencias_periodo_evaluacion
    (matricula_id, periodo_evaluacion_id, clases_programadas, clases_asistidas, observacion, estado)
SELECT a.matricula_id, a.periodo_evaluacion_id,
       COUNT(*) FILTER (WHERE a.estado_asistencia <> 'JUSTIFICADO')::integer,
       COUNT(*) FILTER (WHERE a.estado_asistencia IN ('PRESENTE', 'TARDANZA'))::integer,
       'Consolidado de asistencias simuladas 2026 hasta 2026-10-07', 'ACTIVO'
FROM db_tp1.asistencias_sesion a
JOIN db_tp1.matriculas m ON m.id = a.matricula_id AND m.periodo_academico_id = 1
WHERE a.estado = 'ACTIVO' AND a.fecha_clase <= DATE '2026-10-07'
GROUP BY a.matricula_id, a.periodo_evaluacion_id
ON CONFLICT (matricula_id, periodo_evaluacion_id)
DO UPDATE SET clases_programadas = EXCLUDED.clases_programadas,
              clases_asistidas = EXCLUDED.clases_asistidas,
              observacion = EXCLUDED.observacion,
              estado = 'ACTIVO';

-- Base académica: usa primero el promedio 2026 ya cargado, después el curso 2025,
-- luego el promedio general del alumno en 2025 y, por último, una base simulada estable.
CREATE TEMP TABLE semilla_perfil_curso_2026 ON COMMIT DROP AS
WITH curso_2025 AS (
    SELECT m.alumno_id, n.curso_id, AVG(n.promedio_curso)::numeric AS promedio
    FROM db_tp1.matriculas m
    JOIN db_tp1.notas_curso_periodo_evaluacion n ON n.matricula_id = m.id AND n.estado = 'ACTIVO'
    WHERE m.periodo_academico_id = 2 AND m.estado = 'ACTIVO'
    GROUP BY m.alumno_id, n.curso_id
), alumno_2025 AS (
    SELECT m.alumno_id, AVG(n.promedio_curso)::numeric AS promedio
    FROM db_tp1.matriculas m
    JOIN db_tp1.notas_curso_periodo_evaluacion n ON n.matricula_id = m.id AND n.estado = 'ACTIVO'
    WHERE m.periodo_academico_id = 2 AND m.estado = 'ACTIVO'
    GROUP BY m.alumno_id
)
SELECT m.id AS matricula_id, m.alumno_id, dc.curso_id,
       COALESCE(c25.promedio, al25.promedio,
           10.0 + MOD(ABS(HASHTEXTEXTENDED('perfil-curso-2026:' || m.alumno_id::text || ':' || dc.curso_id::text, 2026)), 700)::numeric / 100.0
       ) AS rendimiento_base,
       ((MOD(ABS(HASHTEXTEXTENDED('tendencia-curso-2026:' || m.alumno_id::text || ':' || dc.curso_id::text, 2026)), 101)::numeric - 50) / 100.0)::numeric(5,3) AS tendencia
FROM db_tp1.matriculas m
JOIN db_tp1.docente_curso_seccion dc ON dc.seccion_id = m.seccion_id
                                    AND dc.periodo_academico_id = m.periodo_academico_id
                                    AND dc.estado = 'ACTIVO'
LEFT JOIN curso_2025 c25 ON c25.alumno_id = m.alumno_id AND c25.curso_id = dc.curso_id
LEFT JOIN alumno_2025 al25 ON al25.alumno_id = m.alumno_id
WHERE m.periodo_academico_id = 1 AND m.estado = 'ACTIVO';

INSERT INTO db_tp1.detalle_notas_evaluacion
    (evaluacion_id, matricula_id, nota, observacion, estado)
SELECT e.id, m.id,
       ROUND(LEAST(20.0, GREATEST(5.0,
           perfil.rendimiento_base
           + perfil.tendencia * (pe.numero::numeric - 2.5)
           + ((COALESCE(ape.clases_asistidas::numeric / NULLIF(ape.clases_programadas, 0), 0.94) - 0.94) * 2.0)
           + ((MOD(ABS(HASHTEXTEXTENDED('evaluacion-2026:' || m.alumno_id::text || ':' || dc.curso_id::text || ':' || e.id::text, 2026)), 2001)::numeric / 1000.0) - 1.0)
           + CASE e.tipo_evaluacion_id WHEN 5 THEN 0.20 WHEN 6 THEN -0.25 ELSE 0.0 END
       )), 1),
       'Nota simulada 2026; trayectoria coherente con historial del alumno', 'ACTIVO'
FROM db_tp1.evaluaciones e
JOIN db_tp1.docente_curso_seccion dc ON dc.id = e.docente_curso_seccion_id
JOIN db_tp1.periodos_evaluacion pe ON pe.id = e.periodo_evaluacion_id
JOIN db_tp1.matriculas m ON m.seccion_id = dc.seccion_id
                        AND m.periodo_academico_id = dc.periodo_academico_id
                        AND m.estado = 'ACTIVO'
JOIN semilla_perfil_curso_2026 perfil ON perfil.matricula_id = m.id AND perfil.curso_id = dc.curso_id
LEFT JOIN db_tp1.asistencias_periodo_evaluacion ape ON ape.matricula_id = m.id
                                                   AND ape.periodo_evaluacion_id = e.periodo_evaluacion_id
WHERE dc.periodo_academico_id = 1
  AND dc.estado = 'ACTIVO'
  AND e.estado = 'ACTIVO'
  AND e.fecha_evaluacion <= DATE '2026-10-07'
ON CONFLICT (evaluacion_id, matricula_id) DO NOTHING;

-- Normaliza todas las notas activas del corte con el mismo modelo de trayectoria,
-- incluidas las filas preexistentes, para evitar saltos artificiales entre evaluaciones.
WITH notas_modeladas AS (
    SELECT d.id,
           ROUND(LEAST(19.5, GREATEST(5.0,
               perfil.rendimiento_base
               + perfil.tendencia * (pe.numero::numeric - 2.5)
               + ((COALESCE(ape.clases_asistidas::numeric / NULLIF(ape.clases_programadas, 0), 0.94) - 0.94) * 2.0)
               + ((MOD(ABS(HASHTEXTEXTENDED('evaluacion-2026:' || m.alumno_id::text || ':' || dc.curso_id::text || ':' || e.id::text, 2026)), 2001)::numeric / 1000.0) - 1.0)
               + CASE e.tipo_evaluacion_id WHEN 5 THEN 0.20 WHEN 6 THEN -0.25 ELSE 0.0 END
           )), 1) AS nota_coherente
    FROM db_tp1.detalle_notas_evaluacion d
    JOIN db_tp1.evaluaciones e ON e.id = d.evaluacion_id AND e.estado = 'ACTIVO'
    JOIN db_tp1.docente_curso_seccion dc ON dc.id = e.docente_curso_seccion_id
                                        AND dc.periodo_academico_id = 1 AND dc.estado = 'ACTIVO'
    JOIN db_tp1.matriculas m ON m.id = d.matricula_id AND m.periodo_academico_id = 1 AND m.estado = 'ACTIVO'
    JOIN db_tp1.periodos_evaluacion pe ON pe.id = e.periodo_evaluacion_id
    JOIN semilla_perfil_curso_2026 perfil ON perfil.matricula_id = m.id AND perfil.curso_id = dc.curso_id
    LEFT JOIN db_tp1.asistencias_periodo_evaluacion ape ON ape.matricula_id = m.id
                                                       AND ape.periodo_evaluacion_id = e.periodo_evaluacion_id
    WHERE d.estado = 'ACTIVO' AND e.fecha_evaluacion <= DATE '2026-10-07'
)
UPDATE db_tp1.detalle_notas_evaluacion d
SET nota = m.nota_coherente,
    observacion = 'Nota simulada 2026; trayectoria coherente con historial del alumno'
FROM notas_modeladas m
WHERE d.id = m.id;

-- Mantiene las notas futuras en el historial, pero inactivas para que no contaminen
-- el periodo vigente ni las predicciones generadas hasta la fecha de corte.
UPDATE db_tp1.detalle_notas_evaluacion d
SET estado = 'INACTIVO',
    observacion = 'Nota desactivada por estar fuera del corte 2026-10-07; se conserva en historial'
FROM db_tp1.evaluaciones e
JOIN db_tp1.docente_curso_seccion dc ON dc.id = e.docente_curso_seccion_id
WHERE d.evaluacion_id = e.id
  AND dc.periodo_academico_id = 1
  AND e.estado = 'ACTIVO'
  AND e.fecha_evaluacion > DATE '2026-10-07'
  AND d.estado = 'ACTIVO';

UPDATE db_tp1.notas_curso_periodo_evaluacion n
SET estado = 'INACTIVO',
    promedio_curso = 0,
    cantidad_evaluaciones_registradas = 0,
    observacion = 'Resumen desactivado por estar fuera del corte 2026-10-07; se conserva en historial'
FROM db_tp1.periodos_evaluacion pe
JOIN db_tp1.matriculas m ON m.periodo_academico_id = pe.periodo_academico_id
WHERE n.periodo_evaluacion_id = pe.id
  AND n.matricula_id = m.id
  AND m.periodo_academico_id = 1
  AND pe.fecha_inicio > DATE '2026-10-07'
  AND n.estado = 'ACTIVO';

INSERT INTO db_tp1.notas_curso_periodo_evaluacion
    (matricula_id, curso_id, periodo_evaluacion_id, promedio_curso,
     cantidad_evaluaciones_registradas, observacion, estado)
SELECT d.matricula_id, dc.curso_id, e.periodo_evaluacion_id,
       ROUND(AVG(d.nota), 2), COUNT(DISTINCT e.id)::integer,
       'Promedio consolidado desde notas 2026 hasta 2026-10-07', 'ACTIVO'
FROM db_tp1.detalle_notas_evaluacion d
JOIN db_tp1.evaluaciones e ON e.id = d.evaluacion_id AND e.estado = 'ACTIVO'
JOIN db_tp1.docente_curso_seccion dc ON dc.id = e.docente_curso_seccion_id
                                    AND dc.periodo_academico_id = 1 AND dc.estado = 'ACTIVO'
JOIN db_tp1.matriculas m ON m.id = d.matricula_id AND m.periodo_academico_id = 1 AND m.estado = 'ACTIVO'
WHERE d.estado = 'ACTIVO'
  AND e.fecha_evaluacion <= DATE '2026-10-07'
GROUP BY d.matricula_id, dc.curso_id, e.periodo_evaluacion_id
ON CONFLICT (matricula_id, curso_id, periodo_evaluacion_id)
DO UPDATE SET promedio_curso = EXCLUDED.promedio_curso,
              cantidad_evaluaciones_registradas = EXCLUDED.cantidad_evaluaciones_registradas,
              observacion = EXCLUDED.observacion,
              estado = 'ACTIVO';

COMMIT;

SELECT 'evaluaciones_2026' AS registro, COUNT(*) AS cantidad
FROM db_tp1.evaluaciones e
JOIN db_tp1.docente_curso_seccion dc ON dc.id = e.docente_curso_seccion_id
WHERE dc.periodo_academico_id = 1 AND dc.estado = 'ACTIVO' AND e.estado = 'ACTIVO'
UNION ALL
SELECT 'evaluaciones_sin_fecha', COUNT(*)
FROM db_tp1.evaluaciones e
JOIN db_tp1.docente_curso_seccion dc ON dc.id = e.docente_curso_seccion_id
WHERE dc.periodo_academico_id = 1 AND dc.estado = 'ACTIVO' AND e.estado = 'ACTIVO' AND e.fecha_evaluacion IS NULL
UNION ALL
SELECT 'asistencias_hasta_2026_10_07', COUNT(*)
FROM db_tp1.asistencias_sesion a
JOIN db_tp1.matriculas m ON m.id = a.matricula_id
WHERE m.periodo_academico_id = 1 AND a.estado = 'ACTIVO' AND a.fecha_clase <= DATE '2026-10-07'
UNION ALL
SELECT 'notas_hasta_2026_10_07', COUNT(*)
FROM db_tp1.detalle_notas_evaluacion d
JOIN db_tp1.evaluaciones e ON e.id = d.evaluacion_id AND e.estado = 'ACTIVO'
JOIN db_tp1.docente_curso_seccion dc ON dc.id = e.docente_curso_seccion_id
WHERE dc.periodo_academico_id = 1 AND d.estado = 'ACTIVO' AND e.fecha_evaluacion <= DATE '2026-10-07'
UNION ALL
SELECT 'promedios_curso_2026', COUNT(*)
FROM db_tp1.notas_curso_periodo_evaluacion n
JOIN db_tp1.matriculas m ON m.id = n.matricula_id
WHERE m.periodo_academico_id = 1 AND n.estado = 'ACTIVO';
