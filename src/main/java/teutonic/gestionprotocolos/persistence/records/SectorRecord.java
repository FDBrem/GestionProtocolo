package teutonic.gestionprotocolos.persistence.records;

/** Representación de persistencia de un sector. */
public record SectorRecord(
        long idSector,
        String nombre,
        boolean activo,
        String nombreNormalizado
) {}