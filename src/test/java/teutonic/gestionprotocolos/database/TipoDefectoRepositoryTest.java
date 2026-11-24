
package teutonic.gestionprotocolos.database;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import teutonic.gestionprotocolos.persistence.records.TipoDefectoRecord;
import teutonic.gestionprotocolos.repository.TipoDefectoRepository;

import java.nio.file.Path;
import java.sql.Connection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TipoDefectoRepositoryTest {

    @TempDir
    Path tempDir;

    private Jdbi jdbi;

    @BeforeEach
    void setUp() throws Exception {

        Path dbPath = tempDir.resolve("tipo-defecto-test.db");

        try (Connection ignored = TestDatabase.create(dbPath)) {
            // Se crea la base y se ejecutan las migraciones.
        }

        String jdbcUrl = "jdbc:sqlite:"
                + dbPath.toAbsolutePath();

        jdbi = JdbiProvider.create(jdbcUrl);
    }

    private long insertar(
            String grupo,
            String nombre,
            int orden,
            boolean activo
    ) {
        return jdbi.withExtension(
                TipoDefectoRepository.class,
                repository -> repository.insertar(
                        grupo,
                        nombre,
                        orden,
                        activo
                )
        );
    }

    @Test
    void insertaYRecuperaTipoDefecto() {

        long id = insertar(
                "Apariencia",
                "Moretón",
                1,
                true
        );

        TipoDefectoRecord defecto = jdbi.withExtension(
                TipoDefectoRepository.class,
                repository -> repository.buscarPorId(id)
                        .orElseThrow()
        );

        assertThat(defecto.idDefecto()).isEqualTo(id);
        assertThat(defecto.grupo()).isEqualTo("Apariencia");
        assertThat(defecto.nombre()).isEqualTo("Moretón");
        assertThat(defecto.orden()).isEqualTo(1);
        assertThat(defecto.activo()).isTrue();
        assertThat(defecto.nombreNormalizado())
                .isEqualTo("moreton");
    }

    @Test
    void listaTodosLosDefectosIncluyendoInactivos() {

        long activo = insertar(
                "Apariencia",
                "Moretón",
                1,
                true
        );

        long inactivo = insertar(
                "Apariencia",
                "Herida",
                2,
                false
        );

        jdbi.useExtension(
                TipoDefectoRepository.class,
                repository -> {

                    assertThat(repository.listarTodos())
                            .extracting(TipoDefectoRecord::idDefecto)
                            .containsExactly(activo, inactivo);

                    assertThat(repository.listarActivos())
                            .extracting(TipoDefectoRecord::idDefecto)
                            .containsExactly(activo);
                }
        );
    }

    @Test
    void buscaPorNombreNormalizado() {

        insertar(
                "Apariencia",
                "Lesión Cutánea",
                1,
                true
        );

        List<TipoDefectoRecord> encontrados = jdbi.withExtension(
                TipoDefectoRepository.class,
                repository -> repository.buscarPorNombre(
                        "LESIÓN CUTÁNEA"
                )
        );

        assertThat(encontrados).hasSize(1);
        assertThat(encontrados.get(0).nombre())
                .isEqualTo("Lesión Cutánea");
    }

    @Test
    void noDevuelveResultadosCuandoLaBusquedaEstaVacia() {

        insertar(
                "Apariencia",
                "Moretón",
                1,
                true
        );

        List<TipoDefectoRecord> encontrados = jdbi.withExtension(
                TipoDefectoRepository.class,
                repository -> repository.buscarPorNombre("   ")
        );

        assertThat(encontrados).isEmpty();
    }

    @Test
    void detectaNombresDuplicadosInclusoEnDefectosInactivos() {

        long id = insertar(
                "Apariencia",
                "Moretón",
                1,
                true
        );

        jdbi.useExtension(
                TipoDefectoRepository.class,
                repository -> {

                    repository.desactivar(id);

                    assertThat(repository.existeNombre("MORETÓN"))
                            .isTrue();

                    assertThat(
                            repository.existeNombreParaOtroDefecto(
                                    "Moretón",
                                    id
                            )
                    ).isFalse();

                    assertThat(
                            repository.existeNombreParaOtroDefecto(
                                    "Moretón",
                                    999L
                            )
                    ).isTrue();
                }
        );
    }

    @Test
    void rechazaDuplicadosNormalizadosEnLaBaseDeDatos() {

        insertar(
                "Apariencia",
                "Moretón",
                1,
                true
        );

        assertThatThrownBy(
                () -> insertar(
                        "Apariencia",
                        "MORETON",
                        2,
                        true
                )
        ).isInstanceOf(UnableToExecuteStatementException.class);
    }

    @Test
    void actualizaDefectoYRecalculaNombreNormalizado() {

        long id = insertar(
                "Apariencia",
                "Moretón",
                1,
                true
        );

        int filasActualizadas = jdbi.withExtension(
                TipoDefectoRepository.class,
                repository -> repository.actualizar(
                        id,
                        "Integridad",
                        "Corte Superficial",
                        3,
                        true
                )
        );

        TipoDefectoRecord actualizado = jdbi.withExtension(
                TipoDefectoRepository.class,
                repository -> repository.buscarPorId(id)
                        .orElseThrow()
        );

        assertThat(filasActualizadas).isEqualTo(1);
        assertThat(actualizado.grupo()).isEqualTo("Integridad");
        assertThat(actualizado.nombre())
                .isEqualTo("Corte Superficial");
        assertThat(actualizado.orden()).isEqualTo(3);
        assertThat(actualizado.nombreNormalizado())
                .isEqualTo("cortesuperficial");
    }

    @Test
    void desactivaYReactivaDefecto() {

        long id = insertar(
                "Apariencia",
                "Moretón",
                1,
                true
        );

        jdbi.useExtension(
                TipoDefectoRepository.class,
                repository -> {

                    assertThat(repository.desactivar(id)).isEqualTo(1);

                    assertThat(repository.listarActivos())
                            .isEmpty();

                    assertThat(repository.listarTodos())
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
                () -> insertar(
                        "Apariencia",
                        "   ",
                        1,
                        true
                )
        ).isInstanceOf(IllegalArgumentException.class);

        long id = insertar(
                "Apariencia",
                "Moretón",
                1,
                true
        );

        assertThatThrownBy(
                () -> jdbi.withExtension(
                        TipoDefectoRepository.class,
                        repository -> repository.actualizar(
                                id,
                                "Integridad",
                                "   ",
                                2,
                                true
                        )
                )
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void respetaElGrupoYOrdenAlListarDefectos() {

        long defectoOrdenTres = insertar(
                "Apariencia",
                "Herida",
                3,
                true
        );

        long defectoOrdenUno = insertar(
                "Apariencia",
                "Moretón",
                1,
                true
        );

        long otroGrupo = insertar(
                "Integridad",
                "Corte",
                1,
                true
        );

        List<TipoDefectoRecord> defectos = jdbi.withExtension(
                TipoDefectoRepository.class,
                TipoDefectoRepository::listarTodos
        );

        assertThat(defectos)
                .extracting(TipoDefectoRecord::idDefecto)
                .containsExactly(
                        defectoOrdenUno,
                        defectoOrdenTres,
                        otroGrupo
                );
    }
}