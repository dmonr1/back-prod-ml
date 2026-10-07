-- Los nombres visibles de un periodo deben conservarse aunque cambien los
-- catalogos o la ficha del docente en años posteriores.
ALTER TABLE db_tp1.docente_curso_seccion
    ADD COLUMN docente_nombre_historico varchar(220),
    ADD COLUMN curso_nombre_historico varchar(150),
    ADD COLUMN seccion_nombre_historico varchar(20),
    ADD COLUMN grado_nombre_historico varchar(100),
    ADD COLUMN nivel_nombre_historico varchar(100),
    ADD COLUMN periodo_nombre_historico varchar(150);

ALTER TABLE db_tp1.tutorias
    ADD COLUMN docente_nombre_historico varchar(220),
    ADD COLUMN seccion_nombre_historico varchar(20),
    ADD COLUMN grado_nombre_historico varchar(100),
    ADD COLUMN nivel_nombre_historico varchar(100),
    ADD COLUMN periodo_nombre_historico varchar(150);

UPDATE db_tp1.docente_curso_seccion dcs SET
    docente_nombre_historico = concat_ws(' ', d.nombres, d.apellidos),
    curso_nombre_historico = c.nombre,
    seccion_nombre_historico = s.nombre,
    grado_nombre_historico = g.nombre,
    nivel_nombre_historico = n.nombre,
    periodo_nombre_historico = p.nombre
FROM db_tp1.docentes d, db_tp1.cursos c, db_tp1.secciones s,
     db_tp1.grados g, db_tp1.niveles n, db_tp1.periodos_academicos p
WHERE dcs.docente_id=d.id AND dcs.curso_id=c.id AND dcs.seccion_id=s.id
  AND s.grado_id=g.id AND g.nivel_id=n.id AND dcs.periodo_academico_id=p.id;

UPDATE db_tp1.tutorias t SET
    docente_nombre_historico = concat_ws(' ', d.nombres, d.apellidos),
    seccion_nombre_historico = s.nombre,
    grado_nombre_historico = g.nombre,
    nivel_nombre_historico = n.nombre,
    periodo_nombre_historico = p.nombre
FROM db_tp1.docentes d, db_tp1.secciones s, db_tp1.grados g,
     db_tp1.niveles n, db_tp1.periodos_academicos p
WHERE t.docente_id=d.id AND t.seccion_id=s.id AND s.grado_id=g.id
  AND g.nivel_id=n.id AND t.periodo_academico_id=p.id;

ALTER TABLE db_tp1.docente_curso_seccion
    ALTER COLUMN docente_nombre_historico SET NOT NULL,
    ALTER COLUMN curso_nombre_historico SET NOT NULL,
    ALTER COLUMN seccion_nombre_historico SET NOT NULL,
    ALTER COLUMN grado_nombre_historico SET NOT NULL,
    ALTER COLUMN nivel_nombre_historico SET NOT NULL,
    ALTER COLUMN periodo_nombre_historico SET NOT NULL;
ALTER TABLE db_tp1.tutorias
    ALTER COLUMN docente_nombre_historico SET NOT NULL,
    ALTER COLUMN seccion_nombre_historico SET NOT NULL,
    ALTER COLUMN grado_nombre_historico SET NOT NULL,
    ALTER COLUMN nivel_nombre_historico SET NOT NULL,
    ALTER COLUMN periodo_nombre_historico SET NOT NULL;

CREATE OR REPLACE FUNCTION db_tp1.capturar_nombres_asignacion()
RETURNS trigger LANGUAGE plpgsql AS $$
BEGIN
    SELECT concat_ws(' ',d.nombres,d.apellidos),c.nombre,s.nombre,g.nombre,n.nombre,p.nombre
    INTO NEW.docente_nombre_historico,NEW.curso_nombre_historico,
         NEW.seccion_nombre_historico,NEW.grado_nombre_historico,
         NEW.nivel_nombre_historico,NEW.periodo_nombre_historico
    FROM db_tp1.docentes d,db_tp1.cursos c,db_tp1.secciones s,
         db_tp1.grados g,db_tp1.niveles n,db_tp1.periodos_academicos p
    WHERE d.id=NEW.docente_id AND c.id=NEW.curso_id AND s.id=NEW.seccion_id
      AND g.id=s.grado_id AND n.id=g.nivel_id AND p.id=NEW.periodo_academico_id;
    RETURN NEW;
END $$;

CREATE OR REPLACE FUNCTION db_tp1.capturar_nombres_tutoria()
RETURNS trigger LANGUAGE plpgsql AS $$
BEGIN
    SELECT concat_ws(' ',d.nombres,d.apellidos),s.nombre,g.nombre,n.nombre,p.nombre
    INTO NEW.docente_nombre_historico,NEW.seccion_nombre_historico,
         NEW.grado_nombre_historico,NEW.nivel_nombre_historico,
         NEW.periodo_nombre_historico
    FROM db_tp1.docentes d,db_tp1.secciones s,db_tp1.grados g,
         db_tp1.niveles n,db_tp1.periodos_academicos p
    WHERE d.id=NEW.docente_id AND s.id=NEW.seccion_id AND g.id=s.grado_id
      AND n.id=g.nivel_id AND p.id=NEW.periodo_academico_id;
    RETURN NEW;
END $$;

CREATE TRIGGER trg_dcs_nombres_historicos
BEFORE INSERT OR UPDATE OF docente_id,curso_id,seccion_id,periodo_academico_id
ON db_tp1.docente_curso_seccion FOR EACH ROW
EXECUTE FUNCTION db_tp1.capturar_nombres_asignacion();

CREATE TRIGGER trg_tutorias_nombres_historicos
BEFORE INSERT OR UPDATE OF docente_id,seccion_id,periodo_academico_id
ON db_tp1.tutorias FOR EACH ROW
EXECUTE FUNCTION db_tp1.capturar_nombres_tutoria();

CREATE TABLE db_tp1.historial_asignaciones_docente (
    id bigserial PRIMARY KEY,
    asignacion_id bigint NOT NULL,
    periodo_academico_id bigint NOT NULL,
    accion varchar(20) NOT NULL,
    fecha_cambio timestamp NOT NULL DEFAULT now(),
    datos_anteriores jsonb,
    datos_nuevos jsonb
);
CREATE INDEX idx_historial_asignaciones_periodo
ON db_tp1.historial_asignaciones_docente(periodo_academico_id,asignacion_id,fecha_cambio);

CREATE TABLE db_tp1.historial_tutorias (
    id bigserial PRIMARY KEY,
    tutoria_id bigint NOT NULL,
    periodo_academico_id bigint NOT NULL,
    accion varchar(20) NOT NULL,
    fecha_cambio timestamp NOT NULL DEFAULT now(),
    datos_anteriores jsonb,
    datos_nuevos jsonb
);
CREATE INDEX idx_historial_tutorias_periodo
ON db_tp1.historial_tutorias(periodo_academico_id,tutoria_id,fecha_cambio);

-- Las filas existentes reciben un punto de partida. No se inventan eventos
-- anteriores a la instalación de esta migración.
INSERT INTO db_tp1.historial_asignaciones_docente
    (asignacion_id,periodo_academico_id,accion,datos_nuevos)
SELECT id,periodo_academico_id,'BASE_INICIAL',to_jsonb(dcs)
FROM db_tp1.docente_curso_seccion dcs;
INSERT INTO db_tp1.historial_tutorias
    (tutoria_id,periodo_academico_id,accion,datos_nuevos)
SELECT id,periodo_academico_id,'BASE_INICIAL',to_jsonb(t)
FROM db_tp1.tutorias t;

CREATE OR REPLACE FUNCTION db_tp1.auditar_asignacion_docente()
RETURNS trigger LANGUAGE plpgsql AS $$
BEGIN
    IF TG_OP='INSERT' THEN
        INSERT INTO db_tp1.historial_asignaciones_docente
            (asignacion_id,periodo_academico_id,accion,datos_nuevos)
        VALUES (NEW.id,NEW.periodo_academico_id,'CREACION',to_jsonb(NEW));
    ELSIF TG_OP='UPDATE' AND (to_jsonb(OLD)-'fecha_modificacion')
                                  IS DISTINCT FROM (to_jsonb(NEW)-'fecha_modificacion') THEN
        INSERT INTO db_tp1.historial_asignaciones_docente
            (asignacion_id,periodo_academico_id,accion,datos_anteriores,datos_nuevos)
        VALUES (NEW.id,NEW.periodo_academico_id,'MODIFICACION',to_jsonb(OLD),to_jsonb(NEW));
    END IF;
    RETURN NEW;
END $$;

CREATE OR REPLACE FUNCTION db_tp1.auditar_tutoria()
RETURNS trigger LANGUAGE plpgsql AS $$
BEGIN
    IF TG_OP='INSERT' THEN
        INSERT INTO db_tp1.historial_tutorias
            (tutoria_id,periodo_academico_id,accion,datos_nuevos)
        VALUES (NEW.id,NEW.periodo_academico_id,'CREACION',to_jsonb(NEW));
    ELSIF TG_OP='UPDATE' AND (to_jsonb(OLD)-'fecha_modificacion')
                                  IS DISTINCT FROM (to_jsonb(NEW)-'fecha_modificacion') THEN
        INSERT INTO db_tp1.historial_tutorias
            (tutoria_id,periodo_academico_id,accion,datos_anteriores,datos_nuevos)
        VALUES (NEW.id,NEW.periodo_academico_id,'MODIFICACION',to_jsonb(OLD),to_jsonb(NEW));
    END IF;
    RETURN NEW;
END $$;

CREATE TRIGGER trg_auditar_asignacion_docente
AFTER INSERT OR UPDATE ON db_tp1.docente_curso_seccion FOR EACH ROW
EXECUTE FUNCTION db_tp1.auditar_asignacion_docente();
CREATE TRIGGER trg_auditar_tutoria
AFTER INSERT OR UPDATE ON db_tp1.tutorias FOR EACH ROW
EXECUTE FUNCTION db_tp1.auditar_tutoria();

-- Las correcciones de catalogos o fichas se reflejan solamente en periodos
-- vigentes o futuros. Las filas historicas y sus nombres quedan congelados.
CREATE OR REPLACE FUNCTION db_tp1.sincronizar_nombres_vigentes()
RETURNS trigger LANGUAGE plpgsql AS $$
BEGIN
    IF TG_TABLE_NAME='docentes' THEN
        UPDATE db_tp1.docente_curso_seccion x
        SET docente_nombre_historico=concat_ws(' ',NEW.nombres,NEW.apellidos)
        FROM db_tp1.periodos_academicos p
        WHERE x.docente_id=NEW.id AND p.id=x.periodo_academico_id
          AND p.anio>=EXTRACT(YEAR FROM CURRENT_DATE);
        UPDATE db_tp1.tutorias x
        SET docente_nombre_historico=concat_ws(' ',NEW.nombres,NEW.apellidos)
        FROM db_tp1.periodos_academicos p
        WHERE x.docente_id=NEW.id AND p.id=x.periodo_academico_id
          AND p.anio>=EXTRACT(YEAR FROM CURRENT_DATE);
    ELSIF TG_TABLE_NAME='cursos' THEN
        UPDATE db_tp1.docente_curso_seccion x
        SET curso_nombre_historico=NEW.nombre
        FROM db_tp1.periodos_academicos p
        WHERE x.curso_id=NEW.id AND p.id=x.periodo_academico_id
          AND p.anio>=EXTRACT(YEAR FROM CURRENT_DATE);
    ELSIF TG_TABLE_NAME='secciones' THEN
        UPDATE db_tp1.docente_curso_seccion x
        SET seccion_nombre_historico=NEW.nombre,
            grado_nombre_historico=g.nombre,nivel_nombre_historico=n.nombre
        FROM db_tp1.periodos_academicos p,db_tp1.grados g,db_tp1.niveles n
        WHERE x.seccion_id=NEW.id AND p.id=x.periodo_academico_id
          AND g.id=NEW.grado_id AND n.id=g.nivel_id
          AND p.anio>=EXTRACT(YEAR FROM CURRENT_DATE);
        UPDATE db_tp1.tutorias x
        SET seccion_nombre_historico=NEW.nombre,
            grado_nombre_historico=g.nombre,nivel_nombre_historico=n.nombre
        FROM db_tp1.periodos_academicos p,db_tp1.grados g,db_tp1.niveles n
        WHERE x.seccion_id=NEW.id AND p.id=x.periodo_academico_id
          AND g.id=NEW.grado_id AND n.id=g.nivel_id
          AND p.anio>=EXTRACT(YEAR FROM CURRENT_DATE);
    ELSIF TG_TABLE_NAME='grados' THEN
        UPDATE db_tp1.docente_curso_seccion x
        SET grado_nombre_historico=NEW.nombre
        FROM db_tp1.secciones s,db_tp1.periodos_academicos p
        WHERE x.seccion_id=s.id AND s.grado_id=NEW.id
          AND p.id=x.periodo_academico_id AND p.anio>=EXTRACT(YEAR FROM CURRENT_DATE);
        UPDATE db_tp1.tutorias x
        SET grado_nombre_historico=NEW.nombre
        FROM db_tp1.secciones s,db_tp1.periodos_academicos p
        WHERE x.seccion_id=s.id AND s.grado_id=NEW.id
          AND p.id=x.periodo_academico_id AND p.anio>=EXTRACT(YEAR FROM CURRENT_DATE);
    ELSIF TG_TABLE_NAME='niveles' THEN
        UPDATE db_tp1.docente_curso_seccion x
        SET nivel_nombre_historico=NEW.nombre
        FROM db_tp1.secciones s,db_tp1.grados g,db_tp1.periodos_academicos p
        WHERE x.seccion_id=s.id AND s.grado_id=g.id AND g.nivel_id=NEW.id
          AND p.id=x.periodo_academico_id AND p.anio>=EXTRACT(YEAR FROM CURRENT_DATE);
        UPDATE db_tp1.tutorias x
        SET nivel_nombre_historico=NEW.nombre
        FROM db_tp1.secciones s,db_tp1.grados g,db_tp1.periodos_academicos p
        WHERE x.seccion_id=s.id AND s.grado_id=g.id AND g.nivel_id=NEW.id
          AND p.id=x.periodo_academico_id AND p.anio>=EXTRACT(YEAR FROM CURRENT_DATE);
    ELSIF TG_TABLE_NAME='periodos_academicos' THEN
        IF NEW.anio>=EXTRACT(YEAR FROM CURRENT_DATE) THEN
            UPDATE db_tp1.docente_curso_seccion
            SET periodo_nombre_historico=NEW.nombre WHERE periodo_academico_id=NEW.id;
            UPDATE db_tp1.tutorias
            SET periodo_nombre_historico=NEW.nombre WHERE periodo_academico_id=NEW.id;
        END IF;
    END IF;
    RETURN NEW;
END $$;

CREATE TRIGGER trg_docentes_nombres_vigentes
AFTER UPDATE OF nombres,apellidos ON db_tp1.docentes FOR EACH ROW
WHEN (OLD.nombres IS DISTINCT FROM NEW.nombres OR OLD.apellidos IS DISTINCT FROM NEW.apellidos)
EXECUTE FUNCTION db_tp1.sincronizar_nombres_vigentes();
CREATE TRIGGER trg_cursos_nombres_vigentes
AFTER UPDATE OF nombre ON db_tp1.cursos FOR EACH ROW
WHEN (OLD.nombre IS DISTINCT FROM NEW.nombre)
EXECUTE FUNCTION db_tp1.sincronizar_nombres_vigentes();
CREATE TRIGGER trg_secciones_nombres_vigentes
AFTER UPDATE OF nombre,grado_id ON db_tp1.secciones FOR EACH ROW
WHEN (OLD.nombre IS DISTINCT FROM NEW.nombre OR OLD.grado_id IS DISTINCT FROM NEW.grado_id)
EXECUTE FUNCTION db_tp1.sincronizar_nombres_vigentes();
CREATE TRIGGER trg_grados_nombres_vigentes
AFTER UPDATE OF nombre ON db_tp1.grados FOR EACH ROW
WHEN (OLD.nombre IS DISTINCT FROM NEW.nombre)
EXECUTE FUNCTION db_tp1.sincronizar_nombres_vigentes();
CREATE TRIGGER trg_niveles_nombres_vigentes
AFTER UPDATE OF nombre ON db_tp1.niveles FOR EACH ROW
WHEN (OLD.nombre IS DISTINCT FROM NEW.nombre)
EXECUTE FUNCTION db_tp1.sincronizar_nombres_vigentes();
CREATE TRIGGER trg_periodos_nombres_vigentes
AFTER UPDATE OF nombre ON db_tp1.periodos_academicos FOR EACH ROW
WHEN (OLD.nombre IS DISTINCT FROM NEW.nombre)
EXECUTE FUNCTION db_tp1.sincronizar_nombres_vigentes();
