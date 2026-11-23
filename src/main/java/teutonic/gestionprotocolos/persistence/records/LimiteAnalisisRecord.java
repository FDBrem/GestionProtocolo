package teutonic.gestionprotocolos.persistence.records;

public record LimiteAnalisisRecord(
        Long idLimite,
        Long idAnalisis,
        Double limiteMaximo
) {}
