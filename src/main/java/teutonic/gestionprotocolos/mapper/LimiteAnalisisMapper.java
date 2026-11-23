package teutonic.gestionprotocolos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import teutonic.gestionprotocolos.model.LimiteAnalisis;
import teutonic.gestionprotocolos.persistence.records.LimiteAnalisisRecord;

@Mapper(componentModel = "default")
public interface LimiteAnalisisMapper {

    LimiteAnalisisMapper INSTANCE = Mappers.getMapper(LimiteAnalisisMapper.class);

    LimiteAnalisis toModel(LimiteAnalisisRecord record);
}
