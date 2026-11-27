
package teutonic.gestionprotocolos.repository.fake;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import teutonic.gestionprotocolos.model.CatalogoItem;
import teutonic.gestionprotocolos.model.CatalogoTipo;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FakeCatalogoRepositoryTest {

    private CatalogoRepository repository;

    @BeforeEach
    void setUp() {
        repository = new FakeCatalogoRepository();
    }

    @Test
    void debeCargarLosTresCatalogos() {

        assertEquals(3,
                repository.listar(CatalogoTipo.GRANJA).size());

        assertEquals(4,
                repository.listar(CatalogoTipo.SECTOR).size());

        assertEquals(3,
                repository.listar(CatalogoTipo.CLIENTE).size());
    }

    @Test
    void debeCrearUnRegistro() {

        CatalogoItem nuevo = repository.crear(
                CatalogoTipo.GRANJA,
                "Granja Nueva"
        );

        assertNotNull(nuevo);
        assertEquals("Granja Nueva", nuevo.nombre());
        assertTrue(nuevo.activo());

        assertEquals(4,
                repository.listar(CatalogoTipo.GRANJA).size());
    }

    @Test
    void debeActualizarUnRegistro() {

        CatalogoItem actualizado = repository.actualizar(
                CatalogoTipo.GRANJA,
                1,
                "Granja Modificada"
        );

        assertEquals("Granja Modificada", actualizado.nombre());
        assertTrue(actualizado.activo());
    }

    @Test
    void debeCambiarElEstado() {

        CatalogoItem actualizado = repository.cambiarEstado(
                CatalogoTipo.GRANJA,
                1,
                false
        );

        assertFalse(actualizado.activo());
    }

    @Test
    void debeDetectarNombresDuplicados() {

        assertTrue(repository.existeNombre(
                CatalogoTipo.GRANJA,
                "  GRANJA NORTE  ",
                0
        ));
    }

    @Test
    void noDebeConsiderarDuplicadoElMismoRegistro() {

        assertFalse(repository.existeNombre(
                CatalogoTipo.GRANJA,
                "Granja Norte",
                1
        ));
    }

    @Test
    void debeMantenerLosCatalogosIndependientes() {

        repository.crear(
                CatalogoTipo.GRANJA,
                "Nueva Granja"
        );

        assertEquals(4,
                repository.listar(CatalogoTipo.GRANJA).size());

        assertEquals(4,
                repository.listar(CatalogoTipo.SECTOR).size());

        assertEquals(3,
                repository.listar(CatalogoTipo.CLIENTE).size());
    }

    @Test
    void noDebePermitirNombresVacios() {

        assertThrows(
                IllegalArgumentException.class,
                () -> repository.crear(
                        CatalogoTipo.GRANJA,
                        "   "
                )
        );
    }
}
