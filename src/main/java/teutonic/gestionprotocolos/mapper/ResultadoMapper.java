package teutonic.gestionprotocolos.mapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import teutonic.gestionprotocolos.model.Resultado;
import teutonic.gestionprotocolos.persistence.records.ResultadoRecord;

@Mapper
public interface ResultadoMapper {

    ResultadoMapper INSTANCE = Mappers.getMapper(ResultadoMapper.class);

    Resultado toModel(ResultadoRecord record);

    ResultadoRecord toRecord(Resultado resultado);
}