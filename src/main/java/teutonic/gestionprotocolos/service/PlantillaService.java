package teutonic.gestionprotocolos.service;

import teutonic.gestionprotocolos.model.PlantillaFormulario;

import java.util.List;
import java.util.Optional;

public interface PlantillaService {

    Optional<PlantillaFormulario> buscarFormularioPorId(long id);

    List<PlantillaFormulario> listarFormularios();

    List<PlantillaFormulario> buscarFormulariosPorProducto(long idProducto);
}