package teutonic.gestionprotocolos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import teutonic.gestionprotocolos.model.Producto;
import teutonic.gestionprotocolos.persistence.records.ProductoRecord;
import teutonic.gestionprotocolos.util.NombreNormalizer;

@Mapper
public interface ProductoMapper {

    ProductoMapper INSTANCE = Mappers.getMapper(ProductoMapper.class);

    Producto toModel(ProductoRecord record);

    @Mapping(
            target = "nombreNormalizado",
            expression = "java(NombreNormalizer.normalizar(producto.nombre()))"
    )
    ProductoRecord toRecord(Producto producto);
}
