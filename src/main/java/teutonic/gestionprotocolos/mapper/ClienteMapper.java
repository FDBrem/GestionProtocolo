package teutonic.gestionprotocolos.mapper;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import teutonic.gestionprotocolos.persistence.records.ClienteRecord;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ClienteMapper implements RowMapper<ClienteRecord> {
    @Override
    public ClienteRecord map(ResultSet rs, StatementContext ctx) throws SQLException {
        return new ClienteRecord(
                rs.getLong("id_cliente"),
                rs.getString("nombre")
        );
    }
}
