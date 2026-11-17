package teutonic.gestionprotocolos.persistence.records;

public record ResultadoValidado(
        Double valor,
        boolean menorQue,
        Integer colonias,
        Integer presencia,
        Integer conforme,
        boolean pendiente,
        String observacion,
        Double limiteAplicado
) {}
