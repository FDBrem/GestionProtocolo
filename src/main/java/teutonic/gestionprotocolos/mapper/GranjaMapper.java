package teutonic.gestionprotocolos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import teutonic.gestionprotocolos.model.Granja;
import teutonic.gestionprotocolos.persistence.records.GranjaRecord;

@Mapper
public interface GranjaMapper {

    GranjaMapper INSTANCE = Mappers.getMapper(GranjaMapper.class);

    Granja toModel(GranjaRecord record);

    @Mapping(
            target = "nombreNormalizado",
            expression = "java(teutonic.gestionprotocolos.util.NombreNormalizer.normalizar(granja.nombre()))"
    )
    GranjaRecord toRecord(Granja granja);
}
