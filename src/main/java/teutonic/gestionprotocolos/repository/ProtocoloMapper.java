package teutonic.gestionprotocolos.repository;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import teutonic.gestionprotocolos.model.ProtocoloRecord;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class ProtocoloMapper
        implements RowMapper<ProtocoloRecord> {

    @Override
    public ProtocoloRecord map(
            ResultSet rs,
            StatementContext context
    ) throws SQLException {

        String fechaTexto = rs.getString("fecha");

        return new ProtocoloRecord(
                rs.getLong("id_protocolo"),
                rs.getLong("id_plantilla"),
                (Integer) rs.getObject("numero"),
                LocalDate.parse(fechaTexto)
        );
    }
}