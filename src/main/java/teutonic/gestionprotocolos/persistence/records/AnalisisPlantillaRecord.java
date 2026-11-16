package teutonic.gestionprotocolos.persistence.records;

public record AnalisisPlantillaRecord(
        long idAnalisis,
        String nombre,
        String tipoValor,
        String unidad,
        int orden,
        Double limiteMax,
        String etiquetaNeg,
        String etiquetaPos
) {}
