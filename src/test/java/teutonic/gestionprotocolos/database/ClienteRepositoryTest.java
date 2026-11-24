
package teutonic.gestionprotocolos.database;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import teutonic.gestionprotocolos.persistence.records.ClienteRecord;
import teutonic.gestionprotocolos.repository.ClienteRepository;

import java.nio.file.Path;
import java.sql.Connection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ClienteRepositoryTest {

    @TempDir
    Path tempDir;

    private Jdbi jdbi;

    @BeforeEach
    void setUp() throws Exception {

        Path dbPath = tempDir.resolve("cliente-test.db");

        try (Connection ignored = TestDatabase.create(dbPath)) {
            // Se crea la base y se ejecutan las migraciones.
        }

        String jdbcUrl = "jdbc:sqlite:"
                + dbPath.toAbsolutePath();

        jdbi = JdbiProvider.create(jdbcUrl);
    }

    private long insertar(String nombre, boolean activo) {

        return jdbi.withExtension(
                ClienteRepository.class,
                repository -> repository.insertar(nombre, activo)
        );
    }

    @Test
    void insertaYRecuperaCliente() {

        long id = insertar("Laboratorio Central", true);

        ClienteRecord cliente = jdbi.withExtension(
                ClienteRepository.class,
                repository -> repository.buscarPorId(id)
                        .orElseThrow()
        );

        assertThat(cliente.idCliente()).isEqualTo(id);
        assertThat(cliente.nombre()).isEqualTo("Laboratorio Central");
        assertThat(cliente.activo()).isTrue();
        assertThat(cliente.nombreNormalizado())
                .isEqualTo("laboratoriocentral");
    }

    @Test
    void listaTodosLosClientesIncluyendoInactivos() {

        long activo = insertar("Laboratorio Central", true);
        long inactivo = insertar("Laboratorio Norte", false);

        jdbi.useExtension(
                ClienteRepository.class,
                repository -> {

                    assertThat(repository.listarTodas())
                            .extracting(ClienteRecord::idCliente)
                            .containsExactlyInAnyOrder(activo, inactivo);

                    assertThat(repository.listarActivos())
                            .extracting(ClienteRecord::idCliente)
                            .containsExactly(activo);
                }
        );
    }

    @Test
    void buscaPorNombreNormalizado() {

        insertar("Laboratorio Ñandú", true);

        List<ClienteRecord> encontrados = jdbi.withExtension(
                ClienteRepository.class,
                repository -> repository.buscarPorNombre(
                        "LABORATORIO ÑANDÚ"
                )
        );

        assertThat(encontrados).hasSize(1);
        assertThat(encontrados.get(0).nombre())
                .isEqualTo("Laboratorio Ñandú");
    }

    @Test
    void noDevuelveResultadosCuandoLaBusquedaEstaVacia() {

        insertar("Laboratorio Central", true);

        List<ClienteRecord> encontrados = jdbi.withExtension(
                ClienteRepository.class,
                repository -> repository.buscarPorNombre("   ")
        );

        assertThat(encontrados).isEmpty();
    }

    @Test
    void detectaNombresDuplicadosInclusoEnClientesInactivos() {

        long id = insertar("Laboratorio Central", true);

        jdbi.useExtension(
                ClienteRepository.class,
                repository -> {

                    repository.desactivar(id);

                    assertThat(
                            repository.existeNombre("LABORATORIO CÉNTRAL")
                    ).isTrue();

                    assertThat(
                            repository.existeNombreParaOtroCliente(
                                    "Laboratorio Central",
                                    id
                            )
                    ).isFalse();

                    assertThat(
                            repository.existeNombreParaOtroCliente(
                                    "Laboratorio Central",
                                    999L
                            )
                    ).isTrue();
                }
        );
    }

    @Test
    void rechazaDuplicadosNormalizadosEnLaBaseDeDatos() {

        insertar("Laboratorio Central", true);

        assertThatThrownBy(
                () -> insertar("LABORATORIOCENTRAL", true)
        ).isInstanceOf(UnableToExecuteStatementException.class);
    }

    @Test
    void actualizaClienteYRecalculaNombreNormalizado() {

        long id = insertar("Laboratorio Central", true);

        int filasActualizadas = jdbi.withExtension(
                ClienteRepository.class,
                repository -> repository.actualizar(
                        id,
                        "Laboratorio Industrial",
                        true
                )
        );

        ClienteRecord actualizado = jdbi.withExtension(
                ClienteRepository.class,
                repository -> repository.buscarPorId(id)
                        .orElseThrow()
        );

        assertThat(filasActualizadas).isEqualTo(1);
        assertThat(actualizado.nombre())
                .isEqualTo("Laboratorio Industrial");
        assertThat(actualizado.nombreNormalizado())
                .isEqualTo("laboratorioindustrial");
    }

    @Test
    void desactivaYReactivaCliente() {

        long id = insertar("Laboratorio Central", true);

        jdbi.useExtension(
                ClienteRepository.class,
                repository -> {

                    assertThat(repository.desactivar(id)).isEqualTo(1);

                    assertThat(repository.listarActivos())
                            .isEmpty();

                    assertThat(repository.listarTodas())
                            .hasSize(1);

                    assertThat(repository.activar(id)).isEqualTo(1);

                    assertThat(repository.listarActivos())
                            .hasSize(1);
                }
        );
    }

    @Test
    void rechazaNombresVaciosAlInsertarYActualizar() {

        assertThatThrownBy(
                () -> insertar("   ", true)
        ).isInstanceOf(IllegalArgumentException.class);

        long id = insertar("Laboratorio Central", true);

        assertThatThrownBy(
                () -> jdbi.withExtension(
                        ClienteRepository.class,
                        repository -> repository.actualizar(
                                id,
                                "   ",
                                true
                        )
                )
        ).isInstanceOf(IllegalArgumentException.class);
    }
}
