package teutonic.gestionprotocolos.repository;

import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.customizer.Bind;
import teutonic.gestionprotocolos.model.ProtocoloRecord;

import java.util.Optional;

@RegisterRowMapper(ProtocoloMapper.class)
public interface ProtocoloRepository {

    @SqlUpdate("""
            INSERT INTO protocolo
                (id_protocolo, id_plantilla, numero, fecha)
            VALUES
                (:id, :plantilla, :numero, :fecha)
            """)
    int insertRaw(
            @Bind("id") long id,
            @Bind("plantilla") long plantilla,
            @Bind("numero") Integer numero,
            @Bind("fecha") String fecha
    );

    default int insert(ProtocoloRecord protocolo) {
        return insertRaw(
                protocolo.idProtocolo(),
                protocolo.idPlantilla(),
                protocolo.numero(),
                protocolo.fecha().toString()
        );
    }

    @SqlQuery("""
            SELECT id_protocolo, id_plantilla, numero, fecha
            FROM protocolo
            WHERE id_protocolo = :id
            """)
    Optional<ProtocoloRecord> findById(
            @Bind("id") long id
    );
}