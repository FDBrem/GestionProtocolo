package teutonic.gestionprotocolos.persistence.records;

public record ResultadoRecord(
        Long idResultado,
        Long idMuestra,
        Long idAnalisis,
        String horaControl,
        Double valor,
        Integer menorQue,
        Integer colonias,
        Integer presencia,
        Integer conforme,
        Integer pendiente,
        String observacion,
        String creadoEn
) {}
