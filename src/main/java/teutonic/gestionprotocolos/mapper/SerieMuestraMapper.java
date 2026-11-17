package teutonic.gestionprotocolos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import teutonic.gestionprotocolos.model.SerieMuestra;
import teutonic.gestionprotocolos.persistence.records.SerieMuestraRecord;

@Mapper
public interface SerieMuestraMapper {

    SerieMuestraMapper INSTANCE = Mappers.getMapper(SerieMuestraMapper.class);

    SerieMuestra toModel(SerieMuestraRecord record);

    SerieMuestraRecord toRecord(SerieMuestra serieMuestra);
}
