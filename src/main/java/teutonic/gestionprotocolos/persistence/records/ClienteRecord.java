package teutonic.gestionprotocolos.persistence.records;

/** Representación de persistencia de un cliente. */
public record ClienteRecord(
        long idCliente,
        String nombre,
        boolean activo,
        String nombreNormalizado
) {}
