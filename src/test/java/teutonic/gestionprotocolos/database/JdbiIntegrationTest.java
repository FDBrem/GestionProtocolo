package teutonic.gestionprotocolos.database;

import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import teutonic.gestionprotocolos.persistence.records.ProtocoloRecord;
import teutonic.gestionprotocolos.repository.ProtocoloRepository;

import java.nio.file.Path;
import java.sql.Connection;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class JdbiIntegrationTest {

    @TempDir
    Path tempDir;

    private Jdbi jdbi;

    @BeforeEach
    void setUp() throws Exception {
        Path dbPath = tempDir.resolve("jdbi-test.db");

        try (Connection ignored =
                     TestDatabase.create(dbPath)) {
        }

        String jdbcUrl = "jdbc:sqlite:"
                + dbPath.toAbsolutePath();

        jdbi = JdbiProvider.create(jdbcUrl);

        jdbi.useHandle(handle -> {
            handle.execute("""
                    INSERT INTO serie_muestra
                        (id_serie, nombre)
                    VALUES (1, 'Serie test')
                    """);

            handle.execute("""
                    INSERT INTO producto
                        (id_producto, nombre, id_serie)
                    VALUES (1, 'Producto test', 1)
                    """);

            handle.execute("""
                    INSERT INTO plantilla
                        (id_plantilla, nombre, familia, id_producto)
                    VALUES
                        (1, 'Plantilla test',
                         'MUESTRA_PRODUCTO', 1)
                    """);
        });
    }

    @Test
    void insertsAndReadsProtocol() {
        ProtocoloRecord expected = new ProtocoloRecord(
                1,
                1,
                100,
                LocalDate.of(2026, 10, 15)
        );

        jdbi.useExtension(
                ProtocoloRepository.class,
                repository -> repository.insert(expected)
        );

        ProtocoloRecord actual = jdbi.withExtension(
                ProtocoloRepository.class,
                repository -> repository.findById(1)
                        .orElseThrow()
        );

        assertThat(actual).isEqualTo(expected);
        assertThat(actual.fecha())
                .isEqualTo(LocalDate.of(2026, 10, 15));
    }
    record FechaHora(LocalDate fecha, java.time.LocalTime hora) {
    }
    @Test
    void convertsIsoTextToLocalDateAndLocalTime() {
        FechaHora result = jdbi.withHandle(handle ->
                handle.createQuery("""
                    SELECT
                        '2026-10-15' AS fecha,
                        '14:30:00' AS hora
                    """)
                        .map((rs, context) -> new FechaHora(
                                LocalDate.parse(rs.getString("fecha")),
                                java.time.LocalTime.parse(
                                        rs.getString("hora"))
                        ))
                        .one()
        );

        assertThat(result.fecha())
                .isEqualTo(LocalDate.of(2026, 10, 15));

        assertThat(result.hora())
                .isEqualTo(java.time.LocalTime.of(14, 30));
    }
}