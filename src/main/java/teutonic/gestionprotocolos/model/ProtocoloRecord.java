package teutonic.gestionprotocolos.model;

import java.time.LocalDate;

public record ProtocoloRecord(
        long idProtocolo,
        long idPlantilla,
        Integer numero,
        LocalDate fecha
) {
}