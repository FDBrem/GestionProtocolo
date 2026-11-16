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