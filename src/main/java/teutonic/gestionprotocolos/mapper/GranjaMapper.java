package teutonic.gestionprotocolos.mapper;

import org.mapstruct.Mapper;
import teutonic.gestionprotocolos.model.Granja;
import teutonic.gestionprotocolos.persistence.records.GranjaRecord;

@Mapper
public interface GranjaMapper {

    Granja toModel(GranjaRecord record);

    GranjaRecord toRecord(Granja granja);
}
