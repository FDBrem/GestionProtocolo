package teutonic.gestionprotocolos.service.parser;

public final class ParserProvider {

    // Una única instancia compartida en toda la aplicación.
    private static final ResultadoParser RESULTADO_PARSER =
            new ResultadoParser();

    // Evitamos que se creen instancias de esta clase.
    private ParserProvider() {
    }

    public static ResultadoParser getResultadoParser() {
        return RESULTADO_PARSER;
    }
}
