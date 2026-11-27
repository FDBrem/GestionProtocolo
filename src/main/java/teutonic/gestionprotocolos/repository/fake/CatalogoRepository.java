
package teutonic.gestionprotocolos.repository.fake;

import teutonic.gestionprotocolos.model.CatalogoItem;
import teutonic.gestionprotocolos.model.CatalogoTipo;

import java.util.List;
import java.util.Optional;

public interface CatalogoRepository {

    List<CatalogoItem> listar(CatalogoTipo tipo);

    Optional<CatalogoItem> buscarPorId(
            CatalogoTipo tipo,
            long id
    );

    boolean existeNombre(
            CatalogoTipo tipo,
            String nombre,
            long idExcluir
    );

    CatalogoItem crear(
            CatalogoTipo tipo,
            String nombre
    );

    CatalogoItem actualizar(
            CatalogoTipo tipo,
            long id,
            String nombre
    );

    CatalogoItem cambiarEstado(
            CatalogoTipo tipo,
            long id,
            boolean activo
    );
}
