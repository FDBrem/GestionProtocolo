package teutonic.gestionprotocolos.repository;

import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import teutonic.gestionprotocolos.persistence.records.ClienteRecord;

import java.util.List;
import java.util.Optional;

@RegisterConstructorMapper(ClienteRecord.class)
public interface ClienteRepository {

    @SqlUpdate("""
            INSERT INTO cliente (id_cliente, nombre)
            VALUES (:id, :nombre)
            """)
    int insertRaw(@Bind("id") long id, @Bind("nombre") String nombre);

    default int insert(ClienteRecord cliente) {
        return insertRaw(cliente.idCliente(), cliente.nombre());
    }

    @SqlQuery("""
            SELECT id_cliente AS idCliente, nombre
            FROM cliente
            WHERE id_cliente = :id
            """)
    Optional<ClienteRecord> findById(@Bind("id") long id);

    @SqlQuery("""
            SELECT id_cliente AS idCliente, nombre
            FROM cliente
            ORDER BY nombre
            """)
    List<ClienteRecord> findAll();

    @SqlUpdate("""
            UPDATE cliente SET nombre = :nombre WHERE id_cliente = :id
            """)
    int updateRaw(@Bind("id") long id, @Bind("nombre") String nombre);

    default int update(ClienteRecord cliente) {
        return updateRaw(cliente.idCliente(), cliente.nombre());
    }

    @SqlUpdate("""
            DELETE FROM cliente WHERE id_cliente = :id
            """)
    int delete(@Bind("id") long id);
}
