package teutonic.gestionprotocolos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import teutonic.gestionprotocolos.model.Cliente;
import teutonic.gestionprotocolos.persistence.records.ClienteRecord;
import teutonic.gestionprotocolos.util.NombreNormalizer;

@Mapper
public interface ClienteMapper {

    ClienteMapper INSTANCE = Mappers.getMapper(ClienteMapper.class);

    Cliente toModel(ClienteRecord record);

    @Mapping(
            target = "nombreNormalizado",
            expression = "java(NombreNormalizer.normalizar(cliente.nombre()))"
    )
    ClienteRecord toRecord(Cliente cliente);
}