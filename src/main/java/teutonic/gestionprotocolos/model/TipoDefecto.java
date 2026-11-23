package teutonic.gestionprotocolos.model;

public record TipoDefecto(
        long idDefecto,
        String grupo,
        String nombre,
        int orden,
        boolean activo
) {}