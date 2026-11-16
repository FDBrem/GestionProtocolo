package teutonic.gestionprotocolos.persistence.records;

public record PlantillaCompletaRecord(
        long idPlantilla,
        String nombrePlantilla,
        String familia,
        Long idProducto,
        String metodoDefault,
        String equipoDefault,
        Double pesoMuestraG,
        Double superficieCm2,
        boolean usaNumero,

        //datos del análisis (pueden ser null por el LEFT JOIN)
        Long idAnalisis,
        String nombreAnalisis,
        String tipoValor,
        String unidad,
        Integer orden,
        Double limiteMax,
        String etiquetaNeg,
        String etiquetaPos
) {}
