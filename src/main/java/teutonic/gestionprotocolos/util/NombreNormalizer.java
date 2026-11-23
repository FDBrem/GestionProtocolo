
package teutonic.gestionprotocolos.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Objects;

/**
 * Utilidad para normalizar nombres de los catálogos.
 *
 * Permite comparar nombres ignorando:
 * - Mayúsculas y minúsculas.
 * - Tildes y otros signos diacríticos.
 * - Espacios en blanco, incluso entre palabras.
 */
public final class NombreNormalizer {

    private NombreNormalizer() {
        // Evita la instanciación de esta clase.
    }

    public static String normalizar(String nombre) {
        Objects.requireNonNull(nombre, "El nombre no puede ser null");

        String descompuesto = Normalizer.normalize(
                nombre,
                Normalizer.Form.NFD
        );

        StringBuilder resultado = new StringBuilder();

        descompuesto
                .toLowerCase(Locale.ROOT)
                .codePoints()
                .filter(caracter ->
                        !esMarcaDiacritica(caracter)
                                && !esEspacio(caracter)
                )
                .forEach(resultado::appendCodePoint);

        return resultado.toString();
    }

    private static boolean esMarcaDiacritica(int caracter) {
        int tipo = Character.getType(caracter);

        return tipo == Character.NON_SPACING_MARK
                || tipo == Character.COMBINING_SPACING_MARK
                || tipo == Character.ENCLOSING_MARK;
    }

    private static boolean esEspacio(int caracter) {
        return Character.isWhitespace(caracter)
                || Character.isSpaceChar(caracter);
    }
}
