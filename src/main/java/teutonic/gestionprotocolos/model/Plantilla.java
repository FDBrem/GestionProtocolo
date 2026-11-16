package teutonic.gestionprotocolos.model;

public record Plantilla(
        long idPlantilla,
        String nombre,
        String familia,
        Long idProducto,
        String metodoDefault,
        String equipoDefault,
        Double pesoMuestraG,
        Double superficieCm2,
        boolean usaNumero
) {}
