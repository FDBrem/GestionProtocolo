CREATE TABLE serie_muestra
(
    id_serie INTEGER PRIMARY KEY,
    nombre   TEXT NOT NULL UNIQUE
);

CREATE TABLE producto
(
    id_producto INTEGER PRIMARY KEY,
    nombre      TEXT    NOT NULL UNIQUE,
    id_serie    INTEGER REFERENCES serie_muestra (id_serie),
    activo      INTEGER NOT NULL DEFAULT 1
);

CREATE TABLE sector
(
    id_sector INTEGER PRIMARY KEY,
    nombre    TEXT    NOT NULL UNIQUE,
    activo    INTEGER NOT NULL DEFAULT 1
);

CREATE TABLE granja
(
    id_granja INTEGER PRIMARY KEY,
    nombre    TEXT    NOT NULL UNIQUE,
    activo    INTEGER NOT NULL DEFAULT 1
);

CREATE TABLE cliente
(
    id_cliente INTEGER PRIMARY KEY,
    nombre     TEXT NOT NULL UNIQUE
);

CREATE TABLE analisis
(
    id_analisis INTEGER PRIMARY KEY,
    nombre      TEXT NOT NULL UNIQUE,
    tipo_valor  TEXT NOT NULL CHECK (tipo_valor IN ('RECUENTO', 'PRESENCIA')),
    unidad      TEXT
);

CREATE TABLE plantilla
(
    id_plantilla   INTEGER PRIMARY KEY,
    nombre         TEXT    NOT NULL UNIQUE,
    familia        TEXT    NOT NULL CHECK (familia IN ('MUESTRA_PRODUCTO', 'MONITOREO_AMBIENTAL')),
    id_producto    INTEGER REFERENCES producto (id_producto),
    metodo_default TEXT,
    equipo_default TEXT,
    peso_muestra_g REAL,
    superficie_cm2 REAL,
    usa_numero     INTEGER NOT NULL DEFAULT 1
);

CREATE TABLE plantilla_analisis
(
    id_plantilla INTEGER NOT NULL REFERENCES plantilla (id_plantilla),
    id_analisis  INTEGER NOT NULL REFERENCES analisis (id_analisis),
    orden        INTEGER NOT NULL DEFAULT 0,
    limite_max   REAL,
    etiqueta_neg TEXT,
    etiqueta_pos TEXT,
    PRIMARY KEY (id_plantilla, id_analisis)
);


CREATE TABLE protocolo
(
    id_protocolo   INTEGER PRIMARY KEY,
    id_plantilla   INTEGER NOT NULL REFERENCES plantilla (id_plantilla),
    numero         INTEGER,
    fecha          TEXT    NOT NULL,
    id_cliente     INTEGER REFERENCES cliente (id_cliente),
    metodo         TEXT,
    equipo         TEXT,
    peso_muestra_g REAL,
    superficie_cm2 REAL,
    analista       TEXT,
    observaciones  TEXT,
    anulado        INTEGER NOT NULL DEFAULT 0,
    anulado_motivo TEXT,
    creado_en      TEXT    NOT NULL DEFAULT (datetime('now', 'localtime')),
    UNIQUE (id_plantilla, numero)
);

CREATE TABLE muestra
(
    id_muestra     INTEGER PRIMARY KEY,
    id_protocolo   INTEGER NOT NULL REFERENCES protocolo (id_protocolo),
    id_producto    INTEGER NOT NULL REFERENCES producto (id_producto),
    id_serie       INTEGER REFERENCES serie_muestra (id_serie),
    numero_muestra INTEGER,
    fecha_toma     TEXT    NOT NULL,
    hora_toma      TEXT,
    id_granja      INTEGER REFERENCES granja (id_granja),
    lote           TEXT,
    id_sector      INTEGER REFERENCES sector (id_sector),
    elemento       TEXT,
    bolson         TEXT,
    t_carcaza_c    REAL,
    t_proceso_c    REAL,
    t_congelada_c  REAL,
    t_producto_c   REAL,
    anulado        INTEGER NOT NULL DEFAULT 0,
    anulado_motivo TEXT,
    creado_en      TEXT    NOT NULL DEFAULT (datetime('now', 'localtime')),
    UNIQUE (id_serie, numero_muestra)
);

CREATE TABLE resultado
(
    id_resultado INTEGER PRIMARY KEY,
    id_muestra   INTEGER NOT NULL REFERENCES muestra (id_muestra),
    id_analisis  INTEGER NOT NULL REFERENCES analisis (id_analisis),
    hora_control TEXT    NOT NULL DEFAULT '',
    valor        REAL,
    menor_que    INTEGER NOT NULL DEFAULT 0,
    colonias     INTEGER,
    presencia    INTEGER,
    conforme     INTEGER,
    pendiente    INTEGER NOT NULL DEFAULT 0,
    observacion  TEXT,
    creado_en    TEXT    NOT NULL DEFAULT (datetime('now', 'localtime')),
    UNIQUE (id_muestra, id_analisis, hora_control),
    CHECK (pendiente = 1 OR valor IS NOT NULL OR presencia IS NOT NULL),
    CHECK (menor_que = 0 OR valor IS NOT NULL)
);


CREATE TABLE tipo_defecto
(
    id_defecto INTEGER PRIMARY KEY,
    grupo      TEXT    NOT NULL,
    nombre     TEXT    NOT NULL UNIQUE,
    orden      INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE control_pollo
(
    id_control  INTEGER PRIMARY KEY,
    fecha       TEXT    NOT NULL,
    hora        TEXT,
    peso_caja_g REAL,
    cant_pollos INTEGER NOT NULL CHECK (cant_pollos > 0),
    anulado     INTEGER NOT NULL DEFAULT 0,
    creado_en   TEXT    NOT NULL DEFAULT (datetime('now', 'localtime'))
);

CREATE TABLE control_pollo_defecto
(
    id_control INTEGER NOT NULL REFERENCES control_pollo (id_control) ON DELETE CASCADE,
    id_defecto INTEGER NOT NULL REFERENCES tipo_defecto (id_defecto),
    cantidad   INTEGER NOT NULL CHECK (cantidad >= 0),
    PRIMARY KEY (id_control, id_defecto)
);