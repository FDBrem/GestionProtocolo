package teutonic.gestionprotocolos.database;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatabaseConstraintsTest {

    @TempDir
    Path tempDir;

    private Connection connection;

    @BeforeEach
    void setUp() throws Exception {
        connection = TestDatabase.create(
                tempDir.resolve("test.db")
        );

        insert("""
                INSERT INTO serie_muestra (id_serie, nombre)
                VALUES (1, 'Serie test')
                """);

        insert("""
                INSERT INTO producto
                    (id_producto, nombre, id_serie)
                VALUES (1, 'Producto test', 1)
                """);

        insert("""
                INSERT INTO plantilla
                    (id_plantilla, nombre, familia, id_producto)
                VALUES (1, 'Plantilla test', 'MUESTRA_PRODUCTO', 1)
                """);

        insert("""
                INSERT INTO analisis
                    (id_analisis, nombre, tipo_valor)
                VALUES (1, 'Analisis test', 'RECUENTO')
                """);
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    void rejectsDuplicateProtocolNumber() throws SQLException {
        insertProtocol(1, 100);

        assertConstraintFailure(
                """
                        INSERT INTO protocolo
                            (id_protocolo, id_plantilla, numero, fecha)
                        VALUES (2, 1, 100, '2026-10-15')
                        """,
                "UNIQUE constraint failed: protocolo.id_plantilla, protocolo.numero"
        );
    }

    @Test
    void rejectsDuplicateSampleNumberWithinSeries()
            throws SQLException {

        insertProtocol(1, 100);
        insertProtocol(2, 101);

        insertSample(1, 1, 1);

        assertConstraintFailure(
                """
                        INSERT INTO muestra
                            (id_muestra, id_protocolo, id_producto,
                             id_serie, numero_muestra, fecha_toma)
                        VALUES (2, 2, 1, 1, 1, '2026-10-15')
                        """,
                "UNIQUE constraint failed: muestra.id_serie, muestra.numero_muestra"
        );
    }

    @Test
    void rejectsDuplicateResultForSameSampleAnalysisAndTime()
            throws SQLException {

        insertProtocol(1, 100);
        insertSample(1, 1, 1);

        insert("""
                INSERT INTO resultado
                    (id_resultado, id_muestra, id_analisis,
                     hora_control, valor)
                VALUES (1, 1, 1, '', 10)
                """);

        assertConstraintFailure(
                """
                        INSERT INTO resultado
                            (id_resultado, id_muestra, id_analisis,
                             hora_control, valor)
                        VALUES (2, 1, 1, '', 20)
                        """,
                "UNIQUE constraint failed: resultado.id_muestra, resultado.id_analisis, resultado.hora_control"
        );
    }

    @Test
    void rejectsLessThanResultWithoutThreshold()
            throws SQLException {

        insertProtocol(1, 100);
        insertSample(1, 1, 1);

        assertConstraintFailure(
                """
                        INSERT INTO resultado
                            (id_resultado, id_muestra, id_analisis,
                             hora_control, valor, menor_que, pendiente)
                        VALUES (1, 1, 1, '', NULL, 1, 1)
                        """,
                "CHECK constraint failed"
        );
    }

    private void insertProtocol(int id, int number)
            throws SQLException {

        insert("""
                INSERT INTO protocolo
                    (id_protocolo, id_plantilla, numero, fecha)
                VALUES (%d, 1, %d, '2026-10-15')
                """.formatted(id, number));
    }

    private void insertSample(int id, int protocolId, int sampleNumber)
            throws SQLException {

        insert("""
                INSERT INTO muestra
                    (id_muestra, id_protocolo, id_producto,
                     id_serie, numero_muestra, fecha_toma)
                VALUES (%d, %d, 1, 1, %d, '2026-10-15')
                """.formatted(id, protocolId, sampleNumber));
    }

    private void insert(String sql) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        }
    }

    private void assertConstraintFailure(String sql, String expectedMessage)
            throws SQLException {

        SQLException exception = assertThrows(
                SQLException.class,
                () -> {
                    try (Statement statement = connection.createStatement()) {
                        statement.executeUpdate(sql);
                    }
                }
        );

        System.out.println("Excepcion SQLite: " + exception.getMessage());

        assertTrue(
                exception.getMessage() != null
                        && exception.getMessage().contains(expectedMessage),
                "Expected message to contain '%s', but got: %s"
                        .formatted(expectedMessage, exception.getMessage())
        );
    }
}

