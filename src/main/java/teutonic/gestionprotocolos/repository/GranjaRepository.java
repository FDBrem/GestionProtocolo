package teutonic.gestionprotocolos.repository;

import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import teutonic.gestionprotocolos.persistence.records.GranjaRecord;

import java.util.List;
import java.util.Optional;

@RegisterConstructorMapper(GranjaRecord.class)
public interface GranjaRepository {

    @SqlQuery("""
            SELECT id_granja AS idGranja, nombre, activo
            FROM granja
            WHERE id_granja = :id
            """)
    Optional<GranjaRecord> buscarPorId(@Bind("id") long id);

    @SqlQuery("""
            SELECT id_granja AS idGranja, nombre, activo
            FROM granja
            ORDER BY nombre
            """)
    List<GranjaRecord> listarTodas();

    @SqlQuery("""
            SELECT id_granja AS idGranja, nombre, activo
            FROM granja
            WHERE activo = 1
            ORDER BY nombre
            """)
    List<GranjaRecord> listarActivas();

    @SqlUpdate("""
            INSERT INTO granja (nombre, activo)
            VALUES (:nombre, :activo)
            """)
    @GetGeneratedKeys("id_granja")
    long insertar(
            @Bind("nombre") String nombre,
            @Bind("activo") Integer activo
    );

    @SqlUpdate("""
            UPDATE granja
            SET nombre = :nombre,
                activo = :activo
            WHERE id_granja = :id
            """)
    int actualizar(
            @Bind("id") long id,
            @Bind("nombre") String nombre,
            @Bind("activo") Integer activo
    );
}