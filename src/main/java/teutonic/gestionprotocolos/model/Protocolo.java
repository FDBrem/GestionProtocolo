package teutonic.gestionprotocolos.model;

import java.time.LocalDate;

public record Protocolo(
        long idProtocolo,
        long idPlantilla,
        Integer numero,
        LocalDate fecha
) {}