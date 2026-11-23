package teutonic.gestionprotocolos.persistence.records.registro;

import java.util.List;

public record RegistroAnalisisRequest(
        ProtocoloRequest protocolo,
        List<MuestraRequest> muestras
) {}