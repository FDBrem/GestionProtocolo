package teutonic.gestionprotocolos.service;

import teutonic.gestionprotocolos.model.Protocolo;

import java.util.List;
import java.util.Optional;

public interface ProtocoloService {

    Optional<Protocolo> buscarPorId(long id);

    List<Protocolo> listarActivos();

    Protocolo registrar(Protocolo protocolo);

    Protocolo actualizar(Protocolo protocolo);

    void anular(long id, String motivo);
}
