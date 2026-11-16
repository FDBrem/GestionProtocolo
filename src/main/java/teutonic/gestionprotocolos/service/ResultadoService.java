package teutonic.gestionprotocolos.service;

import teutonic.gestionprotocolos.model.Resultado;

import java.util.List;
import java.util.Optional;

public interface ResultadoService {

    Optional<Resultado> buscarPorId(long id);

    List<Resultado> listarPorMuestra(long muestraId);

    Resultado registrar(Resultado resultado);

    Resultado actualizar(Resultado resultado);
}
