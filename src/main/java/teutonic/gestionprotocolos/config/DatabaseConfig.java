package teutonic.gestionprotocolos.config;

import org.jdbi.v3.core.Jdbi;
import teutonic.gestionprotocolos.database.JdbiProvider;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

public final class DatabaseConfig {

    // Ruta a la base de datos SQLite
    private static final String DB_URL =
            "jdbc:sqlite:gestion_protocolo_data.db";

    private static final Jdbi JDBI = JdbiProvider.create();
    
    public static Jdbi getJdbi() {
        return JDBI;
    }

    //esto es para activar las fk, parece que sqlite no las tiene activada y hay que hacerlo siempre porq no hace persistencia de esa config
    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(DB_URL);

        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");

            try (ResultSet rs =
                         statement.executeQuery("PRAGMA foreign_keys")) {
                if (!rs.next() || rs.getInt(1) != 1) {
                    throw new SQLException(
                            "SQLite foreign key constraints are not enabled"
                    );
                }
            }
        } catch (SQLException e) {
            connection.close();
            throw e;
        }

        return connection;
    }

    //lo mismo con wal, asi no bloqueamos escritura y lectura si pasan a la vez
    public static void initialize() throws SQLException {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs =
                     statement.executeQuery("PRAGMA journal_mode = WAL")) {

            if (!rs.next() || !"wal".equalsIgnoreCase(rs.getString(1))) {
                throw new SQLException(
                        "SQLite could not enable WAL journal mode"
                );
            }
        }
    }

    public static String getDatabaseUrl() {
        return DB_URL;
    }

    //esto habria que borrarlo una vez en produccion
    public static void verifyConfiguration() throws SQLException {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            try (ResultSet rs =
                         statement.executeQuery("PRAGMA foreign_keys")) {
                rs.next();
                System.out.println(
                        "foreign_keys = " + rs.getInt(1)
                );
            }

            try (ResultSet rs =
                         statement.executeQuery("PRAGMA journal_mode")) {
                rs.next();
                System.out.println(
                        "journal_mode = " + rs.getString(1)
                );
            }
        }
    }
}
