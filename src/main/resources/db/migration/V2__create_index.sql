CREATE INDEX ix_protocolo_fecha     ON protocolo(fecha);
CREATE INDEX ix_muestra_fecha       ON muestra(fecha_toma);
CREATE INDEX ix_muestra_prod_fecha  ON muestra(id_producto, fecha_toma);
CREATE INDEX ix_muestra_granja      ON muestra(id_granja, fecha_toma);
CREATE INDEX ix_muestra_lote        ON muestra(lote);
CREATE INDEX ix_resultado_analisis  ON resultado(id_analisis);
CREATE INDEX ix_control_fecha       ON control_pollo(fecha);
