package teutonic.gestionprotocolos.repository;

import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import teutonic.gestionprotocolos.persistence.records.SerieMuestraRecord;

import java.util.List;
import java.util.Optional;

@RegisterConstructorMapper(SerieMuestraRecord.class)
public interface SerieMuestraRepository {

    @SqlUpdate("""
            INSERT INTO serie_muestra (id_serie, nombre)
            VALUES (:id, :nombre)
            """)
    int insertRaw(@Bind("id") long id, @Bind("nombre") String nombre);

    default int insert(SerieMuestraRecord serie) {
        return insertRaw(serie.idSerie(), serie.nombre());
    }

    @SqlQuery("""
            SELECT id_serie AS idSerie, nombre
            FROM serie_muestra
            WHERE id_serie = :id
            """)
    Optional<SerieMuestraRecord> findById(@Bind("id") long id);

    @SqlQuery("""
            SELECT id_serie AS idSerie, nombre
            FROM serie_muestra
            ORDER BY nombre
            """)
    List<SerieMuestraRecord> findAll();

    @SqlUpdate("""
            UPDATE serie_muestra SET nombre = :nombre WHERE id_serie = :id
            """)
    int updateRaw(@Bind("id") long id, @Bind("nombre") String nombre);

    default int update(SerieMuestraRecord serie) {
        return updateRaw(serie.idSerie(), serie.nombre());
    }

    @SqlUpdate("""
            DELETE FROM serie_muestra WHERE id_serie = :id
            """)
    int delete(@Bind("id") long id);
}
