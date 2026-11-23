
package teutonic.gestionprotocolos.database;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import teutonic.gestionprotocolos.persistence.records.ProductoRecord;
import teutonic.gestionprotocolos.repository.ProductoRepository;

import java.nio.file.Path;
import java.sql.Connection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductoRepositoryTest {

    @TempDir
    Path tempDir;

    private Jdbi jdbi;

    @BeforeEach
    void setUp() throws Exception {

        Path dbPath = tempDir.resolve("producto-test.db");

        try (Connection ignored = TestDatabase.create(dbPath)) {
            // La base se crea y se migran las tablas.
        }

        String jdbcUrl = "jdbc:sqlite:"
                + dbPath.toAbsolutePath();

        jdbi = JdbiProvider.create(jdbcUrl);
    }

    private long insertar(String nombre, boolean activo) {

        return jdbi.withExtension(
                ProductoRepository.class,
                repository -> repository.insertar(
                        nombre,
                        null,
                        activo
                )
        );
    }

    @Test
    void insertaYRecuperaProducto() {

        long id = insertar("Pollo Entero", true);

        ProductoRecord producto = jdbi.withExtension(
                ProductoRepository.class,
                repository -> repository.buscarPorId(id)
                        .orElseThrow()
        );

        assertThat(producto.idProducto()).isEqualTo(id);
        assertThat(producto.nombre()).isEqualTo("Pollo Entero");
        assertThat(producto.idSerie()).isNull();
        assertThat(producto.activo()).isTrue();
        assertThat(producto.nombreNormalizado())
                .isEqualTo("polloentero");
    }

    @Test
    void listaTodosLosProductosIncluyendoInactivos() {

        long activo = insertar("Pollo", true);
        long inactivo = insertar("Carne", false);

        jdbi.useExtension(
                ProductoRepository.class,
                repository -> {

                    assertThat(repository.listarTodas())
                            .extracting(ProductoRecord::idProducto)
                            .containsExactlyInAnyOrder(activo, inactivo);

                    assertThat(repository.listarActivas())
                            .extracting(ProductoRecord::idProducto)
                            .containsExactly(activo);
                }
        );
    }

    @Test
    void buscaPorNombreNormalizado() {

        insertar("Pechúga Entera", true);

        List<ProductoRecord> encontrados = jdbi.withExtension(
                ProductoRepository.class,
                repository -> repository.buscarPorNombre("PECHÚGA")
        );

        assertThat(encontrados).hasSize(1);
        assertThat(encontrados.get(0).nombre())
                .isEqualTo("Pechúga Entera");
    }

    @Test
    void noDevuelveResultadosCuandoLaBusquedaEstaVacia() {

        insertar("Pollo Entero", true);

        List<ProductoRecord> encontrados = jdbi.withExtension(
                ProductoRepository.class,
                repository -> repository.buscarPorNombre("   ")
        );

        assertThat(encontrados).isEmpty();
    }

    @Test
    void detectaNombresDuplicadosInclusoEnProductosInactivos() {

        long id = insertar("Pollo Entero", true);

        jdbi.useExtension(
                ProductoRepository.class,
                repository -> {

                    repository.desactivar(id);

                    assertThat(repository.existeNombre("PÓLLO ENTERO"))
                            .isTrue();

                    assertThat(
                            repository.existeNombreParaOtroProducto(
                                    "Pollo Entero",
                                    id
                            )
                    ).isFalse();

                    assertThat(
                            repository.existeNombreParaOtroProducto(
                                    "Pollo Entero",
                                    999L
                            )
                    ).isTrue();
                }
        );
    }

    @Test
    void rechazaDuplicadosNormalizadosEnLaBaseDeDatos() {

        insertar("Pollo Entero", true);

        assertThatThrownBy(
                () -> insertar("PÓLLOENTERO", true)
        ).isInstanceOf(UnableToExecuteStatementException.class);
    }

    @Test
    void actualizaProductoYRecalculaNombreNormalizado() {

        long id = insertar("Pollo Entero", true);

        int filasActualizadas = jdbi.withExtension(
                ProductoRepository.class,
                repository -> repository.actualizar(
                        id,
                        "Carne Vacuna",
                        null,
                        true
                )
        );

        ProductoRecord actualizado = jdbi.withExtension(
                ProductoRepository.class,
                repository -> repository.buscarPorId(id)
                        .orElseThrow()
        );

        assertThat(filasActualizadas).isEqualTo(1);
        assertThat(actualizado.nombre()).isEqualTo("Carne Vacuna");
        assertThat(actualizado.nombreNormalizado())
                .isEqualTo("carnevacuna");
    }

    @Test
    void desactivaYReactivaProducto() {

        long id = insertar("Pollo", true);

        jdbi.useExtension(
                ProductoRepository.class,
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

        long id = insertar("Pollo", true);

        assertThatThrownBy(
                () -> jdbi.withExtension(
                        ProductoRepository.class,
                        repository -> repository.actualizar(
                                id,
                                "   ",
                                null,
                                true
                        )
                )
        ).isInstanceOf(IllegalArgumentException.class);
    }
}
