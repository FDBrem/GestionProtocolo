package teutonic.gestionprotocolos.service.Impl;

import org.jdbi.v3.core.Jdbi;
import teutonic.gestionprotocolos.database.RepositoryProvider;
import teutonic.gestionprotocolos.mapper.MuestraMapper;
import teutonic.gestionprotocolos.model.Muestra;
import teutonic.gestionprotocolos.persistence.records.MuestraRecord;
import teutonic.gestionprotocolos.repository.MuestraRepository;
import teutonic.gestionprotocolos.service.MuestraService;

import java.util.List;
import java.util.Optional;

public class MuestraServiceImpl implements MuestraService {

    private final Jdbi jdbi;
    private final MuestraMapper mapper;

    public MuestraServiceImpl() {
        this.jdbi = RepositoryProvider.getJdbi();
        this.mapper = MuestraMapper.INSTANCE;
    }

    @Override
    public long guardar(Muestra muestra) {

        if (muestra == null) {
            throw new IllegalArgumentException("La muestra no puede ser null");
        }

        if (muestra.serieId() == null) {
            throw new IllegalArgumentException(
                    "La muestra debe tener una serie asignada"
            );
        }

        return jdbi.inTransaction(handle -> {

            MuestraRepository repository =
                    handle.attach(MuestraRepository.class);

            // Obtener el siguiente número interno de la serie.
            int numeroCorrelativo =
                    repository.obtenerSiguienteCorrelativo(muestra.serieId());

            // Calcular el número visible: 1 a 1000.
            int numeroMuestra =
                    ((numeroCorrelativo - 1) % 1000) + 1;

            // Crear una muestra con su numeración definitiva.
            Muestra muestraNumerada = new Muestra(
                    null,
                    muestra.protocoloId(),
                    muestra.productoId(),
                    muestra.serieId(),
                    numeroCorrelativo,
                    numeroMuestra,
                    muestra.fechaToma(),
                    muestra.horaToma(),
                    muestra.granjaId(),
                    muestra.lote(),
                    muestra.sectorId(),
                    muestra.elemento(),
                    muestra.bolson(),
                    muestra.temperaturaCarcaza(),
                    muestra.temperaturaProceso(),
                    muestra.temperaturaCongelada(),
                    muestra.temperaturaProducto(),
                    false,
                    null,
                    null
            );

            MuestraRecord record = mapper.toRecord(muestraNumerada);

            return repository.insertar(record);
        });
    }

    @Override
    public Optional<Muestra> buscarPorId(long id) {
        return RepositoryProvider.getMuestraRepository()
                .buscarPorId(id)
                .map(mapper::toModel);
    }

    @Override
    public List<Muestra> listarActivas() {
        return RepositoryProvider.getMuestraRepository()
                .listarActivas()
                .stream()
                .map(mapper::toModel)
                .toList();
    }

    @Override
    public List<Muestra> listarTodas() {
        return RepositoryProvider.getMuestraRepository()
                .listarTodas()
                .stream()
                .map(mapper::toModel)
                .toList();
    }

    @Override
    public List<Muestra> buscarPorProtocolo(long protocoloId) {
        return RepositoryProvider.getMuestraRepository()
                .buscarPorProtocolo(protocoloId)
                .stream()
                .map(mapper::toModel)
                .toList();
    }

    @Override
    public boolean anular(long id, String motivo) {
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException(
                    "Debe especificar un motivo de anulación"
            );
        }

        return RepositoryProvider.getMuestraRepository()
                .anular(id, motivo.trim()) > 0;
    }

    @Override
    public boolean eliminar(long id) {
        return RepositoryProvider.getMuestraRepository()
                .eliminar(id) > 0;
    }
}
