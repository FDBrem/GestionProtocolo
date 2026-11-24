
package teutonic.gestionprotocolos.repository;

import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import teutonic.gestionprotocolos.persistence.records.TipoDefectoRecord;
import teutonic.gestionprotocolos.util.NombreNormalizer;

import java.util.List;
import java.util.Optional;

@RegisterConstructorMapper(TipoDefectoRecord.class)
public interface TipoDefectoRepository {

    // ---------------------------------------------------------
    // CONSULTAS
    // ---------------------------------------------------------

    @SqlQuery("""
            SELECT
                id_defecto AS idDefecto,
                grupo,
                nombre,
                orden,
                activo,
                nombre_normalizado AS nombreNormalizado
            FROM tipo_defecto
            WHERE id_defecto = :id
            """)
    Optional<TipoDefectoRecord> buscarPorId(
            @Bind("id") long id
    );

    @SqlQuery("""
            SELECT
                id_defecto AS idDefecto,
                grupo,
                nombre,
                orden,
                activo,
                nombre_normalizado AS nombreNormalizado
            FROM tipo_defecto
            ORDER BY grupo, orden, nombre
            """)
    List<TipoDefectoRecord> listarTodos();

    @SqlQuery("""
            SELECT
                id_defecto AS idDefecto,
                grupo,
                nombre,
                orden,
                activo,
                nombre_normalizado AS nombreNormalizado
            FROM tipo_defecto
            WHERE activo = 1
            ORDER BY grupo, orden, nombre
            """)
    List<TipoDefectoRecord> listarActivos();

    // ---------------------------------------------------------
    // BÚSQUEDA POR NOMBRE
    // ---------------------------------------------------------

    @SqlQuery("""
            SELECT
                id_defecto AS idDefecto,
                grupo,
                nombre,
                orden,
                activo,
                nombre_normalizado AS nombreNormalizado
            FROM tipo_defecto
            WHERE instr(nombre_normalizado, :nombreNormalizado) > 0
            ORDER BY grupo, orden, nombre
            """)
    List<TipoDefectoRecord> buscarPorNombreNormalizado(
            @Bind("nombreNormalizado") String nombreNormalizado
    );

    default List<TipoDefectoRecord> buscarPorNombre(String nombre) {

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
                FROM tipo_defecto
                WHERE nombre_normalizado = :nombreNormalizado
            )
            """)
    boolean existeNombreNormalizado(
            @Bind("nombreNormalizado") String nombreNormalizado
    );

    @SqlQuery("""
            SELECT EXISTS (
                SELECT 1
                FROM tipo_defecto
                WHERE nombre_normalizado = :nombreNormalizado
                  AND id_defecto <> :id
            )
            """)
    boolean existeNombreNormalizadoParaOtroDefecto(
            @Bind("nombreNormalizado") String nombreNormalizado,
            @Bind("id") long id
    );

    default boolean existeNombre(String nombre) {

        String normalizado = NombreNormalizer.normalizar(nombre);

        return !normalizado.isBlank()
                && existeNombreNormalizado(normalizado);
    }

    default boolean existeNombreParaOtroDefecto(
            String nombre,
            long id
    ) {
        String normalizado = NombreNormalizer.normalizar(nombre);

        return !normalizado.isBlank()
                && existeNombreNormalizadoParaOtroDefecto(
                normalizado,
                id
        );
    }

    // ---------------------------------------------------------
    // INSERTAR
    // ---------------------------------------------------------

    @SqlUpdate("""
            INSERT INTO tipo_defecto (
                grupo,
                nombre,
                orden,
                activo,
                nombre_normalizado
            )
            VALUES (
                :grupo,
                :nombre,
                :orden,
                :activo,
                :nombreNormalizado
            )
            """)
    @GetGeneratedKeys("id_defecto")
    long insertarNormalizado(
            @Bind("grupo") String grupo,
            @Bind("nombre") String nombre,
            @Bind("orden") int orden,
            @Bind("activo") boolean activo,
            @Bind("nombreNormalizado") String nombreNormalizado
    );

    default long insertar(
            String grupo,
            String nombre,
            int orden,
            boolean activo
    ) {
        String normalizado = NombreNormalizer.normalizar(nombre);

        if (normalizado.isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre del tipo de defecto no puede estar vacío"
            );
        }

        return insertarNormalizado(
                grupo,
                nombre,
                orden,
                activo,
                normalizado
        );
    }

    // ---------------------------------------------------------
    // ACTUALIZAR
    // ---------------------------------------------------------

    @SqlUpdate("""
            UPDATE tipo_defecto
            SET
                grupo = :grupo,
                nombre = :nombre,
                orden = :orden,
                activo = :activo,
                nombre_normalizado = :nombreNormalizado
            WHERE id_defecto = :id
            """)
    int actualizarNormalizado(
            @Bind("id") long id,
            @Bind("grupo") String grupo,
            @Bind("nombre") String nombre,
            @Bind("orden") int orden,
            @Bind("activo") boolean activo,
            @Bind("nombreNormalizado") String nombreNormalizado
    );

    default int actualizar(
            long id,
            String grupo,
            String nombre,
            int orden,
            boolean activo
    ) {
        String normalizado = NombreNormalizer.normalizar(nombre);

        if (normalizado.isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre del tipo de defecto no puede estar vacío"
            );
        }

        return actualizarNormalizado(
                id,
                grupo,
                nombre,
                orden,
                activo,
                normalizado
        );
    }

    // ---------------------------------------------------------
    // BAJA LÓGICA Y REACTIVACIÓN
    // ---------------------------------------------------------

    @SqlUpdate("""
            UPDATE tipo_defecto
            SET activo = 0
            WHERE id_defecto = :id
            """)
    int desactivar(
            @Bind("id") long id
    );

    @SqlUpdate("""
            UPDATE tipo_defecto
            SET activo = 1
            WHERE id_defecto = :id
            """)
    int activar(
            @Bind("id") long id
    );
}
