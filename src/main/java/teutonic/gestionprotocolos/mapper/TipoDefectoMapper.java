package teutonic.gestionprotocolos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import teutonic.gestionprotocolos.model.TipoDefecto;
import teutonic.gestionprotocolos.persistence.records.TipoDefectoRecord;
import teutonic.gestionprotocolos.util.NombreNormalizer;

@Mapper
public interface TipoDefectoMapper {

    TipoDefectoMapper INSTANCE = Mappers.getMapper(TipoDefectoMapper.class);

    TipoDefecto toModel(TipoDefectoRecord record);

    @Mapping(
            target = "nombreNormalizado",
            expression = "java(NombreNormalizer.normalizar(tipoDefecto.nombre()))"
    )
    TipoDefectoRecord toRecord(TipoDefecto tipoDefecto);
}
