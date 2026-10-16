package teutonic.gestionprotocolos.persistence.records;

public record PlantillaRecord(
        long idPlantilla,
        String nombre,
        String familia,
        Long idProducto,
        String metodoDefault,
        String equipoDefault,
        Double pesoMuestraG,
        Double superficieCm2,
        boolean usaNumero
) {
}
