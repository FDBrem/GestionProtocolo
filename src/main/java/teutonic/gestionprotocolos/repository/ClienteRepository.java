
package teutonic.gestionprotocolos.repository;

import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import teutonic.gestionprotocolos.persistence.records.ClienteRecord;
import teutonic.gestionprotocolos.util.NombreNormalizer;

import java.util.List;
import java.util.Optional;

@RegisterConstructorMapper(ClienteRecord.class)
public interface ClienteRepository {

    // ---------------------------------------------------------
    // CONSULTAS
    // ---------------------------------------------------------

    @SqlQuery("""
            SELECT
                id_cliente AS idCliente,
                nombre,
                activo,
                nombre_normalizado AS nombreNormalizado
            FROM cliente
            WHERE id_cliente = :id
            """)
    Optional<ClienteRecord> buscarPorId(
            @Bind("id") long id
    );

    @SqlQuery("""
            SELECT
                id_cliente AS idCliente,
                nombre,
                activo,
                nombre_normalizado AS nombreNormalizado
            FROM cliente
            ORDER BY nombre
            """)
    List<ClienteRecord> listarTodas();

    @SqlQuery("""
            SELECT
                id_cliente AS idCliente,
                nombre,
                activo,
                nombre_normalizado AS nombreNormalizado
            FROM cliente
            WHERE activo = 1
            ORDER BY nombre
            """)
    List<ClienteRecord> listarActivos();

    // ---------------------------------------------------------
    // BÚSQUEDA POR NOMBRE
    // ---------------------------------------------------------

    @SqlQuery("""
            SELECT
                id_cliente AS idCliente,
                nombre,
                activo,
                nombre_normalizado AS nombreNormalizado
            FROM cliente
            WHERE instr(nombre_normalizado, :nombreNormalizado) > 0
            ORDER BY nombre
            """)
    List<ClienteRecord> buscarPorNombreNormalizado(
            @Bind("nombreNormalizado") String nombreNormalizado
    );

    default List<ClienteRecord> buscarPorNombre(String nombre) {

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
                FROM cliente
                WHERE nombre_normalizado = :nombreNormalizado
            )
            """)
    boolean existeNombreNormalizado(
            @Bind("nombreNormalizado") String nombreNormalizado
    );

    @SqlQuery("""
            SELECT EXISTS (
                SELECT 1
                FROM cliente
                WHERE nombre_normalizado = :nombreNormalizado
                  AND id_cliente <> :id
            )
            """)
    boolean existeNombreNormalizadoParaOtroCliente(
            @Bind("nombreNormalizado") String nombreNormalizado,
            @Bind("id") long id
    );

    default boolean existeNombre(String nombre) {

        String normalizado = NombreNormalizer.normalizar(nombre);

        return !normalizado.isBlank()
                && existeNombreNormalizado(normalizado);
    }

    default boolean existeNombreParaOtroCliente(
            String nombre,
            long id
    ) {
        String normalizado = NombreNormalizer.normalizar(nombre);

        return !normalizado.isBlank()
                && existeNombreNormalizadoParaOtroCliente(
                normalizado,
                id
        );
    }

    // ---------------------------------------------------------
    // INSERTAR
    // ---------------------------------------------------------

    @SqlUpdate("""
            INSERT INTO cliente (
                nombre,
                activo,
                nombre_normalizado
            )
            VALUES (
                :nombre,
                :activo,
                :nombreNormalizado
            )
            """)
    @GetGeneratedKeys("id_cliente")
    long insertarNormalizado(
            @Bind("nombre") String nombre,
            @Bind("activo") boolean activo,
            @Bind("nombreNormalizado") String nombreNormalizado
    );

    default long insertar(
            String nombre,
            boolean activo
    ) {
        String normalizado = NombreNormalizer.normalizar(nombre);

        if (normalizado.isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre del cliente no puede estar vacío"
            );
        }

        return insertarNormalizado(
                nombre,
                activo,
                normalizado
        );
    }

    // ---------------------------------------------------------
    // ACTUALIZAR
    // ---------------------------------------------------------

    @SqlUpdate("""
            UPDATE cliente
            SET
                nombre = :nombre,
                activo = :activo,
                nombre_normalizado = :nombreNormalizado
            WHERE id_cliente = :id
            """)
    int actualizarNormalizado(
            @Bind("id") long id,
            @Bind("nombre") String nombre,
            @Bind("activo") boolean activo,
            @Bind("nombreNormalizado") String nombreNormalizado
    );

    default int actualizar(
            long id,
            String nombre,
            boolean activo
    ) {
        String normalizado = NombreNormalizer.normalizar(nombre);

        if (normalizado.isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre del cliente no puede estar vacío"
            );
        }

        return actualizarNormalizado(
                id,
                nombre,
                activo,
                normalizado
        );
    }

    // ---------------------------------------------------------
    // BAJA LÓGICA Y REACTIVACIÓN
    // ---------------------------------------------------------

    @SqlUpdate("""
            UPDATE cliente
            SET activo = 0
            WHERE id_cliente = :id
            """)
    int desactivar(
            @Bind("id") long id
    );

    @SqlUpdate("""
            UPDATE cliente
            SET activo = 1
            WHERE id_cliente = :id
            """)
    int activar(
            @Bind("id") long id
    );

    // ---------------------------------------------------------
    // MÉTODOS DE COMPATIBILIDAD
    // ---------------------------------------------------------

    default Optional<ClienteRecord> findById(long id) {
        return buscarPorId(id);
    }

    default List<ClienteRecord> findAll() {
        return listarTodas();
    }

    default long insert(ClienteRecord cliente) {
        return insertar(cliente.nombre(), cliente.activo());
    }

    default int update(ClienteRecord cliente) {
        return actualizar(
                cliente.idCliente(),
                cliente.nombre(),
                cliente.activo()
        );
    }

    /**
     * Compatibilidad con el método anterior.
     * Ahora realiza una baja lógica, no una eliminación física.
     */
    @Deprecated
    default int delete(long id) {
        return desactivar(id);
    }
}
