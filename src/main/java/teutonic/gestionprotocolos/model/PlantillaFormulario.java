package teutonic.gestionprotocolos.model;

import java.util.List;

public record PlantillaFormulario(
        long idPlantilla,
        String nombre,
        String familia,
        Long idProducto,
        String metodoDefault,
        String equipoDefault,
        Double pesoMuestraG,
        Double superficieCm2,
        boolean usaNumero,
        List<AnalisisPlantilla> analisis
) {}
