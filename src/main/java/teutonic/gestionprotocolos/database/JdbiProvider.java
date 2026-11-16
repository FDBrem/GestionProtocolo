package teutonic.gestionprotocolos.database;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import teutonic.gestionprotocolos.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class JdbiProvider {

    //la app usa DatabaseConfig
    public static Jdbi create() {
        return create(DatabaseConfig::getConnection);
    }

    //los tests usan otra url, y como no usan la config de la db hay que volver a activarles las fk, no es duplicado
    public static Jdbi create(String databaseUrl) {
        return create(() -> {
            Connection connection =
                    DriverManager.getConnection(databaseUrl);

            try (Statement statement = connection.createStatement()) {
                statement.execute("PRAGMA foreign_keys = ON");
                statement.execute("PRAGMA busy_timeout = 5000");
                return connection;
            } catch (SQLException e) {
                connection.close();
                throw e;
            }
        });
    }

    private static Jdbi create(
            org.jdbi.v3.core.ConnectionFactory connectionFactory) {

        Jdbi jdbi = Jdbi.create(connectionFactory);
        jdbi.installPlugin(new SqlObjectPlugin());

        return jdbi;
    }
}