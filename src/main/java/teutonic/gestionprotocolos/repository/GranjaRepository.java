package teutonic.gestionprotocolos.repository;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import teutonic.gestionprotocolos.persistence.records.GranjaRecord;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class GranjaRepository {

    private final Jdbi jdbi;

    private static final RowMapper<GranjaRecord> ROW_MAPPER =
            (rs, ctx) -> new GranjaRecord(
                    rs.getLong("id_granja"),
                    rs.getString("nombre"),
                    nullableInteger(rs, "activo")
            );

    public GranjaRepository(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    public Optional<GranjaRecord> buscarPorId(long id) {
        return jdbi.withHandle(handle ->
                handle.createQuery("""
                                SELECT id_granja, nombre, activo
                                FROM granja
                                WHERE id_granja = :id
                                """)
                        .bind("id", id)
                        .map(ROW_MAPPER)
                        .findOne()
        );
    }

    public List<GranjaRecord> listarTodas() {
        return jdbi.withHandle(handle ->
                handle.createQuery("""
                                SELECT id_granja, nombre, activo
                                FROM granja
                                ORDER BY nombre
                                """)
                        .map(ROW_MAPPER)
                        .list()
        );
    }

    public List<GranjaRecord> listarActivas() {
        return jdbi.withHandle(handle ->
                handle.createQuery("""
                                SELECT id_granja, nombre, activo
                                FROM granja
                                WHERE activo = 1
                                ORDER BY nombre
                                """)
                        .map(ROW_MAPPER)
                        .list()
        );
    }

    public GranjaRecord insertar(GranjaRecord granja) {
        return jdbi.inTransaction(handle -> {
            long id = handle.createUpdate("""
                            INSERT INTO granja (nombre, activo)
                            VALUES (:nombre, :activo)
                            """)
                    .bind("nombre", granja.nombre())
                    .bind("activo", granja.activo())
                    .executeAndReturnGeneratedKeys("id_granja")
                    .mapTo(Long.class)
                    .one();

            return handle.createQuery("""
                            SELECT id_granja, nombre, activo
                            FROM granja
                            WHERE id_granja = :id
                            """)
                    .bind("id", id)
                    .map(ROW_MAPPER)
                    .one();
        });
    }

    public boolean actualizar(GranjaRecord granja) {
        if (granja.idGranja() == null) {
            throw new IllegalArgumentException(
                    "La granja debe tener ID para actualizarse"
            );
        }

        return jdbi.withHandle(handle ->
                handle.createUpdate("""
                                UPDATE granja
                                SET nombre = :nombre,
                                    activo = :activo
                                WHERE id_granja = :id
                                """)
                        .bind("id", granja.idGranja())
                        .bind("nombre", granja.nombre())
                        .bind("activo", granja.activo())
                        .execute() > 0
        );
    }

    private static Integer nullableInteger(
            ResultSet rs,
            String column
    ) throws SQLException {
        Object value = rs.getObject(column);
        return value == null ? null : ((Number) value).intValue();
    }
}
