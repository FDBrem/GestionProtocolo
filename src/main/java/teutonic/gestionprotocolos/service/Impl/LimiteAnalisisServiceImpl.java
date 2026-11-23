package teutonic.gestionprotocolos.service.Impl;

import teutonic.gestionprotocolos.database.RepositoryProvider;
import teutonic.gestionprotocolos.mapper.LimiteAnalisisMapper;
import teutonic.gestionprotocolos.model.LimiteAnalisis;
import teutonic.gestionprotocolos.repository.LimiteAnalisisRepository;
import teutonic.gestionprotocolos.service.LimiteAnalisisService;

import java.util.List;
import java.util.Optional;

public class LimiteAnalisisServiceImpl implements LimiteAnalisisService {

    private final LimiteAnalisisRepository limiteRepository;
    private final LimiteAnalisisMapper limiteMapper;

    public LimiteAnalisisServiceImpl() {
        this(
                RepositoryProvider.getLimiteAnalisisRepository(),
                LimiteAnalisisMapper.INSTANCE
        );
    }

    public LimiteAnalisisServiceImpl(
            LimiteAnalisisRepository limiteRepository,
            LimiteAnalisisMapper limiteMapper
    ) {
        this.limiteRepository = limiteRepository;
        this.limiteMapper = limiteMapper;
    }

    @Override
    public Optional<LimiteAnalisis> buscarPorAnalisis(long idAnalisis) {
        return limiteRepository.buscarPorAnalisis(idAnalisis)
                .map(limiteMapper::toModel);
    }

    @Override
    public List<LimiteAnalisis> listarTodos() {
        return limiteRepository.listarTodos()
                .stream()
                .map(limiteMapper::toModel)
                .toList();
    }

    @Override
    public void actualizar(long idAnalisis, double limiteMaximo) {
        if (idAnalisis <= 0) {
            throw new IllegalArgumentException(
                    "El ID del análisis debe ser mayor que cero"
            );
        }
        //uso isFinite para evitar guardar valores invalidos desde la interfaz, es mas amplio que usar <0
        if (!Double.isFinite(limiteMaximo) || limiteMaximo <= 0) {
            throw new IllegalArgumentException(
                    "El límite máximo debe ser un número positivo y finito"
            );
        }

        int filasActualizadas = limiteRepository.actualizar(
                idAnalisis,
                limiteMaximo
        );

        if (filasActualizadas == 0) {
            throw new IllegalArgumentException(
                    "No existe un límite configurado para el análisis " + idAnalisis
            );
        }
    }
}
