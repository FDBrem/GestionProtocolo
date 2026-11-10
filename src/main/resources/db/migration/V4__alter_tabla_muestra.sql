DROP TABLE muestra;

CREATE TABLE muestra
(
    id_muestra         INTEGER PRIMARY KEY,
    id_protocolo       INTEGER NOT NULL REFERENCES protocolo (id_protocolo),
    id_producto        INTEGER NOT NULL REFERENCES producto (id_producto),
    id_serie           INTEGER NOT NULL REFERENCES serie_muestra (id_serie),
    numero_correlativo INTEGER NOT NULL,
    numero_muestra     INTEGER NOT NULL,
    fecha_toma         TEXT    NOT NULL,
    hora_toma          TEXT,
    id_granja          INTEGER REFERENCES granja (id_granja),
    lote               TEXT,
    id_sector          INTEGER REFERENCES sector (id_sector),
    elemento           TEXT,
    bolson             TEXT,
    t_carcaza_c        REAL,
    t_proceso_c        REAL,
    t_congelada_c      REAL,
    t_producto_c       REAL,
    anulado            INTEGER NOT NULL DEFAULT 0,
    anulado_motivo     TEXT,
    creado_en          TEXT    NOT NULL DEFAULT (datetime('now', 'localtime')),
    UNIQUE (id_serie, numero_correlativo)
);

CREATE TABLE contador_muestra
(
    id_serie      INTEGER PRIMARY KEY
        REFERENCES serie_muestra (id_serie),
    ultimo_numero INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE contador_protocolo
(
    id_plantilla  INTEGER PRIMARY KEY
        REFERENCES plantilla (id_plantilla),
    ultimo_numero INTEGER NOT NULL DEFAULT 0
);