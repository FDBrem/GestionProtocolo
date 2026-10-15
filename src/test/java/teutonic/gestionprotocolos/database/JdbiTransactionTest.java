package teutonic.gestionprotocolos.database;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.sql.Connection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JdbiTransactionTest {

    @TempDir
    Path tempDir;

    @Test
    void rollsBackAllChangesWhenAnInsertFails()
            throws Exception {

        Path dbPath = tempDir.resolve("transaction-test.db");

        try (Connection ignored =
                     TestDatabase.create(dbPath)) {
        }

        Jdbi jdbi = JdbiProvider.create(
                "jdbc:sqlite:" + dbPath.toAbsolutePath()
        );

        assertThatThrownBy(() ->
                jdbi.useTransaction(handle -> {
                    handle.createUpdate("""
                            INSERT INTO serie_muestra
                                (id_serie, nombre)
                            VALUES (:id, :nombre)
                            """)
                            .bind("id", 1)
                            .bind("nombre", "Serie transaccion")
                            .execute();

                    // Falla porque nombre tiene UNIQUE.
                    handle.createUpdate("""
                            INSERT INTO serie_muestra
                                (id_serie, nombre)
                            VALUES (:id, :nombre)
                            """)
                            .bind("id", 2)
                            .bind("nombre", "Serie transaccion")
                            .execute();
                })
        ).isInstanceOf(UnableToExecuteStatementException.class);

        long count = jdbi.withHandle(handle ->
                handle.createQuery("""
                        SELECT COUNT(*)
                        FROM serie_muestra
                        WHERE nombre = :nombre
                        """)
                        .bind("nombre", "Serie transaccion")
                        .mapTo(long.class)
                        .one()
        );

        assertThat(count).isZero();
    }
}