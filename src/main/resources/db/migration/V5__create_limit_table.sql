CREATE TABLE limite_analisis
(
    id_limite     INTEGER PRIMARY KEY,
    id_analisis   INTEGER NOT NULL UNIQUE
        REFERENCES analisis (id_analisis),
    limite_maximo REAL    NOT NULL
);

INSERT INTO limite_analisis (id_analisis, limite_maximo)
SELECT id_analisis, 50
FROM analisis
WHERE nombre = 'Staphylococcus';

INSERT INTO limite_analisis (id_analisis, limite_maximo)
SELECT id_analisis, 100.000
FROM analisis
WHERE nombre = 'Aerobios';

INSERT INTO limite_analisis (id_analisis, limite_maximo)
SELECT id_analisis, 100
FROM analisis
WHERE nombre = 'Enterobacterias';

ALTER TABLE resultado
    ADD COLUMN limite_aplicado REAL;