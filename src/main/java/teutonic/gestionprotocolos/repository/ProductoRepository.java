
package teutonic.gestionprotocolos.repository;

import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import teutonic.gestionprotocolos.persistence.records.ProductoRecord;
import teutonic.gestionprotocolos.util.NombreNormalizer;

import java.util.List;
import java.util.Optional;

@RegisterConstructorMapper(ProductoRecord.class)
public interface ProductoRepository {

    // ---------------------------------------------------------
    // CONSULTAS
    // ---------------------------------------------------------

    @SqlQuery("""
            SELECT
                id_producto AS idProducto,
                nombre,
                id_serie AS idSerie,
                activo,
                nombre_normalizado AS nombreNormalizado
            FROM producto
            WHERE id_producto = :id
            """)
    Optional<ProductoRecord> buscarPorId(
            @Bind("id") long id
    );

    @SqlQuery("""
            SELECT
                id_producto AS idProducto,
                nombre,
                id_serie AS idSerie,
                activo,
                nombre_normalizado AS nombreNormalizado
            FROM producto
            ORDER BY nombre
            """)
    List<ProductoRecord> listarTodas();

    @SqlQuery("""
            SELECT
                id_producto AS idProducto,
                nombre,
                id_serie AS idSerie,
                activo,
                nombre_normalizado AS nombreNormalizado
            FROM producto
            WHERE activo = 1
            ORDER BY nombre
            """)
    List<ProductoRecord> listarActivas();

    // ---------------------------------------------------------
    // BÚSQUEDA POR NOMBRE
    // ---------------------------------------------------------

    @SqlQuery("""
            SELECT
                id_producto AS idProducto,
                nombre,
                id_serie AS idSerie,
                activo,
                nombre_normalizado AS nombreNormalizado
            FROM producto
            WHERE instr(nombre_normalizado, :nombreNormalizado) > 0
            ORDER BY nombre
            """)
    List<ProductoRecord> buscarPorNombreNormalizado(
            @Bind("nombreNormalizado") String nombreNormalizado
    );

    default List<ProductoRecord> buscarPorNombre(String nombre) {

        String normalizado = NombreNormalizer.normalizar(nombre);

        if (normalizado.isBlank()) {
            return List.of();
        }

        return buscarPorNombreNormalizado(normalizado);
    }

    // ---------------------------------------------------------
    // CONTROL DE DUPLICADOS
    // ---------------------------------------------------------

    @SqlQuery("""
            SELECT EXISTS (
                SELECT 1
                FROM producto
                WHERE nombre_normalizado = :nombreNormalizado
            )
            """)
    boolean existeNombreNormalizado(
            @Bind("nombreNormalizado") String nombreNormalizado
    );

    @SqlQuery("""
            SELECT EXISTS (
                SELECT 1
                FROM producto
                WHERE nombre_normalizado = :nombreNormalizado
                  AND id_producto <> :id
            )
            """)
    boolean existeNombreNormalizadoParaOtroProducto(
            @Bind("nombreNormalizado") String nombreNormalizado,
            @Bind("id") long id
    );

    default boolean existeNombre(String nombre) {

        String normalizado = NombreNormalizer.normalizar(nombre);

        return !normalizado.isBlank()
                && existeNombreNormalizado(normalizado);
    }

    default boolean existeNombreParaOtroProducto(
            String nombre,
            long id
    ) {
        String normalizado = NombreNormalizer.normalizar(nombre);

        return !normalizado.isBlank()
                && existeNombreNormalizadoParaOtroProducto(
                normalizado,
                id
        );
    }

    // ---------------------------------------------------------
    // INSERTAR
    // ---------------------------------------------------------

    @SqlUpdate("""
            INSERT INTO producto (
                nombre,
                id_serie,
                activo,
                nombre_normalizado
            )
            VALUES (
                :nombre,
                :idSerie,
                :activo,
                :nombreNormalizado
            )
            """)
    @GetGeneratedKeys("id_producto")
    long insertarNormalizado(
            @Bind("nombre") String nombre,
            @Bind("idSerie") Long idSerie,
            @Bind("activo") boolean activo,
            @Bind("nombreNormalizado") String nombreNormalizado
    );

    default long insertar(
            String nombre,
            Long idSerie,
            boolean activo
    ) {
        String normalizado = NombreNormalizer.normalizar(nombre);

        if (normalizado.isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre del producto no puede estar vacío"
            );
        }

        return insertarNormalizado(
                nombre,
                idSerie,
                activo,
                normalizado
        );
    }

    // ---------------------------------------------------------
    // ACTUALIZAR
    // ---------------------------------------------------------

    @SqlUpdate("""
            UPDATE producto
            SET
                nombre = :nombre,
                id_serie = :idSerie,
                activo = :activo,
                nombre_normalizado = :nombreNormalizado
            WHERE id_producto = :id
            """)
    int actualizarNormalizado(
            @Bind("id") long id,
            @Bind("nombre") String nombre,
            @Bind("idSerie") Long idSerie,
            @Bind("activo") boolean activo,
            @Bind("nombreNormalizado") String nombreNormalizado
    );

    default int actualizar(
            long id,
            String nombre,
            Long idSerie,
            boolean activo
    ) {
        String normalizado = NombreNormalizer.normalizar(nombre);

        if (normalizado.isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre del producto no puede estar vacío"
            );
        }

        return actualizarNormalizado(
                id,
                nombre,
                idSerie,
                activo,
                normalizado
        );
    }

    // ---------------------------------------------------------
    // BAJA LÓGICA Y REACTIVACIÓN
    // ---------------------------------------------------------

    @SqlUpdate("""
            UPDATE producto
            SET activo = 0
            WHERE id_producto = :id
            """)
    int desactivar(
            @Bind("id") long id
    );

    @SqlUpdate("""
            UPDATE producto
            SET activo = 1
            WHERE id_producto = :id
            """)
    int activar(
            @Bind("id") long id
    );
}
