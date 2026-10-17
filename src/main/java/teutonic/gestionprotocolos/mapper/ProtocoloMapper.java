package teutonic.gestionprotocolos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import teutonic.gestionprotocolos.model.Protocolo;
import teutonic.gestionprotocolos.persistence.records.ProtocoloRecord;

@Mapper
public interface ProtocoloMapper {

    ProtocoloMapper INSTANCE = Mappers.getMapper(ProtocoloMapper.class);

    Protocolo toModel(ProtocoloRecord record);

    ProtocoloRecord toRecord(Protocolo protocolo);
}