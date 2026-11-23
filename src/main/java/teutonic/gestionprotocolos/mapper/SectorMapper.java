package teutonic.gestionprotocolos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import teutonic.gestionprotocolos.model.Sector;
import teutonic.gestionprotocolos.persistence.records.SectorRecord;
import teutonic.gestionprotocolos.util.NombreNormalizer;

@Mapper
public interface SectorMapper {

    SectorMapper INSTANCE = Mappers.getMapper(SectorMapper.class);

    Sector toModel(SectorRecord record);

    @Mapping(
            target = "nombreNormalizado",
            expression = "java(NombreNormalizer.normalizar(sector.nombre()))"
    )
    SectorRecord toRecord(Sector sector);
}