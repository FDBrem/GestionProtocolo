package teutonic.gestionprotocolos.model;

public record Resultado(
        long idResultado,
        long idMuestra,
        long idAnalisis,
        String horaControl,
        Double valor,
        int menorQue,
        Integer colonias,
        Integer presencia,
        Integer conforme,
        int pendiente,
        String observacion,
        String creadoEn
) {}
