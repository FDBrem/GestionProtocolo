package teutonic.gestionprotocolos.persistence.records;

/** Representación de persistencia de un producto. */
public record ProductoRecord(
        long idProducto,
        String nombre,
        Long idSerie,
        boolean activo,
        String nombreNormalizado
) {}

