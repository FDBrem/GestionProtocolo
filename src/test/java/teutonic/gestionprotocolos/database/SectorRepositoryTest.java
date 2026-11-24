
package teutonic.gestionprotocolos.database;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import teutonic.gestionprotocolos.persistence.records.SectorRecord;
import teutonic.gestionprotocolos.repository.SectorRepository;

import java.nio.file.Path;
import java.sql.Connection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectorRepositoryTest {

    @TempDir
    Path tempDir;

    private Jdbi jdbi;

    @BeforeEach
    void setUp() throws Exception {

        Path dbPath = tempDir.resolve("sector-test.db");

        try (Connection ignored = TestDatabase.create(dbPath)) {
            // Se crea la base y se ejecutan las migraciones.
        }

        String jdbcUrl = "jdbc:sqlite:"
                + dbPath.toAbsolutePath();

        jdbi = JdbiProvider.create(jdbcUrl);
    }

    private long insertar(String nombre, boolean activo) {

        return jdbi.withExtension(
                SectorRepository.class,
                repository -> repository.insertar(nombre, activo)
        );
    }

    @Test
    void insertaYRecuperaSector() {

        long id = insertar("Sector Norte", true);

        SectorRecord sector = jdbi.withExtension(
                SectorRepository.class,
                repository -> repository.buscarPorId(id)
                        .orElseThrow()
        );

        assertThat(sector.idSector()).isEqualTo(id);
        assertThat(sector.nombre()).isEqualTo("Sector Norte");
        assertThat(sector.activo()).isTrue();
        assertThat(sector.nombreNormalizado())
                .isEqualTo("sectornorte");
    }

    @Test
    void listaTodosLosSectoresIncluyendoInactivos() {

        long activo = insertar("Sector Norte", true);
        long inactivo = insertar("Sector Sur", false);

        jdbi.useExtension(
                SectorRepository.class,
                repository -> {

                    assertThat(repository.listarTodas())
                            .extracting(SectorRecord::idSector)
                            .containsExactlyInAnyOrder(activo, inactivo);

                    assertThat(repository.listarActivas())
                            .extracting(SectorRecord::idSector)
                            .containsExactly(activo);
                }
        );
    }

    @Test
    void buscaPorNombreNormalizado() {

        insertar("Sector Ñandú", true);

        List<SectorRecord> encontrados = jdbi.withExtension(
                SectorRepository.class,
                repository -> repository.buscarPorNombre("SECTOR ÑANDÚ")
        );

        assertThat(encontrados).hasSize(1);
        assertThat(encontrados.get(0).nombre())
                .isEqualTo("Sector Ñandú");
    }

    @Test
    void noDevuelveResultadosCuandoLaBusquedaEstaVacia() {

        insertar("Sector Norte", true);

        List<SectorRecord> encontrados = jdbi.withExtension(
                SectorRepository.class,
                repository -> repository.buscarPorNombre("   ")
        );

        assertThat(encontrados).isEmpty();
    }

    @Test
    void detectaNombresDuplicadosInclusoEnSectoresInactivos() {

        long id = insertar("Sector Norte", true);

        jdbi.useExtension(
                SectorRepository.class,
                repository -> {

                    repository.desactivar(id);

                    assertThat(repository.existeNombre("SECTOR NÓRTE"))
                            .isTrue();

                    assertThat(
                            repository.existeNombreParaOtroSector(
                                    "Sector Norte",
                                    id
                            )
                    ).isFalse();

                    assertThat(
                            repository.existeNombreParaOtroSector(
                                    "Sector Norte",
                                    999L
                            )
                    ).isTrue();
                }
        );
    }

    @Test
    void rechazaDuplicadosNormalizadosEnLaBaseDeDatos() {

        insertar("Sector Norte", true);

        assertThatThrownBy(
                () -> insertar("SECTORNORTE", true)
        ).isInstanceOf(UnableToExecuteStatementException.class);
    }

    @Test
    void actualizaSectorYRecalculaNombreNormalizado() {

        long id = insertar("Sector Norte", true);

        int filasActualizadas = jdbi.withExtension(
                SectorRepository.class,
                repository -> repository.actualizar(
                        id,
                        "Sector Industrial",
                        true
                )
        );

        SectorRecord actualizado = jdbi.withExtension(
                SectorRepository.class,
                repository -> repository.buscarPorId(id)
                        .orElseThrow()
        );

        assertThat(filasActualizadas).isEqualTo(1);
        assertThat(actualizado.nombre()).isEqualTo("Sector Industrial");
        assertThat(actualizado.nombreNormalizado())
                .isEqualTo("sectorindustrial");
    }

    @Test
    void desactivaYReactivaSector() {

        long id = insertar("Sector Norte", true);

        jdbi.useExtension(
                SectorRepository.class,
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

        long id = insertar("Sector Norte", true);

        assertThatThrownBy(
                () -> jdbi.withExtension(
                        SectorRepository.class,
                        repository -> repository.actualizar(
                                id,
                                "   ",
                                true
                        )
                )
        ).isInstanceOf(IllegalArgumentException.class);
    }
}
