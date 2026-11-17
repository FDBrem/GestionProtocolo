package teutonic.gestionprotocolos.persistence.records.registro;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record MuestraRequest(
        Long productoId,
        Long serieId,
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
        List<ResultadoRequest> resultados
) {}