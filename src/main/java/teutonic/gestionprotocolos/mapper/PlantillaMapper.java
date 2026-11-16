package teutonic.gestionprotocolos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import teutonic.gestionprotocolos.model.Plantilla;
import teutonic.gestionprotocolos.persistence.records.PlantillaRecord;

@Mapper
public interface PlantillaMapper {

    PlantillaMapper INSTANCE = Mappers.getMapper(PlantillaMapper.class);

    Plantilla toModel(PlantillaRecord record);

    PlantillaRecord toRecord(Plantilla plantilla);
}
