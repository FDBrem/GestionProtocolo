
package teutonic.gestionprotocolos.repository;

import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import teutonic.gestionprotocolos.persistence.records.SectorRecord;
import teutonic.gestionprotocolos.util.NombreNormalizer;

import java.util.List;
import java.util.Optional;

@RegisterConstructorMapper(SectorRecord.class)
public interface SectorRepository {

    // ---------------------------------------------------------
    // CONSULTAS
    // ---------------------------------------------------------

    @SqlQuery("""
            SELECT
                id_sector AS idSector,
                nombre,
                activo,
                nombre_normalizado AS nombreNormalizado
            FROM sector
            WHERE id_sector = :id
            """)
    Optional<SectorRecord> buscarPorId(
            @Bind("id") long id
    );

    @SqlQuery("""
            SELECT
                id_sector AS idSector,
                nombre,
                activo,
                nombre_normalizado AS nombreNormalizado
            FROM sector
            ORDER BY nombre
            """)
    List<SectorRecord> listarTodas();

    @SqlQuery("""
            SELECT
                id_sector AS idSector,
                nombre,
                activo,
                nombre_normalizado AS nombreNormalizado
            FROM sector
            WHERE activo = 1
            ORDER BY nombre
            """)
    List<SectorRecord> listarActivas();

    // ---------------------------------------------------------
    // BÚSQUEDA POR NOMBRE
    // ---------------------------------------------------------

    @SqlQuery("""
            SELECT
                id_sector AS idSector,
                nombre,
                activo,
                nombre_normalizado AS nombreNormalizado
            FROM sector
            WHERE instr(nombre_normalizado, :nombreNormalizado) > 0
            ORDER BY nombre
            """)
    List<SectorRecord> buscarPorNombreNormalizado(
            @Bind("nombreNormalizado") String nombreNormalizado
    );

    default List<SectorRecord> buscarPorNombre(String nombre) {

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
                FROM sector
                WHERE nombre_normalizado = :nombreNormalizado
            )
            """)
    boolean existeNombreNormalizado(
            @Bind("nombreNormalizado") String nombreNormalizado
    );

    @SqlQuery("""
            SELECT EXISTS (
                SELECT 1
                FROM sector
                WHERE nombre_normalizado = :nombreNormalizado
                  AND id_sector <> :id
            )
            """)
    boolean existeNombreNormalizadoParaOtroSector(
            @Bind("nombreNormalizado") String nombreNormalizado,
            @Bind("id") long id
    );

    default boolean existeNombre(String nombre) {

        String normalizado = NombreNormalizer.normalizar(nombre);

        return !normalizado.isBlank()
                && existeNombreNormalizado(normalizado);
    }

    default boolean existeNombreParaOtroSector(
            String nombre,
            long id
    ) {
        String normalizado = NombreNormalizer.normalizar(nombre);

        return !normalizado.isBlank()
                && existeNombreNormalizadoParaOtroSector(
                normalizado,
                id
        );
    }

    // ---------------------------------------------------------
    // INSERTAR
    // ---------------------------------------------------------

    @SqlUpdate("""
            INSERT INTO sector (
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
    @GetGeneratedKeys("id_sector")
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
                    "El nombre del sector no puede estar vacío"
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
            UPDATE sector
            SET
                nombre = :nombre,
                activo = :activo,
                nombre_normalizado = :nombreNormalizado
            WHERE id_sector = :id
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
                    "El nombre del sector no puede estar vacío"
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
            UPDATE sector
            SET activo = 0
            WHERE id_sector = :id
            """)
    int desactivar(
            @Bind("id") long id
    );

    @SqlUpdate("""
            UPDATE sector
            SET activo = 1
            WHERE id_sector = :id
            """)
    int activar(
            @Bind("id") long id
    );
}
