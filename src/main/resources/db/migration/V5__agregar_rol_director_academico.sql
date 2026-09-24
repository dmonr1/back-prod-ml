INSERT INTO db_tp1.roles (nombre, descripcion, estado)
VALUES ('DIRECTOR_ACADEMICO', 'Director academico con gestion de periodos vigentes y futuros', 'ACTIVO')
ON CONFLICT (nombre) DO NOTHING;
