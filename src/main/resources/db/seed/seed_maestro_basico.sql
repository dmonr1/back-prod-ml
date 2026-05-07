SET search_path TO db_tp1;

-- =========================================
-- ROLES
-- =========================================
INSERT INTO roles (nombre, descripcion, estado, fecha_registro, fecha_modificacion)
VALUES
    ('ADMIN', 'Administrador del sistema', 'ACTIVO', NOW(), NOW()),
    ('DOCENTE', 'Docente del colegio', 'ACTIVO', NOW(), NOW()),
    ('TUTOR', 'Docente tutor', 'ACTIVO', NOW(), NOW())
ON CONFLICT (nombre) DO NOTHING;

-- =========================================
-- NIVELES
-- =========================================
INSERT INTO niveles (nombre, descripcion, estado, fecha_registro, fecha_modificacion)
VALUES
    ('PRIMARIA', 'Nivel primaria', 'ACTIVO', NOW(), NOW()),
    ('SECUNDARIA', 'Nivel secundaria', 'ACTIVO', NOW(), NOW())
ON CONFLICT (nombre) DO NOTHING;

-- =========================================
-- GRADOS
-- =========================================
INSERT INTO grados (nivel_id, nombre, orden, estado, fecha_registro, fecha_modificacion)
SELECT n.id, v.nombre, v.orden, 'ACTIVO', NOW(), NOW()
FROM niveles n
JOIN (
    VALUES
        ('PRIMARIA', '1RO', 1),
        ('PRIMARIA', '2DO', 2),
        ('PRIMARIA', '3RO', 3),
        ('PRIMARIA', '4TO', 4),
        ('PRIMARIA', '5TO', 5),
        ('PRIMARIA', '6TO', 6),
        ('SECUNDARIA', '1RO', 1),
        ('SECUNDARIA', '2DO', 2),
        ('SECUNDARIA', '3RO', 3),
        ('SECUNDARIA', '4TO', 4),
        ('SECUNDARIA', '5TO', 5)
) AS v(nombre_nivel, nombre, orden)
    ON n.nombre = v.nombre_nivel
ON CONFLICT (nivel_id, nombre) DO NOTHING;

-- =========================================
-- SECCIONES
-- =========================================
INSERT INTO secciones (grado_id, nombre, capacidad, estado, fecha_registro, fecha_modificacion)
SELECT g.id, s.nombre, 35, 'ACTIVO', NOW(), NOW()
FROM grados g
CROSS JOIN (
    VALUES ('A'), ('B'), ('C')
) AS s(nombre)
ON CONFLICT (grado_id, nombre) DO NOTHING;

-- =========================================
-- PERIODO ACADEMICO
-- =========================================
INSERT INTO periodos_academicos (
    nombre, anio, fecha_inicio, fecha_fin, estado, fecha_registro, fecha_modificacion
)
VALUES
    ('ANIO ESCOLAR 2026', 2026, DATE '2026-03-01', DATE '2026-12-20', 'ACTIVO', NOW(), NOW())
ON CONFLICT (anio) DO NOTHING;

-- =========================================
-- BIMESTRES
-- =========================================
INSERT INTO bimestres (
    periodo_academico_id, nombre, numero, fecha_inicio, fecha_fin, estado, fecha_registro, fecha_modificacion
)
SELECT p.id, b.nombre, b.numero, b.fecha_inicio, b.fecha_fin, 'ACTIVO', NOW(), NOW()
FROM periodos_academicos p
JOIN (
    VALUES
        ('BIMESTRE I', 1, DATE '2026-03-01', DATE '2026-05-15'),
        ('BIMESTRE II', 2, DATE '2026-05-16', DATE '2026-07-31'),
        ('BIMESTRE III', 3, DATE '2026-08-01', DATE '2026-10-15'),
        ('BIMESTRE IV', 4, DATE '2026-10-16', DATE '2026-12-20')
) AS b(nombre, numero, fecha_inicio, fecha_fin)
    ON p.anio = 2026
ON CONFLICT (periodo_academico_id, numero) DO NOTHING;

-- =========================================
-- CURSOS PRIMARIA
-- =========================================
INSERT INTO cursos (nombre, descripcion, nivel_id, estado, fecha_registro, fecha_modificacion)
SELECT c.nombre, c.descripcion, n.id, 'ACTIVO', NOW(), NOW()
FROM niveles n
JOIN (
    VALUES
        ('COMUNICACION', 'Curso de comunicacion'),
        ('MATEMATICA', 'Curso de matematica'),
        ('PERSONAL SOCIAL', 'Curso de personal social'),
        ('CIENCIA Y TECNOLOGIA', 'Curso de ciencia y tecnologia'),
        ('ARTE Y CULTURA', 'Curso de arte y cultura'),
        ('EDUCACION FISICA', 'Curso de educacion fisica'),
        ('INGLES', 'Curso de ingles'),
        ('RELIGION', 'Curso de religion')
) AS c(nombre, descripcion)
    ON n.nombre = 'PRIMARIA'
ON CONFLICT (nivel_id, nombre) DO NOTHING;

-- =========================================
-- CURSOS SECUNDARIA
-- =========================================
INSERT INTO cursos (nombre, descripcion, nivel_id, estado, fecha_registro, fecha_modificacion)
SELECT c.nombre, c.descripcion, n.id, 'ACTIVO', NOW(), NOW()
FROM niveles n
JOIN (
    VALUES
        ('COMUNICACION', 'Curso de comunicacion'),
        ('MATEMATICA', 'Curso de matematica'),
        ('CIENCIA Y TECNOLOGIA', 'Curso de ciencia y tecnologia'),
        ('CIENCIAS SOCIALES', 'Curso de ciencias sociales'),
        ('DESARROLLO PERSONAL CIUDADANIA Y CIVICA', 'Curso de desarrollo personal ciudadania y civica'),
        ('INGLES', 'Curso de ingles'),
        ('ARTE Y CULTURA', 'Curso de arte y cultura'),
        ('EDUCACION FISICA', 'Curso de educacion fisica'),
        ('RELIGION', 'Curso de religion')
) AS c(nombre, descripcion)
    ON n.nombre = 'SECUNDARIA'
ON CONFLICT (nivel_id, nombre) DO NOTHING;
