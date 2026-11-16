package teutonic.gestionprotocolos.database;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;

class TestDatabaseTest {

    @TempDir
    Path tempDir;

    @Test
    void createsTemporaryDatabaseWithMigrations()
            throws Exception {

        try (Connection connection = TestDatabase.create(
                tempDir.resolve("example.db"))) {

            assertThat(connection.isValid(2)).isTrue();

            try (Statement statement =
                         connection.createStatement();
                 ResultSet result = statement.executeQuery(
                         "SELECT COUNT(*) FROM sqlite_master "
                                 + "WHERE type = 'table' "
                                 + "AND name = 'protocolo'")) {

                assertThat(result.next()).isTrue();
                assertThat(result.getInt(1)).isEqualTo(1);
            }
        }
    }
}