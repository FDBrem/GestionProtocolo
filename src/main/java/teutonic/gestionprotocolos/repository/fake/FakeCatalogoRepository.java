
package teutonic.gestionprotocolos.repository.fake;

import teutonic.gestionprotocolos.model.CatalogoItem;
import teutonic.gestionprotocolos.model.CatalogoTipo;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

public class FakeCatalogoRepository implements CatalogoRepository {

    private final Map<CatalogoTipo, Map<Long, CatalogoItem>> catalogos =
            new EnumMap<>(CatalogoTipo.class);

    private final Map<CatalogoTipo, Long> contadores =
            new EnumMap<>(CatalogoTipo.class);

    public FakeCatalogoRepository() {

        for (CatalogoTipo tipo : CatalogoTipo.values()) {
            catalogos.put(tipo, new LinkedHashMap<>());
            contadores.put(tipo, 0L);
        }

        cargarDatosIniciales();
    }

    // =====================================================
    // DATOS INICIALES
    // =====================================================

    private void cargarDatosIniciales() {

        agregarInicial(CatalogoTipo.GRANJA, "Granja Norte", true);
        agregarInicial(CatalogoTipo.GRANJA, "Granja Sur", true);
        agregarInicial(CatalogoTipo.GRANJA, "Granja Central", false);

        agregarInicial(CatalogoTipo.SECTOR, "Recepción", true);
        agregarInicial(CatalogoTipo.SECTOR, "Faena", true);
        agregarInicial(CatalogoTipo.SECTOR, "Trozado", true);
        agregarInicial(CatalogoTipo.SECTOR, "Cámaras", false);

        agregarInicial(CatalogoTipo.CLIENTE, "Bonnin Hnos.", true);
        agregarInicial(CatalogoTipo.CLIENTE, "Cliente Demo 2", true);
        agregarInicial(CatalogoTipo.CLIENTE, "Cliente Demo 3", false);
    }

    private void agregarInicial(
            CatalogoTipo tipo,
            String nombre,
            boolean activo
    ) {

        long id = siguienteId(tipo);

        CatalogoItem item = new CatalogoItem(
                id,
                nombre,
                activo
        );

        catalogos.get(tipo).put(id, item);
    }

    // =====================================================
    // LISTAR
    // =====================================================

    @Override
    public synchronized List<CatalogoItem> listar(
            CatalogoTipo tipo
    ) {

        return List.copyOf(
                catalogo(tipo).values()
        );
    }

    // =====================================================
    // BUSCAR POR ID
    // =====================================================

    @Override
    public synchronized Optional<CatalogoItem> buscarPorId(
            CatalogoTipo tipo,
            long id
    ) {

        return Optional.ofNullable(
                catalogo(tipo).get(id)
        );
    }

    // =====================================================
    // VERIFICAR DUPLICADOS
    // =====================================================

    @Override
    public synchronized boolean existeNombre(
            CatalogoTipo tipo,
            String nombre,
            long idExcluir
    ) {

        String nombreNormalizado = normalizar(nombre);

        return catalogo(tipo).values()
                .stream()
                .anyMatch(item ->
                        item.id() != idExcluir
                                && normalizar(item.nombre())
                                .equalsIgnoreCase(nombreNormalizado)
                );
    }

    // =====================================================
    // CREAR
    // =====================================================

    @Override
    public synchronized CatalogoItem crear(
            CatalogoTipo tipo,
            String nombre
    ) {

        long id = siguienteId(tipo);

        CatalogoItem nuevo = new CatalogoItem(
                id,
                nombre,
                true
        );

        catalogo(tipo).put(id, nuevo);

        return nuevo;
    }

    // =====================================================
    // ACTUALIZAR
    // =====================================================

    @Override
    public synchronized CatalogoItem actualizar(
            CatalogoTipo tipo,
            long id,
            String nombre
    ) {

        CatalogoItem actual = buscarPorId(tipo, id)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "No se encontró el registro."
                        )
                );

        CatalogoItem actualizado = new CatalogoItem(
                id,
                nombre,
                actual.activo()
        );

        catalogo(tipo).put(id, actualizado);

        return actualizado;
    }

    // =====================================================
    // CAMBIAR ESTADO
    // =====================================================

    @Override
    public synchronized CatalogoItem cambiarEstado(
            CatalogoTipo tipo,
            long id,
            boolean activo
    ) {

        CatalogoItem actual = buscarPorId(tipo, id)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "No se encontró el registro."
                        )
                );

        CatalogoItem actualizado = new CatalogoItem(
                actual.id(),
                actual.nombre(),
                activo
        );

        catalogo(tipo).put(id, actualizado);

        return actualizado;
    }

    // =====================================================
    // UTILIDADES
    // =====================================================

    private Map<Long, CatalogoItem> catalogo(
            CatalogoTipo tipo
    ) {

        if (tipo == null) {
            throw new IllegalArgumentException(
                    "El tipo de catálogo es obligatorio."
            );
        }

        return catalogos.get(tipo);
    }

    private long siguienteId(CatalogoTipo tipo) {

        long siguiente = contadores.get(tipo) + 1;

        contadores.put(tipo, siguiente);

        return siguiente;
    }

    private String normalizar(String nombre) {

        if (nombre == null) {
            return "";
        }

        return nombre.strip();
    }
}
