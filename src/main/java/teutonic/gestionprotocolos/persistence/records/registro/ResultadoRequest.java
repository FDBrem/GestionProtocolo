package teutonic.gestionprotocolos.persistence.records.registro;

public record ResultadoRequest(
        long idAnalisis,
        String horaControl,
        Double valor,
        boolean menorQue,
        Integer colonias,
        Integer presencia,
        boolean pendiente,
        String observacion
) {}