package teutonic.gestionprotocolos.persistence.records;

public record AnalisisRecord(
        long idAnalisis,
        String nombre,
        String tipoValor,
        String unidad
) {
}
