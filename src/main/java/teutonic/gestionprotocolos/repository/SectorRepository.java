package teutonic.gestionprotocolos.repository;

import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import teutonic.gestionprotocolos.persistence.records.SectorRecord;

import java.util.List;
import java.util.Optional;

@RegisterConstructorMapper(SectorRecord.class)
public interface SectorRepository {

    @SqlQuery("""
            SELECT id_sector AS idSector, nombre, activo
            FROM sector
            WHERE id_sector = :id
            """)
    Optional<SectorRecord> buscarPorId(@Bind("id") long id);

    @SqlQuery("""
            SELECT id_sector AS idSector, nombre, activo
            FROM sector
            ORDER BY nombre
            """)
    List<SectorRecord> listarTodas();

    @SqlQuery("""
            SELECT id_sector AS idSector, nombre, activo
            FROM sector
            WHERE activo = 1
            ORDER BY nombre
            """)
    List<SectorRecord> listarActivas();

    @SqlUpdate("""
            INSERT INTO sector (nombre, activo)
            VALUES (:nombre, :activo)
            """)
    @GetGeneratedKeys("id_sector")
    long insertar(
            @Bind("nombre") String nombre,
            @Bind("activo") boolean activo
    );

    @SqlUpdate("""
            INSERT INTO sector (id_sector, nombre, activo)
            VALUES (:id, :nombre, :activo)
            """)
    int insertRaw(
            @Bind("id") long id,
            @Bind("nombre") String nombre,
            @Bind("activo") boolean activo
    );

    @SqlUpdate("""
            UPDATE sector
            SET nombre = :nombre,
                activo = :activo
            WHERE id_sector = :id
            """)
    int actualizar(
            @Bind("id") long id,
            @Bind("nombre") String nombre,
            @Bind("activo") boolean activo
    );

    @SqlUpdate("""
            DELETE FROM sector
            WHERE id_sector = :id
            """)
    int eliminar(@Bind("id") long id);

    //metodos de conveniencia compatiblesh
    default Optional<SectorRecord> findById(long id) {
        return buscarPorId(id);
    }

    default List<SectorRecord> findAll() {
        return listarTodas();
    }

    default int delete(long id) {
        return eliminar(id);
    }

    default int update(SectorRecord sector) {
        return actualizar(sector.idSector(), sector.nombre(), sector.activo());
    }

    default long insert(SectorRecord sector) {
        return insertar(sector.nombre(), sector.activo());
    }
}
