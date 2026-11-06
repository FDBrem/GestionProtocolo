package teutonic.gestionprotocolos.model;

public record AnalisisPlantilla(
        Analisis analisis,
        int orden,
        Double limiteMax,
        String etiquetaNeg,
        String etiquetaPos
) {}
