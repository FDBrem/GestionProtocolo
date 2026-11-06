package teutonic.gestionprotocolos.persistence.records;

public record MuestraRecord(
        Long idMuestra,
        Long idProtocolo,
        Long idProducto,
        Long idSerie,
        Integer numeroMuestra,
        Integer numeroCorrelativo,
        String fechaToma,
        String horaToma,
        Long idGranja,
        String lote,
        Long idSector,
        String elemento,
        String bolson,
        Double temperaturaCarcaza,
        Double temperaturaProceso,
        Double temperaturaCongelada,
        Double temperaturaProducto,
        Integer anulado,
        String anuladoMotivo,
        String creadoEn
) {}
