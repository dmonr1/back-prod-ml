CREATE TABLE db_tp1.tipos_documento (
    id BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(2) NOT NULL UNIQUE,
    descripcion_larga VARCHAR(100) NOT NULL,
    descripcion_corta VARCHAR(30) NOT NULL,
    longitud SMALLINT NOT NULL,
    tipo VARCHAR(15) NOT NULL,
    alcance_nacionalidad VARCHAR(15) NOT NULL,
    longitud_exacta BOOLEAN NOT NULL DEFAULT FALSE,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_tipos_documento_longitud CHECK (longitud > 0),
    CONSTRAINT chk_tipos_documento_tipo CHECK (tipo IN ('NUMERICO', 'ALFANUMERICO')),
    CONSTRAINT chk_tipos_documento_alcance CHECK (alcance_nacionalidad IN ('NACIONAL', 'EXTRANJERO', 'AMBOS')),
    CONSTRAINT chk_tipos_documento_estado CHECK (estado IN ('ACTIVO', 'INACTIVO'))
);

INSERT INTO db_tp1.tipos_documento (
    id, codigo, descripcion_larga, descripcion_corta, longitud, tipo, alcance_nacionalidad, longitud_exacta
) VALUES
    (1, '01', 'DNI', 'DNI', 8, 'NUMERICO', 'NACIONAL', TRUE),
    (2, '04', 'CARNET DE EXTRANJERIA', 'CARNET EXT.', 12, 'ALFANUMERICO', 'EXTRANJERO', FALSE),
    (3, '06', 'REGISTRO UNICO DE CONTRIBUYENTES', 'RUC', 11, 'NUMERICO', 'NACIONAL', TRUE),
    (4, '07', 'PASAPORTE', 'PASAPORTE', 12, 'ALFANUMERICO', 'AMBOS', FALSE),
    (5, '11', 'PARTIDA DE NACIMIENTO - IDENTIDAD', 'P. NAC.', 15, 'ALFANUMERICO', 'NACIONAL', FALSE),
    (6, '00', 'OTROS', 'OTROS', 15, 'ALFANUMERICO', 'EXTRANJERO', FALSE);

ALTER TABLE db_tp1.docentes RENAME COLUMN dni TO numero_documento;
ALTER TABLE db_tp1.alumnos RENAME COLUMN dni TO numero_documento;

ALTER TABLE db_tp1.docentes ALTER COLUMN numero_documento TYPE VARCHAR(15);
ALTER TABLE db_tp1.alumnos ALTER COLUMN numero_documento TYPE VARCHAR(15);

ALTER TABLE db_tp1.docentes ADD COLUMN tipo_documento_id BIGINT NOT NULL DEFAULT 1;
ALTER TABLE db_tp1.alumnos ADD COLUMN tipo_documento_id BIGINT NOT NULL DEFAULT 1;

ALTER TABLE db_tp1.docentes DROP CONSTRAINT IF EXISTS docentes_dni_key;
ALTER TABLE db_tp1.alumnos DROP CONSTRAINT IF EXISTS alumnos_dni_key;

ALTER TABLE db_tp1.docentes
    ADD CONSTRAINT fk_docentes_tipo_documento
    FOREIGN KEY (tipo_documento_id) REFERENCES db_tp1.tipos_documento(id);

ALTER TABLE db_tp1.alumnos
    ADD CONSTRAINT fk_alumnos_tipo_documento
    FOREIGN KEY (tipo_documento_id) REFERENCES db_tp1.tipos_documento(id);

ALTER TABLE db_tp1.docentes
    ADD CONSTRAINT uq_docentes_tipo_numero_documento
    UNIQUE (tipo_documento_id, numero_documento);

ALTER TABLE db_tp1.alumnos
    ADD CONSTRAINT uq_alumnos_tipo_numero_documento
    UNIQUE (tipo_documento_id, numero_documento);

CREATE TRIGGER trg_tipos_documento_mod
BEFORE UPDATE ON db_tp1.tipos_documento
FOR EACH ROW EXECUTE FUNCTION db_tp1.actualizar_fecha_modificacion();

SELECT setval(
    pg_get_serial_sequence('db_tp1.tipos_documento', 'id'),
    (SELECT MAX(id) FROM db_tp1.tipos_documento)
);
