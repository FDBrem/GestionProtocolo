package teutonic.gestionprotocolos.database;

import org.flywaydb.core.Flyway;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class TestDatabase {

    private TestDatabase() {
    }

    public static Connection create(Path databasePath)
            throws SQLException {

        String url = "jdbc:sqlite:"
                + databasePath.toAbsolutePath();

        Flyway.configure()
                .dataSource(url, null, null)
                .locations(
                        "filesystem:src/main/resources/db/migration"
                )
                .load()
                .migrate();

        Connection connection =
                DriverManager.getConnection(url);

        try (Statement statement =
                     connection.createStatement()) {

            statement.execute("PRAGMA foreign_keys = ON");
            return connection;

        } catch (SQLException exception) {
            connection.close();
            throw exception;
        }
    }
}