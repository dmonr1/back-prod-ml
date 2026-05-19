CREATE TABLE IF NOT EXISTS db_tp1.configuraciones_asistencia_periodo (
    id BIGSERIAL PRIMARY KEY,
    docente_curso_seccion_id BIGINT NOT NULL,
    periodo_evaluacion_id BIGINT NOT NULL,
    clases_programadas INTEGER NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_config_asistencia_docente_curso_seccion
        FOREIGN KEY (docente_curso_seccion_id)
        REFERENCES db_tp1.docente_curso_seccion(id),
    CONSTRAINT fk_config_asistencia_periodo_evaluacion
        FOREIGN KEY (periodo_evaluacion_id)
        REFERENCES db_tp1.periodos_evaluacion(id),
    CONSTRAINT uq_config_asistencia_periodo
        UNIQUE (docente_curso_seccion_id, periodo_evaluacion_id)
);



select id, nombre, numero
from db_tp1.periodos_evaluacion
order by id;

select id, nombre
from db_tp1.secciones
order by id;

create table predicciones_riesgo_global_backup_20260518 as
select *
from predicciones_riesgo_global
where modelo_version = 'v1';

create table predicciones_riesgo_curso_backup_20260518 as
select *
from predicciones_riesgo_curso
where modelo_version = 'v1';

ALTER TABLE hallazgos_data_mining
ADD COLUMN seccion_id BIGINT,
ADD COLUMN nivel_relevancia VARCHAR(20) NOT NULL DEFAULT 'MEDIO',
ADD COLUMN fuente VARCHAR(30) NOT NULL DEFAULT 'AGREGACION',
ADD COLUMN codigo VARCHAR(50);

ALTER TABLE hallazgos_data_mining
ADD CONSTRAINT fk_hallazgos_seccion
FOREIGN KEY (seccion_id) REFERENCES secciones(id);

ALTER TABLE hallazgos_data_mining
ADD CONSTRAINT chk_hallazgos_estado
CHECK (estado IN ('ACTIVO', 'INACTIVO'));

ALTER TABLE hallazgos_data_mining
ADD CONSTRAINT chk_hallazgos_relevancia
CHECK (nivel_relevancia IN ('ALTO', 'MEDIO', 'BAJO'));

ALTER TABLE hallazgos_data_mining
ADD CONSTRAINT chk_hallazgos_fuente
CHECK (fuente IN ('ML', 'REGLA', 'AGREGACION', 'PATRON'));

