package teutonic.gestionprotocolos.mapper;

import teutonic.gestionprotocolos.model.Granja;
import teutonic.gestionprotocolos.persistence.records.GranjaRecord;

public final class GranjaMapper {

    private GranjaMapper() {}

    public static Granja toModel(GranjaRecord record) {
        if (record == null) {
            return null;
        }

        if (record.idGranja() == null) {
            throw new IllegalArgumentException(
                    "La granja debe tener un ID para construir el modelo"
            );
        }

        return new Granja(
                record.idGranja(),
                record.nombre(),
                record.activo()
        );
    }

    public static GranjaRecord toRecord(Granja granja) {
        if (granja == null) {
            return null;
        }

        return new GranjaRecord(
                granja.idGranja(),
                granja.nombre(),
                granja.activo()
        );
    }
}
