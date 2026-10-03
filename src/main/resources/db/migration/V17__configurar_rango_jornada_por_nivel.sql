ALTER TABLE db_tp1.periodos_academicos
    ADD COLUMN hora_inicio_jornada_primaria TIME NOT NULL DEFAULT '07:00',
    ADD COLUMN hora_fin_jornada_primaria TIME NOT NULL DEFAULT '18:00',
    ADD COLUMN hora_inicio_jornada_secundaria TIME NOT NULL DEFAULT '07:00',
    ADD COLUMN hora_fin_jornada_secundaria TIME NOT NULL DEFAULT '18:00';

ALTER TABLE db_tp1.periodos_academicos
    ADD CONSTRAINT chk_periodo_jornada_primaria
        CHECK (hora_fin_jornada_primaria > hora_inicio_jornada_primaria),
    ADD CONSTRAINT chk_periodo_jornada_secundaria
        CHECK (hora_fin_jornada_secundaria > hora_inicio_jornada_secundaria);
