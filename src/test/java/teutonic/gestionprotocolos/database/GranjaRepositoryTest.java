
package teutonic.gestionprotocolos.database;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import teutonic.gestionprotocolos.persistence.records.GranjaRecord;
import teutonic.gestionprotocolos.repository.GranjaRepository;

import java.nio.file.Path;
import java.sql.Connection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GranjaRepositoryTest {

    @TempDir
    Path tempDir;

    private Jdbi jdbi;

    @BeforeEach
    void setUp() throws Exception {

        Path dbPath = tempDir.resolve("granja-test.db");

        try (Connection ignored = TestDatabase.create(dbPath)) {
            // Se crea la base y se ejecutan las migraciones.
        }

        String jdbcUrl = "jdbc:sqlite:"
                + dbPath.toAbsolutePath();

        jdbi = JdbiProvider.create(jdbcUrl);
    }

    private long insertar(String nombre, boolean activo) {

        return jdbi.withExtension(
                GranjaRepository.class,
                repository -> repository.insertar(nombre, activo)
        );
    }

    @Test
    void insertaYRecuperaGranja() {

        long id = insertar("Granja Norte", true);

        GranjaRecord granja = jdbi.withExtension(
                GranjaRepository.class,
                repository -> repository.buscarPorId(id)
                        .orElseThrow()
        );

        assertThat(granja.idGranja()).isEqualTo(id);
        assertThat(granja.nombre()).isEqualTo("Granja Norte");
        assertThat(granja.activo()).isTrue();
        assertThat(granja.nombreNormalizado())
                .isEqualTo("granjanorte");
    }

    @Test
    void listaTodasLasGranjasIncluyendoInactivas() {

        long activa = insertar("Granja Norte", true);
        long inactiva = insertar("Granja Sur", false);

        jdbi.useExtension(
                GranjaRepository.class,
                repository -> {

                    assertThat(repository.listarTodas())
                            .extracting(GranjaRecord::idGranja)
                            .containsExactlyInAnyOrder(activa, inactiva);

                    assertThat(repository.listarActivas())
                            .extracting(GranjaRecord::idGranja)
                            .containsExactly(activa);
                }
        );
    }

    @Test
    void buscaPorNombreNormalizado() {

        insertar("Granja Ñandú", true);

        List<GranjaRecord> encontrados = jdbi.withExtension(
                GranjaRepository.class,
                repository -> repository.buscarPorNombre("GRANJA ÑANDÚ")
        );

        assertThat(encontrados).hasSize(1);
        assertThat(encontrados.get(0).nombre())
                .isEqualTo("Granja Ñandú");
    }

    @Test
    void noDevuelveResultadosCuandoLaBusquedaEstaVacia() {

        insertar("Granja Norte", true);

        List<GranjaRecord> encontrados = jdbi.withExtension(
                GranjaRepository.class,
                repository -> repository.buscarPorNombre("   ")
        );

        assertThat(encontrados).isEmpty();
    }

    @Test
    void detectaNombresDuplicadosInclusoEnGranjasInactivas() {

        long id = insertar("Granja Norte", true);

        jdbi.useExtension(
                GranjaRepository.class,
                repository -> {

                    repository.desactivar(id);

                    assertThat(repository.existeNombre("GRANJA NÓRTE"))
                            .isTrue();

                    assertThat(
                            repository.existeNombreParaOtraGranja(
                                    "Granja Norte",
                                    id
                            )
                    ).isFalse();

                    assertThat(
                            repository.existeNombreParaOtraGranja(
                                    "Granja Norte",
                                    999L
                            )
                    ).isTrue();
                }
        );
    }

    @Test
    void rechazaDuplicadosNormalizadosEnLaBaseDeDatos() {

        insertar("Granja Norte", true);

        assertThatThrownBy(
                () -> insertar("GRANJANORTE", true)
        ).isInstanceOf(UnableToExecuteStatementException.class);
    }

    @Test
    void actualizaGranjaYRecalculaNombreNormalizado() {

        long id = insertar("Granja Norte", true);

        int filasActualizadas = jdbi.withExtension(
                GranjaRepository.class,
                repository -> repository.actualizar(
                        id,
                        "Granja Industrial",
                        true
                )
        );

        GranjaRecord actualizada = jdbi.withExtension(
                GranjaRepository.class,
                repository -> repository.buscarPorId(id)
                        .orElseThrow()
        );

        assertThat(filasActualizadas).isEqualTo(1);
        assertThat(actualizada.nombre()).isEqualTo("Granja Industrial");
        assertThat(actualizada.nombreNormalizado())
                .isEqualTo("granjaindustrial");
    }

    @Test
    void desactivaYReactivaGranja() {

        long id = insertar("Granja Norte", true);

        jdbi.useExtension(
                GranjaRepository.class,
                repository -> {

                    assertThat(repository.desactivar(id)).isEqualTo(1);

                    assertThat(repository.listarActivas())
                            .isEmpty();

                    assertThat(repository.listarTodas())
                            .hasSize(1);

                    assertThat(repository.activar(id)).isEqualTo(1);

                    assertThat(repository.listarActivas())
                            .hasSize(1);
                }
        );
    }

    @Test
    void rechazaNombresVaciosAlInsertarYActualizar() {

        assertThatThrownBy(
                () -> insertar("   ", true)
        ).isInstanceOf(IllegalArgumentException.class);

        long id = insertar("Granja Norte", true);

        assertThatThrownBy(
                () -> jdbi.withExtension(
                        GranjaRepository.class,
                        repository -> repository.actualizar(
                                id,
                                "   ",
                                true
                        )
                )
        ).isInstanceOf(IllegalArgumentException.class);
    }
}
