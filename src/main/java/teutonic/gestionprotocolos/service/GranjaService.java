package teutonic.gestionprotocolos.service;

import teutonic.gestionprotocolos.model.Granja;

import java.util.List;
import java.util.Optional;

public interface GranjaService {

    Optional<Granja> buscarPorId(long id);

    List<Granja> listarTodas();

    List<Granja> listarActivas();

    Granja registrar(Granja granja);

    Granja actualizar(Granja granja);
}
