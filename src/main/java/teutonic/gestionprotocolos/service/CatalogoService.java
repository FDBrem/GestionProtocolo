
package teutonic.gestionprotocolos.service;

import teutonic.gestionprotocolos.model.CatalogoItem;
import teutonic.gestionprotocolos.model.CatalogoTipo;
import teutonic.gestionprotocolos.repository.fake.CatalogoRepository;
import teutonic.gestionprotocolos.service.exception.CatalogoException;

import java.util.List;
import java.util.Locale;

public class CatalogoService {

    private final CatalogoRepository repository;

    public CatalogoService(CatalogoRepository repository) {
        if (repository == null) {
            throw new IllegalArgumentException(
                    "El repositorio es obligatorio."
            );
        }

        this.repository = repository;
    }

    // =====================================================
    // LISTAR
    // =====================================================

    public List<CatalogoItem> listar(CatalogoTipo tipo) {

        validarTipo(tipo);

        return repository.listar(tipo);
    }

    // =====================================================
    // BUSCAR POR NOMBRE
    // =====================================================

    public List<CatalogoItem> buscar(
            CatalogoTipo tipo,
            String criterio
    ) {

        validarTipo(tipo);

        if (criterio == null || criterio.isBlank()) {
            return listar(tipo);
        }

        String busqueda = normalizar(criterio);

        return repository.listar(tipo)
                .stream()
                .filter(item ->
                        normalizar(item.nombre())
                                .contains(busqueda)
                )
                .toList();
    }

    // =====================================================
    // CREAR
    // =====================================================

    public CatalogoItem crear(
            CatalogoTipo tipo,
            String nombre
    ) {

        validarTipo(tipo);

        String nombreValidado = validarNombre(nombre);

        validarDuplicado(tipo, nombreValidado, 0);

        return repository.crear(tipo, nombreValidado);
    }

    // =====================================================
    // ACTUALIZAR
    // =====================================================

    public CatalogoItem actualizar(
            CatalogoTipo tipo,
            long id,
            String nombre
    ) {

        validarTipo(tipo);

        validarExistencia(tipo, id);

        String nombreValidado = validarNombre(nombre);

        validarDuplicado(tipo, nombreValidado, id);

        return repository.actualizar(
                tipo,
                id,
                nombreValidado
        );
    }

    // =====================================================
    // ACTIVAR / DESACTIVAR
    // =====================================================

    public CatalogoItem cambiarEstado(
            CatalogoTipo tipo,
            long id,
            boolean activo
    ) {

        validarTipo(tipo);

        validarExistencia(tipo, id);

        return repository.cambiarEstado(
                tipo,
                id,
                activo
        );
    }

    // =====================================================
    // VALIDACIONES
    // =====================================================

    private void validarTipo(CatalogoTipo tipo) {

        if (tipo == null) {
            throw new CatalogoException(
                    "Debe seleccionar un tipo de catálogo."
            );
        }
    }

    private String validarNombre(String nombre) {

        if (nombre == null || nombre.isBlank()) {
            throw new CatalogoException(
                    "El nombre no puede estar vacío."
            );
        }

        return nombre.strip();
    }

    private void validarExistencia(
            CatalogoTipo tipo,
            long id
    ) {

        if (id <= 0 || repository.buscarPorId(tipo, id).isEmpty()) {
            throw new CatalogoException(
                    "No se encontró el registro seleccionado."
            );
        }
    }

    private void validarDuplicado(
            CatalogoTipo tipo,
            String nombre,
            long idExcluir
    ) {

        if (repository.existeNombre(
                tipo,
                nombre,
                idExcluir
        )) {

            throw new CatalogoException(
                    "Ya existe un registro en "
                            + tipo.getDescripcion()
                            + " con el nombre \""
                            + nombre
                            + "\"."
            );
        }
    }

    private String normalizar(String texto) {

        return texto.strip().toLowerCase(Locale.ROOT);
    }
}
