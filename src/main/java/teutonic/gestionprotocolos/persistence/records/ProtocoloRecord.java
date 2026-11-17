package teutonic.gestionprotocolos.persistence.records;

import java.time.LocalDate;

public record ProtocoloRecord(
        long idProtocolo,
        long idPlantilla,
        Integer numero,
        LocalDate fecha,
        Long idCliente,
        String metodo,
        String equipo,
        Double pesoMuestraG,
        Double superficieCm2,
        String analista,
        String observaciones
) {}