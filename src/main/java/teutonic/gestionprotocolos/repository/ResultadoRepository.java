package teutonic.gestionprotocolos.repository;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import teutonic.gestionprotocolos.persistence.records.ResultadoRecord;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ResultadoRepository {

    private final Jdbi jdbi;

    private static final String SELECT_COLUMNS = """
            id_resultado, id_muestra, id_analisis, hora_control,
            valor, menor_que, colonias, presencia, conforme,
            pendiente, observacion, creado_en
            """;

    private static final RowMapper<ResultadoRecord> ROW_MAPPER =
            (rs, ctx) -> new ResultadoRecord(
                    rs.getLong("id_resultado"),
                    rs.getLong("id_muestra"),
                    rs.getLong("id_analisis"),
                    rs.getString("hora_control"),
                    nullableDouble(rs, "valor"),
                    nullableInteger(rs, "menor_que"),
                    nullableInteger(rs, "colonias"),
                    nullableInteger(rs, "presencia"),
                    nullableInteger(rs, "conforme"),
                    nullableInteger(rs, "pendiente"),
                    rs.getString("observacion"),
                    rs.getString("creado_en")
            );

    public ResultadoRepository(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    public Optional<ResultadoRecord> buscarPorId(long id) {
        return jdbi.withHandle(handle ->
                handle.createQuery("""
                                SELECT %s
                                FROM resultado
                                WHERE id_resultado = :id
                                """.formatted(SELECT_COLUMNS))
                        .bind("id", id)
                        .map(ROW_MAPPER)
                        .findOne()
        );
    }

    public List<ResultadoRecord> listarPorMuestra(long muestraId) {
        return jdbi.withHandle(handle ->
                handle.createQuery("""
                                SELECT %s
                                FROM resultado
                                WHERE id_muestra = :muestraId
                                ORDER BY id_analisis, hora_control
                                """.formatted(SELECT_COLUMNS))
                        .bind("muestraId", muestraId)
                        .map(ROW_MAPPER)
                        .list()
        );
    }

    public ResultadoRecord insertar(ResultadoRecord resultado) {
        return jdbi.inTransaction(handle -> {
            long id = handle.createUpdate("""
                            INSERT INTO resultado (
                                id_muestra, id_analisis, hora_control, valor,
                                menor_que, colonias, presencia, conforme,
                                pendiente, observacion
                            )
                            VALUES (
                                :idMuestra, :idAnalisis, :horaControl, :valor,
                                :menorQue, :colonias, :presencia, :conforme,
                                :pendiente, :observacion
                            )
                            """)
                    .bind("idMuestra", resultado.idMuestra())
                    .bind("idAnalisis", resultado.idAnalisis())
                    .bind("horaControl", resultado.horaControl())
                    .bind("valor", resultado.valor())
                    .bind("menorQue", resultado.menorQue())
                    .bind("colonias", resultado.colonias())
                    .bind("presencia", resultado.presencia())
                    .bind("conforme", resultado.conforme())
                    .bind("pendiente", resultado.pendiente())
                    .bind("observacion", resultado.observacion())
                    .executeAndReturnGeneratedKeys("id_resultado")
                    .mapTo(Long.class)
                    .one();

            return handle.createQuery("""
                            SELECT %s
                            FROM resultado
                            WHERE id_resultado = :id
                            """.formatted(SELECT_COLUMNS))
                    .bind("id", id)
                    .map(ROW_MAPPER)
                    .one();
        });
    }

    public boolean actualizar(ResultadoRecord resultado) {
        if (resultado.idResultado() == null) {
            throw new IllegalArgumentException(
                    "El resultado debe tener ID para actualizarse"
            );
        }

        return jdbi.withHandle(handle ->
                handle.createUpdate("""
                                UPDATE resultado
                                SET id_muestra = :idMuestra,
                                    id_analisis = :idAnalisis,
                                    hora_control = :horaControl,
                                    valor = :valor,
                                    menor_que = :menorQue,
                                    colonias = :colonias,
                                    presencia = :presencia,
                                    conforme = :conforme,
                                    pendiente = :pendiente,
                                    observacion = :observacion
                                WHERE id_resultado = :id
                                """)
                        .bind("id", resultado.idResultado())
                        .bind("idMuestra", resultado.idMuestra())
                        .bind("idAnalisis", resultado.idAnalisis())
                        .bind("horaControl", resultado.horaControl())
                        .bind("valor", resultado.valor())
                        .bind("menorQue", resultado.menorQue())
                        .bind("colonias", resultado.colonias())
                        .bind("presencia", resultado.presencia())
                        .bind("conforme", resultado.conforme())
                        .bind("pendiente", resultado.pendiente())
                        .bind("observacion", resultado.observacion())
                        .execute() > 0
        );
    }

    private static Double nullableDouble(
            ResultSet rs,
            String column
    ) throws SQLException {
        Object value = rs.getObject(column);
        return value == null ? null : ((Number) value).doubleValue();
    }

    private static Integer nullableInteger(
            ResultSet rs,
            String column
    ) throws SQLException {
        Object value = rs.getObject(column);
        return value == null ? null : ((Number) value).intValue();
    }
}