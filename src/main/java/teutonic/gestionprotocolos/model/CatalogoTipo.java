
package teutonic.gestionprotocolos.model;

public enum CatalogoTipo {

    GRANJA("Granjas"),
    SECTOR("Sectores"),
    CLIENTE("Clientes");

    private final String descripcion;

    CatalogoTipo(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
