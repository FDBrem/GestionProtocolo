package teutonic.gestionprotocolos.service.parser;

import java.util.Locale;

public class ResultadoParser {

    //este patron permite usar 1200; 0,07; 0.07
    //Pero rechaza: 12,3,4; abc123; 6..6 y asi
    private static final String PATRON_NUMERICO =
            "\\d+(?:[,.°]\\d+)?";

    public ResultadoParseado parse(String texto) {

        if (texto == null || texto.isBlank()) {
            throw new ErrorFormatoResultadoException(
                    "El resultado no puede estar vacío."
            );
        }

        //esto es para normalizar espacios y mayúsculas/minúsculas
        String entrada = texto.trim()
                .toUpperCase(Locale.ROOT);

        //Primero verificamos los resultados cualitativos
        ResultadoParseado cualitativo = parsearPresencia(entrada);

        if (cualitativo != null) {
            return cualitativo;
        }

        //y después se verifican los resultados numéricos
        return parsearNumerico(entrada);
    }

    private ResultadoParseado parsearPresencia(String entrada) {

        return switch (entrada) {
            case "ND", "ND/A", "A", "AUSENCIA" -> new ResultadoParseado(null, false, 0);

            case "P", "PRESENCIA" -> new ResultadoParseado(null, false, 1);

            default -> null;
        };
    }

    private ResultadoParseado parsearNumerico(String entrada) {

        boolean menorQue = entrada.startsWith("<");

        String valorTexto = menorQue
                ? entrada.substring(1).trim()
                : entrada;

        if (valorTexto.isEmpty()) {
            throw new ErrorFormatoResultadoException(
                    "Debe ingresar un valor después del símbolo '<'."
            );
        }

        if (valorTexto.startsWith("-")) {
            throw new ErrorFormatoResultadoException(
                    "No se permiten resultados negativos."
            );
        }

        if (!valorTexto.matches(PATRON_NUMERICO)) {
            throw new ErrorFormatoResultadoException(
                    "Formato de resultado inválido: '" + entrada + "'."
            );
        }

        //Normalizamos los separadores decimales a punto, aca puse para reemplazar el de grados a punto tambien
        String valorNormalizado = valorTexto
                .replace(',', '.')
                .replace('°', '.');

        try {
            double valor = Double.parseDouble(valorNormalizado);

            if (!Double.isFinite(valor)) {
                throw new ErrorFormatoResultadoException(
                        "El resultado debe ser un número válido."
                );
            }

            return new ResultadoParseado(
                    valor,
                    menorQue,
                    null
            );

        } catch (NumberFormatException e) {
            throw new ErrorFormatoResultadoException(
                    "No se pudo interpretar el resultado: '" + entrada + "'."
            );
        }
    }
}
