
package teutonic.gestionprotocolos.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NombreNormalizerTest {

    @Test
    void ignoraMayusculasYMinusculas() {
        assertThat(NombreNormalizer.normalizar("SECTOR"))
                .isEqualTo(NombreNormalizer.normalizar("sector"));
    }

    @Test
    void ignoraTildes() {
        assertThat(NombreNormalizer.normalizar("Análisis"))
                .isEqualTo(NombreNormalizer.normalizar("Analisis"));
    }

    @Test
    void ignoraEspacios() {
        assertThat(NombreNormalizer.normalizar("Pollo Entero"))
                .isEqualTo(NombreNormalizer.normalizar("polloentero"));

        assertThat(NombreNormalizer.normalizar("  Pollo   Entero "))
                .isEqualTo("polloentero");
    }

    @Test
    void combinaTodasLasReglasDeNormalizacion() {
        assertThat(NombreNormalizer.normalizar("  HARÍNA   DE PLÚMAS "))
                .isEqualTo("harinadeplumas");
    }

    @Test
    void noConfundeNombresConDiferenteEscritura() {
        assertThat(NombreNormalizer.normalizar("Gaillard"))
                .isNotEqualTo(NombreNormalizer.normalizar("Gallard"));
    }

    @Test
    void rechazaNombresNulos() {
        assertThatThrownBy(() -> NombreNormalizer.normalizar(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void devuelveCadenaVaciaCuandoElNombreSoloTieneEspacios() {
        assertThat(NombreNormalizer.normalizar("   "))
                .isEmpty();
    }
}
