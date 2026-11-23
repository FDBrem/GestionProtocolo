package teutonic.gestionprotocolos.persistence.records.registro;

import java.time.LocalDate;

public record ProtocoloRequest(
        long idPlantilla,
        LocalDate fecha,
        Long idCliente,
        String metodo,
        String equipo,
        Double pesoMuestraG,
        Double superficieCm2,
        String analista,
        String observaciones
) {}
