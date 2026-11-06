package teutonic.gestionprotocolos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import teutonic.gestionprotocolos.model.Muestra;
import teutonic.gestionprotocolos.persistence.records.MuestraRecord;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Mapper
public interface MuestraMapper {

    MuestraMapper INSTANCE = Mappers.getMapper(MuestraMapper.class);

    DateTimeFormatter SQLITE_DATE_TIME =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Mapping(target = "id", source = "idMuestra")
    @Mapping(target = "protocoloId", source = "idProtocolo")
    @Mapping(target = "productoId", source = "idProducto")
    @Mapping(target = "serieId", source = "idSerie")
    @Mapping(target = "granjaId", source = "idGranja")
    @Mapping(target = "sectorId", source = "idSector")
    @Mapping(target = "fechaToma", source = "fechaToma", qualifiedByName = "stringToLocalDate")
    @Mapping(target = "horaToma", source = "horaToma", qualifiedByName = "stringToLocalTime")
    @Mapping(target = "creadaEn", source = "creadoEn", qualifiedByName = "stringToLocalDateTime")
    @Mapping(target = "anulada", source = "anulado", qualifiedByName = "integerToBoolean")
    @Mapping(target = "motivoAnulacion", source = "anuladoMotivo")
    Muestra toModel(MuestraRecord record);

    @Mapping(target = "idMuestra", source = "id")
    @Mapping(target = "idProtocolo", source = "protocoloId")
    @Mapping(target = "idProducto", source = "productoId")
    @Mapping(target = "idSerie", source = "serieId")
    @Mapping(target = "idGranja", source = "granjaId")
    @Mapping(target = "idSector", source = "sectorId")
    @Mapping(target = "fechaToma", source = "fechaToma", qualifiedByName = "localDateToString")
    @Mapping(target = "horaToma", source = "horaToma", qualifiedByName = "localTimeToString")
    @Mapping(target = "creadoEn", source = "creadaEn", qualifiedByName = "localDateTimeToString")
    @Mapping(target = "anulado", source = "anulada", qualifiedByName = "booleanToInteger")
    @Mapping(target = "anuladoMotivo", source = "motivoAnulacion")
    MuestraRecord toRecord(Muestra muestra);

    @Named("stringToLocalDate")
    default LocalDate stringToLocalDate(String value) {
        return value == null ? null : LocalDate.parse(value);
    }

    @Named("stringToLocalTime")
    default LocalTime stringToLocalTime(String value) {
        return value == null ? null : LocalTime.parse(value);
    }

    @Named("stringToLocalDateTime")
    default LocalDateTime stringToLocalDateTime(String value) {
        if (value == null) {
            return null;
        }
        return LocalDateTime.parse(value, SQLITE_DATE_TIME);
    }

    @Named("localDateToString")
    default String localDateToString(LocalDate value) {
        return value == null ? null : value.toString();
    }

    @Named("localTimeToString")
    default String localTimeToString(LocalTime value) {
        return value == null ? null : value.toString();
    }

    @Named("localDateTimeToString")
    default String localDateTimeToString(LocalDateTime value) {
        return value == null ? null : SQLITE_DATE_TIME.format(value);
    }

    @Named("integerToBoolean")
    default boolean integerToBoolean(Integer value) {
        return value != null && value != 0;
    }

    @Named("booleanToInteger")
    default Integer booleanToInteger(boolean value) {
        return value ? 1 : 0;
    }
}
