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