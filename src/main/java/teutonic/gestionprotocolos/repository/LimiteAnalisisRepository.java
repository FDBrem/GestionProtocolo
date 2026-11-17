package teutonic.gestionprotocolos.repository;

import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import teutonic.gestionprotocolos.persistence.records.LimiteAnalisisRecord;

import java.util.List;
import java.util.Optional;

@RegisterConstructorMapper(LimiteAnalisisRecord.class)
public interface LimiteAnalisisRepository {

    @SqlQuery("""
            SELECT
                id_limite AS idLimite,
                id_analisis AS idAnalisis,
                limite_maximo AS limiteMaximo
            FROM limite_analisis
            WHERE id_analisis = :idAnalisis
            """)
    Optional<LimiteAnalisisRecord> buscarPorAnalisis(
            @Bind("idAnalisis") long idAnalisis
    );

    @SqlQuery("""
            SELECT
                id_limite AS idLimite,
                id_analisis AS idAnalisis,
                limite_maximo AS limiteMaximo
            FROM limite_analisis
            ORDER BY id_analisis
            """)
    List<LimiteAnalisisRecord> listarTodos();

    @SqlUpdate("""
            UPDATE limite_analisis
            SET limite_maximo = :limiteMaximo
            WHERE id_analisis = :idAnalisis
            """)
    int actualizar(
            @Bind("idAnalisis") long idAnalisis,
            @Bind("limiteMaximo") double limiteMaximo
    );
}