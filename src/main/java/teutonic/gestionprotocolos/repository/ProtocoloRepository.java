package teutonic.gestionprotocolos.repository;

import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import teutonic.gestionprotocolos.persistence.records.ProtocoloRecord;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RegisterConstructorMapper(ProtocoloRecord.class)
public interface ProtocoloRepository {

    @SqlQuery("""
            SELECT
                id_protocolo AS idProtocolo,
                id_plantilla AS idPlantilla,
                numero,
                fecha
            FROM protocolo
            WHERE id_protocolo = :id
            """)
    Optional<ProtocoloRecord> buscarPorId(@Bind("id") long id);

    @SqlQuery("""
            SELECT
                id_protocolo AS idProtocolo,
                id_plantilla AS idPlantilla,
                numero,
                fecha
            FROM protocolo
            ORDER BY id_protocolo DESC
            """)
    List<ProtocoloRecord> listarTodas();

    @SqlQuery("""
            SELECT
                id_protocolo AS idProtocolo,
                id_plantilla AS idPlantilla,
                numero,
                fecha
            FROM protocolo
            WHERE id_plantilla = :idPlantilla
            ORDER BY numero DESC
            """)
    List<ProtocoloRecord> buscarPorPlantilla(@Bind("idPlantilla") long idPlantilla);

    @SqlUpdate("""
            INSERT INTO protocolo (
                id_plantilla,
                numero,
                fecha,
                id_cliente,
                metodo,
                equipo,
                peso_muestra_g,
                superficie_cm2,
                analista,
                observaciones
            )
            VALUES (
                :idPlantilla,
                :numero,
                :fecha,
                :idCliente,
                :metodo,
                :equipo,
                :pesoMuestraG,
                :superficieCm2,
                :analista,
                :observaciones
            )
            """)
    @GetGeneratedKeys("id_protocolo")
    long insertar(
            @Bind("idPlantilla") long idPlantilla,
            @Bind("numero") Integer numero,
            @Bind("fecha") LocalDate fecha,
            @Bind("idCliente") Long idCliente,
            @Bind("metodo") String metodo,
            @Bind("equipo") String equipo,
            @Bind("pesoMuestraG") Double pesoMuestraG,
            @Bind("superficieCm2") Double superficieCm2,
            @Bind("analista") String analista,
            @Bind("observaciones") String observaciones
    );

    @SqlUpdate("""
            INSERT INTO protocolo (
                id_protocolo,
                id_plantilla,
                numero,
                fecha
            )
            VALUES (
                :id,
                :plantilla,
                :numero,
                :fecha
            )
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

    @SqlUpdate("""
            UPDATE protocolo
            SET
                id_plantilla = :plantilla,
                numero = :numero,
                fecha = :fecha
            WHERE id_protocolo = :id
            """)
    int actualizar(
            @Bind("id") long id,
            @Bind("plantilla") long plantilla,
            @Bind("numero") Integer numero,
            @Bind("fecha") LocalDate fecha
    );

    @SqlUpdate("""
            DELETE FROM protocolo
            WHERE id_protocolo = :id
            """)
    int eliminar(@Bind("id") long id);

    //esto es para que el contador se incremente dentro de la misma transaccion del registro
    @SqlQuery("""
            INSERT INTO contador_protocolo (id_plantilla, ultimo_numero)
            VALUES (:idPlantilla, 1)
            ON CONFLICT (id_plantilla)
            DO UPDATE SET ultimo_numero = ultimo_numero + 1
            RETURNING ultimo_numero
            """)
    int obtenerSiguienteNumero(
            @Bind("idPlantilla") long idPlantilla
    );

    // Métodos de conveniencia compatibles
    default Optional<ProtocoloRecord> findById(long id) {
        return buscarPorId(id);
    }

    default List<ProtocoloRecord> findAll() {
        return listarTodas();
    }

    default int delete(long id) {
        return eliminar(id);
    }

    default int update(ProtocoloRecord protocolo) {
        return actualizar(
                protocolo.idProtocolo(),
                protocolo.idPlantilla(),
                protocolo.numero(),
                protocolo.fecha()
        );
    }
}