package teutonic.gestionprotocolos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import teutonic.gestionprotocolos.model.Analisis;
import teutonic.gestionprotocolos.persistence.records.AnalisisPlantillaRecord;
import teutonic.gestionprotocolos.persistence.records.AnalisisRecord;

@Mapper
public interface AnalisisMapper {

    AnalisisMapper INSTANCE = Mappers.getMapper(AnalisisMapper.class);

    Analisis toModel(AnalisisRecord record);

    Analisis toModel(AnalisisPlantillaRecord record);

    AnalisisRecord toRecord(Analisis analisis);
}
