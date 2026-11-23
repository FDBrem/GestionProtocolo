package teutonic.gestionprotocolos.persistence.records;

/** Representación de persistencia de un tipo de defecto. */
public record TipoDefectoRecord(
        long idDefecto,
        String grupo,
        String nombre,
        int orden,
        boolean activo,
        String nombreNormalizado
) {}