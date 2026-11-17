package teutonic.gestionprotocolos.service.parser;

public record ResultadoParseado(
        Double valor,
        boolean menorQue,
        Integer presencia
) {
}
