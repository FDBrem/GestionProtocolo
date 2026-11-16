
-- Normalización de nombres para los catálogos existentes.

ALTER TABLE producto
    ADD COLUMN nombre_normalizado TEXT NOT NULL DEFAULT ''
        CHECK (length(trim(nombre_normalizado)) > 0);

ALTER TABLE sector
    ADD COLUMN nombre_normalizado TEXT NOT NULL DEFAULT ''
        CHECK (length(trim(nombre_normalizado)) > 0);

ALTER TABLE granja
    ADD COLUMN nombre_normalizado TEXT NOT NULL DEFAULT ''
        CHECK (length(trim(nombre_normalizado)) > 0);

ALTER TABLE cliente
    ADD COLUMN activo INTEGER NOT NULL DEFAULT 1
        CHECK (activo IN (0, 1));

ALTER TABLE cliente
    ADD COLUMN nombre_normalizado TEXT NOT NULL DEFAULT ''
        CHECK (length(trim(nombre_normalizado)) > 0);

ALTER TABLE tipo_defecto
    ADD COLUMN activo INTEGER NOT NULL DEFAULT 1
        CHECK (activo IN (0, 1));

ALTER TABLE tipo_defecto
    ADD COLUMN nombre_normalizado TEXT NOT NULL DEFAULT ''
        CHECK (length(trim(nombre_normalizado)) > 0);

-- Unicidad global de los nombres normalizados.

CREATE UNIQUE INDEX ux_producto_nombre_normalizado
    ON producto(nombre_normalizado);

CREATE UNIQUE INDEX ux_sector_nombre_normalizado
    ON sector(nombre_normalizado);

CREATE UNIQUE INDEX ux_granja_nombre_normalizado
    ON granja(nombre_normalizado);

CREATE UNIQUE INDEX ux_cliente_nombre_normalizado
    ON cliente(nombre_normalizado);

CREATE UNIQUE INDEX ux_tipo_defecto_nombre_normalizado
    ON tipo_defecto(nombre_normalizado);
