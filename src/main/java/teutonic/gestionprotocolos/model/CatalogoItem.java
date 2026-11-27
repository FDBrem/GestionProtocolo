
package teutonic.gestionprotocolos.model;

public record CatalogoItem(
        long id,
        String nombre,
        boolean activo
) {

    public CatalogoItem {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre no puede estar vacío."
            );
        }

        nombre = nombre.strip();
    }
}
