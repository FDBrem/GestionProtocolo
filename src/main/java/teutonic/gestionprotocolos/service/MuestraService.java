package teutonic.gestionprotocolos.service;

import teutonic.gestionprotocolos.model.Muestra;

import java.util.List;
import java.util.Optional;

public interface MuestraService {

    long guardar(Muestra muestra);

    Optional<Muestra> buscarPorId(long id);

    List<Muestra> listarActivas();

    List<Muestra> listarTodas();

    List<Muestra> buscarPorProtocolo(long protocoloId);

    boolean anular(long id, String motivo);

    boolean eliminar(long id);
}