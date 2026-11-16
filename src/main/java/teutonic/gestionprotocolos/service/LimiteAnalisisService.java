package teutonic.gestionprotocolos.service;

import teutonic.gestionprotocolos.model.LimiteAnalisis;

import java.util.List;
import java.util.Optional;

public interface LimiteAnalisisService {

    Optional<LimiteAnalisis> buscarPorAnalisis(long idAnalisis);

    List<LimiteAnalisis> listarTodos();

    void actualizar(long idAnalisis, double limiteMaximo);
}
