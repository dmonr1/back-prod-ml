ALTER TABLE db_tp1.periodos_academicos
    ADD COLUMN duracion_hora_primaria_minutos INTEGER NOT NULL DEFAULT 50,
    ADD COLUMN duracion_recreo_primaria_minutos INTEGER NOT NULL DEFAULT 20,
    ADD COLUMN duracion_hora_secundaria_minutos INTEGER NOT NULL DEFAULT 90,
    ADD COLUMN duracion_recreo_secundaria_minutos INTEGER NOT NULL DEFAULT 20;

ALTER TABLE db_tp1.periodos_academicos
    ADD CONSTRAINT chk_periodo_duracion_hora_primaria
        CHECK (duracion_hora_primaria_minutos BETWEEN 30 AND 180 AND duracion_hora_primaria_minutos % 5 = 0),
    ADD CONSTRAINT chk_periodo_duracion_recreo_primaria
        CHECK (duracion_recreo_primaria_minutos BETWEEN 5 AND 60 AND duracion_recreo_primaria_minutos % 5 = 0),
    ADD CONSTRAINT chk_periodo_duracion_hora_secundaria
        CHECK (duracion_hora_secundaria_minutos BETWEEN 30 AND 180 AND duracion_hora_secundaria_minutos % 5 = 0),
    ADD CONSTRAINT chk_periodo_duracion_recreo_secundaria
        CHECK (duracion_recreo_secundaria_minutos BETWEEN 5 AND 60 AND duracion_recreo_secundaria_minutos % 5 = 0);
