
package teutonic.gestionprotocolos.repository;

import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import teutonic.gestionprotocolos.persistence.records.GranjaRecord;
import teutonic.gestionprotocolos.util.NombreNormalizer;

import java.util.List;
import java.util.Optional;

@RegisterConstructorMapper(GranjaRecord.class)
public interface GranjaRepository {

    // ---------------------------------------------------------
    // CONSULTAS
    // ---------------------------------------------------------

    @SqlQuery("""
            SELECT
                id_granja AS idGranja,
                nombre,
                activo,
                nombre_normalizado AS nombreNormalizado
            FROM granja
            WHERE id_granja = :id
            """)
    Optional<GranjaRecord> buscarPorId(
            @Bind("id") long id
    );

    @SqlQuery("""
            SELECT
                id_granja AS idGranja,
                nombre,
                activo,
                nombre_normalizado AS nombreNormalizado
            FROM granja
            ORDER BY nombre
            """)
    List<GranjaRecord> listarTodas();

    @SqlQuery("""
            SELECT
                id_granja AS idGranja,
                nombre,
                activo,
                nombre_normalizado AS nombreNormalizado
            FROM granja
            WHERE activo = 1
            ORDER BY nombre
            """)
    List<GranjaRecord> listarActivas();

    // ---------------------------------------------------------
    // BÚSQUEDA POR NOMBRE
    // ---------------------------------------------------------

    @SqlQuery("""
            SELECT
                id_granja AS idGranja,
                nombre,
                activo,
                nombre_normalizado AS nombreNormalizado
            FROM granja
            WHERE instr(nombre_normalizado, :nombreNormalizado) > 0
            ORDER BY nombre
            """)
    List<GranjaRecord> buscarPorNombreNormalizado(
            @Bind("nombreNormalizado") String nombreNormalizado
    );

    default List<GranjaRecord> buscarPorNombre(String nombre) {

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
                FROM granja
                WHERE nombre_normalizado = :nombreNormalizado
            )
            """)
    boolean existeNombreNormalizado(
            @Bind("nombreNormalizado") String nombreNormalizado
    );

    @SqlQuery("""
            SELECT EXISTS (
                SELECT 1
                FROM granja
                WHERE nombre_normalizado = :nombreNormalizado
                  AND id_granja <> :id
            )
            """)
    boolean existeNombreNormalizadoParaOtraGranja(
            @Bind("nombreNormalizado") String nombreNormalizado,
            @Bind("id") long id
    );

    default boolean existeNombre(String nombre) {

        String normalizado = NombreNormalizer.normalizar(nombre);

        return !normalizado.isBlank()
                && existeNombreNormalizado(normalizado);
    }

    default boolean existeNombreParaOtraGranja(
            String nombre,
            long id
    ) {
        String normalizado = NombreNormalizer.normalizar(nombre);

        return !normalizado.isBlank()
                && existeNombreNormalizadoParaOtraGranja(
                normalizado,
                id
        );
    }

    // ---------------------------------------------------------
    // INSERTAR
    // ---------------------------------------------------------

    @SqlUpdate("""
            INSERT INTO granja (
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
    @GetGeneratedKeys("id_granja")
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
                    "El nombre de la granja no puede estar vacío"
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
            UPDATE granja
            SET
                nombre = :nombre,
                activo = :activo,
                nombre_normalizado = :nombreNormalizado
            WHERE id_granja = :id
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
                    "El nombre de la granja no puede estar vacío"
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
            UPDATE granja
            SET activo = 0
            WHERE id_granja = :id
            """)
    int desactivar(
            @Bind("id") long id
    );

    @SqlUpdate("""
            UPDATE granja
            SET activo = 1
            WHERE id_granja = :id
            """)
    int activar(
            @Bind("id") long id
    );
}
