package teutonic.gestionprotocolos.model;

/**
 * Producto del catálogo.
 * idSerie es nullable porque el esquema permite productos sin serie asociada.
 */
public record Producto(
        long idProducto,
        String nombre,
        Long idSerie,
        boolean activo
) {}
