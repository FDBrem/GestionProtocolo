package teutonic.gestionprotocolos.persistence.records;

import java.time.LocalDate;

public record ProtocoloRecord(
        long idProtocolo,
        long idPlantilla,
        Integer numero,
        LocalDate fecha
) {
}