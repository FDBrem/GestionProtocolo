package teutonic.gestionprotocolos.persistence.records;

/** Representación de persistencia de una granja. */
public record GranjaRecord(
        long idGranja,
        String nombre,
        boolean activo,
        String nombreNormalizado
) {}
