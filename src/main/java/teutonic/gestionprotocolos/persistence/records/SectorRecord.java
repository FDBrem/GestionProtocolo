package teutonic.gestionprotocolos.persistence.records;

public record SectorRecord(
        long idSector,
        String nombre,
        boolean activo
) {
}
