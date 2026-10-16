package teutonic.gestionprotocolos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import teutonic.gestionprotocolos.model.Sector;
import teutonic.gestionprotocolos.persistence.records.SectorRecord;

@Mapper
public interface SectorMapper {

    SectorMapper INSTANCE = Mappers.getMapper(SectorMapper.class);

    Sector toModel(SectorRecord record);

    SectorRecord toRecord(Sector sector);
}
