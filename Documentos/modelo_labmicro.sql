-- =====================================================================
-- LabMicro - Bonnin Hnos. | Modelo de datos propuesto (SQLite)
-- Fechas: TEXT ISO 'YYYY-MM-DD'. Horas: TEXT 'HH:MM'. Booleanos: INTEGER 0/1.
-- Activar siempre:  PRAGMA foreign_keys = ON;  PRAGMA journal_mode = WAL;
-- =====================================================================
PRAGMA foreign_keys = ON;

-- ---------------------------------------------------------------------
-- 1) CATALOGOS (se cargan una vez; alimentan combos y evitan errores de
--    tipeo como "Gaillard" / "Gallard")
-- ---------------------------------------------------------------------
CREATE TABLE serie_muestra (                  -- serie correlativa de "Muestra N°"
    id_serie      INTEGER PRIMARY KEY,
    nombre        TEXT NOT NULL UNIQUE       -- 'CMS', 'Suprema', 'Trozado', 'Menudos y Garras'...
);                                            -- varios productos pueden compartir una serie

CREATE TABLE producto (
    id_producto   INTEGER PRIMARY KEY,
    nombre        TEXT NOT NULL UNIQUE,      -- Pollo Entero, CMS, Suprema, Menudos de aves, Ambiente...
    id_serie      INTEGER REFERENCES serie_muestra(id_serie),
    activo        INTEGER NOT NULL DEFAULT 1
);

CREATE TABLE sector (                         -- sector de planta / punto de toma
    id_sector     INTEGER PRIMARY KEY,
    nombre        TEXT NOT NULL UNIQUE,      -- Salida Chiller, Trozado, IQF, Planta Subproducto...
    activo        INTEGER NOT NULL DEFAULT 1
);

CREATE TABLE granja (                         -- lo que hoy figura como "Lote Nombre" / "Granja"
    id_granja     INTEGER PRIMARY KEY,
    nombre        TEXT NOT NULL UNIQUE,      -- El Jabali, Tron Marcelo, Don Edel...
    activo        INTEGER NOT NULL DEFAULT 1
);

CREATE TABLE cliente (
    id_cliente    INTEGER PRIMARY KEY,
    nombre        TEXT NOT NULL UNIQUE       -- Piamontesa
);

CREATE TABLE analisis (
    id_analisis   INTEGER PRIMARY KEY,
    nombre        TEXT NOT NULL UNIQUE,      -- Aerobios, E. coli, Coliformes, Enterobacterias,
                                             -- Staphylococcus, Mohos, Levaduras, Listeria,
                                             -- Salmonella spp, Huesos
    tipo_valor    TEXT NOT NULL CHECK (tipo_valor IN ('RECUENTO','PRESENCIA')),
    unidad        TEXT                       -- UFC/g, UFC/cm2 (NULL si es presencia/ausencia)
);

-- Tipo de formulario: define como se ve y que lleva cada planilla
CREATE TABLE plantilla (
    id_plantilla      INTEGER PRIMARY KEY,
    nombre            TEXT NOT NULL UNIQUE,  -- 'CMS', 'Suprema', 'Trozado', 'Aerobios ambiente'...
    familia           TEXT NOT NULL CHECK (familia IN ('MUESTRA_PRODUCTO','MONITOREO_AMBIENTAL')),
    id_producto       INTEGER REFERENCES producto(id_producto),
    metodo_default    TEXT,
    equipo_default    TEXT,                  -- Petrifilm 3M / PCR CFX96 / IQ-Check
    peso_muestra_g    REAL,
    superficie_cm2    REAL,
    usa_numero        INTEGER NOT NULL DEFAULT 1  -- 1: lleva Protocolo N° (serie propia)
);

CREATE TABLE plantilla_analisis (            -- que analisis lleva cada formulario y su limite
    id_plantilla  INTEGER NOT NULL REFERENCES plantilla(id_plantilla),
    id_analisis   INTEGER NOT NULL REFERENCES analisis(id_analisis),
    orden         INTEGER NOT NULL DEFAULT 0,
    limite_max    REAL,                      -- 50, 100, 100000, 5...  (NULL = sin limite)
    etiqueta_neg  TEXT,                      -- 'Ausencia' / 'ND' / 'Negativo'
    etiqueta_pos  TEXT,                      -- 'Presencia' / 'D' / 'Positivo'
    PRIMARY KEY (id_plantilla, id_analisis)
);

-- ---------------------------------------------------------------------
-- 2) TRANSACCIONAL: Protocolo (encabezado impreso) -> Muestra -> Resultado
-- ---------------------------------------------------------------------
CREATE TABLE protocolo (
    id_protocolo    INTEGER PRIMARY KEY,
    id_plantilla    INTEGER NOT NULL REFERENCES plantilla(id_plantilla),
    numero          INTEGER,                 -- Protocolo N° (NULL en planillas sin numero)
    fecha           TEXT NOT NULL,           -- fecha del protocolo / bloque "Fecha:"
    id_cliente      INTEGER REFERENCES cliente(id_cliente),
    metodo          TEXT,                    -- copia vigente al momento del analisis
    equipo          TEXT,
    peso_muestra_g  REAL,
    superficie_cm2  REAL,
    analista        TEXT,                    -- nombre impreso junto a la firma
    observaciones   TEXT,
    anulado         INTEGER NOT NULL DEFAULT 0,
    anulado_motivo  TEXT,
    creado_en       TEXT NOT NULL DEFAULT (datetime('now','localtime')),
    UNIQUE (id_plantilla, numero)
);

CREATE TABLE muestra (
    id_muestra      INTEGER PRIMARY KEY,
    id_protocolo    INTEGER NOT NULL REFERENCES protocolo(id_protocolo),
    id_producto     INTEGER NOT NULL REFERENCES producto(id_producto),
    id_serie        INTEGER REFERENCES serie_muestra(id_serie), -- copia de producto.id_serie (la pone la app)
    numero_muestra  INTEGER,                 -- "Muestra N°": proximo = MAX(numero) + 1 dentro de la serie
    fecha_toma      TEXT NOT NULL,
    hora_toma       TEXT,
    id_granja       INTEGER REFERENCES granja(id_granja),
    lote            TEXT,                    -- si existiera un codigo de lote ademas de la granja
    id_sector       INTEGER REFERENCES sector(id_sector),
    elemento        TEXT,                    -- Pata muslo, Corazones, Cinta Suprema, Desague...
    bolson          TEXT,                    -- N° de bolson (harinas)
    t_carcaza_c     REAL,
    t_proceso_c     REAL,
    t_congelada_c   REAL,
    t_producto_c    REAL,
    anulado         INTEGER NOT NULL DEFAULT 0,
    anulado_motivo  TEXT,
    creado_en       TEXT NOT NULL DEFAULT (datetime('now','localtime')),
    UNIQUE (id_serie, numero_muestra)        -- una muestra anulada conserva su numero (no se reutiliza)
);

CREATE TABLE resultado (
    id_resultado    INTEGER PRIMARY KEY,
    id_muestra      INTEGER NOT NULL REFERENCES muestra(id_muestra),
    id_analisis     INTEGER NOT NULL REFERENCES analisis(id_analisis),
    hora_control    TEXT NOT NULL DEFAULT '',-- opcional: solo si una misma muestra tiene varias lecturas
    valor           REAL,                    -- recuento (UFC/g o UFC/cm2). Si menor_que=1: el umbral (5)
    menor_que       INTEGER NOT NULL DEFAULT 0, -- 1 = se informa "<valor" (ej. "<5"); no se guarda el numero real
    colonias        INTEGER,                 -- conteo bruto (ambiental: 2 colonias / superficie 40 cm2)
    presencia       INTEGER,                 -- 1 detectado/presencia, 0 ausencia/ND/negativo
    conforme        INTEGER,                 -- 1/0/NULL, calculado al guardar vs. limite vigente
    pendiente       INTEGER NOT NULL DEFAULT 0, -- 1 = resultado aun no cargado (lectura a 48 h)
    observacion     TEXT,                    -- ej. huesos detectados: "revisada nuevamente; decomiso/reproceso"
    creado_en       TEXT NOT NULL DEFAULT (datetime('now','localtime')),
    UNIQUE (id_muestra, id_analisis, hora_control),
    CHECK (pendiente = 1 OR valor IS NOT NULL OR presencia IS NOT NULL),
    CHECK (menor_que = 0 OR valor IS NOT NULL)
);

-- ---------------------------------------------------------------------
-- 3) CONTROL DIARIO DE POLLO ENTERO (defectos)
-- ---------------------------------------------------------------------
CREATE TABLE tipo_defecto (
    id_defecto    INTEGER PRIMARY KEY,
    grupo         TEXT NOT NULL,             -- Encintado, Menudo, Viscera, Pluma, Fractura...
    nombre        TEXT NOT NULL UNIQUE,      -- Encintado, Sin menudo, Envase de menudo,
                                             -- Falta higado/panza/cogote, Grasa,
                                             -- Traquita/Cloaca/Hiel/Buche/Intestinos,
                                             -- Resto de plumas, Fracturas, Hematomas, Rotura de piel
    orden         INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE control_pollo (                 -- una fila por caja controlada
    id_control          INTEGER PRIMARY KEY,
    fecha               TEXT NOT NULL,
    hora                TEXT,
    peso_caja_g         REAL,
    cant_pollos         INTEGER NOT NULL CHECK (cant_pollos > 0),  -- pollos en el cajon (8 cajones por dia)
    anulado             INTEGER NOT NULL DEFAULT 0,
    creado_en           TEXT NOT NULL DEFAULT (datetime('now','localtime'))
);

CREATE TABLE control_pollo_defecto (
    id_control    INTEGER NOT NULL REFERENCES control_pollo(id_control) ON DELETE CASCADE,
    id_defecto    INTEGER NOT NULL REFERENCES tipo_defecto(id_defecto),
    cantidad      INTEGER NOT NULL CHECK (cantidad >= 0),
    PRIMARY KEY (id_control, id_defecto)
);

-- ---------------------------------------------------------------------
-- 4) INDICES para busquedas por fecha / producto / trazabilidad
-- ---------------------------------------------------------------------
CREATE INDEX ix_protocolo_fecha     ON protocolo(fecha);
CREATE INDEX ix_muestra_fecha       ON muestra(fecha_toma);
CREATE INDEX ix_muestra_prod_fecha  ON muestra(id_producto, fecha_toma);
CREATE INDEX ix_muestra_granja      ON muestra(id_granja, fecha_toma);
CREATE INDEX ix_muestra_lote        ON muestra(lote);
CREATE INDEX ix_resultado_analisis  ON resultado(id_analisis);
CREATE INDEX ix_control_fecha       ON control_pollo(fecha);

-- ---------------------------------------------------------------------
-- 5) VISTAS para los informes (parametros :desde y :hasta, ISO)
-- ---------------------------------------------------------------------
CREATE VIEW v_resultados AS
SELECT m.fecha_toma, m.hora_toma, p.nombre AS producto, pl.nombre AS formulario,
       pr.numero AS protocolo, m.numero_muestra, g.nombre AS granja, s.nombre AS sector,
       m.elemento, a.nombre AS analisis, a.unidad, r.valor, r.menor_que,
       r.presencia, r.conforme, r.pendiente, r.observacion
FROM resultado r
JOIN muestra m     ON m.id_muestra = r.id_muestra AND m.anulado = 0
JOIN protocolo pr  ON pr.id_protocolo = m.id_protocolo AND pr.anulado = 0
JOIN plantilla pl  ON pl.id_plantilla = pr.id_plantilla
JOIN producto p    ON p.id_producto = m.id_producto
JOIN analisis a    ON a.id_analisis = r.id_analisis
LEFT JOIN granja g ON g.id_granja = m.id_granja
LEFT JOIN sector s ON s.id_sector = m.id_sector;
