package teutonic.gestionprotocolos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import teutonic.gestionprotocolos.model.Cliente;
import teutonic.gestionprotocolos.persistence.records.ClienteRecord;

@Mapper
public interface ClienteMapper {

    ClienteMapper INSTANCE = Mappers.getMapper(ClienteMapper.class);

    Cliente toModel(ClienteRecord record);

    ClienteRecord toRecord(Cliente cliente);
}
