package teutonic.gestionprotocolos.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record Muestra(
        Long id,
        Long protocoloId,
        Long productoId,
        Long serieId,
        Integer numeroCorrelativo,
        Integer numeroMuestra,
        LocalDate fechaToma,
        LocalTime horaToma,
        Long granjaId,
        String lote,
        Long sectorId,
        String elemento,
        String bolson,
        Double temperaturaCarcaza,
        Double temperaturaProceso,
        Double temperaturaCongelada,
        Double temperaturaProducto,
        boolean anulada,
        String motivoAnulacion,
        LocalDateTime creadaEn
) {}
