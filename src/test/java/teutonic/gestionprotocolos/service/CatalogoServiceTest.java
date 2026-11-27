
package teutonic.gestionprotocolos.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import teutonic.gestionprotocolos.model.CatalogoItem;
import teutonic.gestionprotocolos.model.CatalogoTipo;
import teutonic.gestionprotocolos.repository.fake.FakeCatalogoRepository;
import teutonic.gestionprotocolos.service.exception.CatalogoException;

import static org.junit.jupiter.api.Assertions.*;

class CatalogoServiceTest {

    private CatalogoService service;

    @BeforeEach
    void setUp() {
        service = new CatalogoService(
                new FakeCatalogoRepository()
        );
    }

    @Test
    void debeCrearUnRegistro() {

        CatalogoItem nuevo = service.crear(
                CatalogoTipo.GRANJA,
                "Granja Nueva"
        );

        assertEquals("Granja Nueva", nuevo.nombre());
        assertTrue(nuevo.activo());
    }

    @Test
    void noDebePermitirNombresDuplicados() {

        assertThrows(
                CatalogoException.class,
                () -> service.crear(
                        CatalogoTipo.GRANJA,
                        " GRANJA NORTE "
                )
        );
    }

    @Test
    void noDebePermitirNombresVacios() {

        assertThrows(
                CatalogoException.class,
                () -> service.crear(
                        CatalogoTipo.CLIENTE,
                        "   "
                )
        );
    }

    @Test
    void debePermitirActualizarElMismoNombre() {

        CatalogoItem actualizado = service.actualizar(
                CatalogoTipo.GRANJA,
                1,
                "Granja Norte"
        );

        assertEquals("Granja Norte", actualizado.nombre());
    }

    @Test
    void noDebePermitirActualizarConNombreDuplicado() {

        assertThrows(
                CatalogoException.class,
                () -> service.actualizar(
                        CatalogoTipo.GRANJA,
                        1,
                        "Granja Sur"
                )
        );
    }

    @Test
    void debeConservarElEstadoAlActualizar() {

        CatalogoItem actualizado = service.actualizar(
                CatalogoTipo.GRANJA,
                3,
                "Granja Central Actualizada"
        );

        assertFalse(actualizado.activo());
    }

    @Test
    void debeBuscarPorNombreSinDistinguirMayusculas() {

        var resultados = service.buscar(
                CatalogoTipo.GRANJA,
                "GRAN"
        );

        assertEquals(3, resultados.size());
    }

    @Test
    void debeDevolverTodosLosRegistrosConBusquedaVacia() {

        var resultados = service.buscar(
                CatalogoTipo.SECTOR,
                ""
        );

        assertEquals(4, resultados.size());
    }

    @Test
    void debePermitirDesactivarUnRegistro() {

        CatalogoItem resultado = service.cambiarEstado(
                CatalogoTipo.GRANJA,
                1,
                false
        );

        assertFalse(resultado.activo());
    }

    @Test
    void debePermitirActivarUnRegistro() {

        CatalogoItem resultado = service.cambiarEstado(
                CatalogoTipo.GRANJA,
                3,
                true
        );

        assertTrue(resultado.activo());
    }

    @Test
    void noDebeActualizarUnRegistroInexistente() {

        assertThrows(
                CatalogoException.class,
                () -> service.actualizar(
                        CatalogoTipo.GRANJA,
                        999,
                        "Granja inexistente"
                )
        );
    }

    @Test
    void noDebeAceptarUnTipoNulo() {

        assertThrows(
                CatalogoException.class,
                () -> service.listar(null)
        );
    }
}

