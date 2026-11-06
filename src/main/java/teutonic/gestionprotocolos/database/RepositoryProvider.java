package teutonic.gestionprotocolos.database;

import org.jdbi.v3.core.Jdbi;
import teutonic.gestionprotocolos.config.DatabaseConfig;
import teutonic.gestionprotocolos.repository.AnalisisRepository;
import teutonic.gestionprotocolos.repository.ClienteRepository;
import teutonic.gestionprotocolos.repository.GranjaRepository;
import teutonic.gestionprotocolos.repository.MuestraRepository;
import teutonic.gestionprotocolos.repository.PlantillaRepository;
import teutonic.gestionprotocolos.repository.ProductoRepository;
import teutonic.gestionprotocolos.repository.ProtocoloRepository;
import teutonic.gestionprotocolos.repository.ResultadoRepository;
import teutonic.gestionprotocolos.repository.SectorRepository;
import teutonic.gestionprotocolos.repository.SerieMuestraRepository;

public final class RepositoryProvider {

    public static Jdbi getJdbi() {
        return JDBI;
    }

    private static final Jdbi JDBI = DatabaseConfig.getJdbi();

    private RepositoryProvider() {}

    public static GranjaRepository getGranjaRepository() {
        return JDBI.onDemand(GranjaRepository.class);
    }

    public static ResultadoRepository getResultadoRepository() {
        return JDBI.onDemand(ResultadoRepository.class);
    }

    public static SectorRepository getSectorRepository() {
        return JDBI.onDemand(SectorRepository.class);
    }

    public static PlantillaRepository getPlantillaRepository() {
        return JDBI.onDemand(PlantillaRepository.class);
    }

    public static AnalisisRepository getAnalisisRepository() {
        return JDBI.onDemand(AnalisisRepository.class);
    }

    public static MuestraRepository getMuestraRepository() {
        return JDBI.onDemand(MuestraRepository.class);
    }

    public static ProtocoloRepository getProtocoloRepository() {
        return JDBI.onDemand(ProtocoloRepository.class);
    }

    public static ProductoRepository getProductoRepository() {
        return JDBI.onDemand(ProductoRepository.class);
    }

    public static SerieMuestraRepository getSerieMuestraRepository() {
        return JDBI.onDemand(SerieMuestraRepository.class);
    }

    public static ClienteRepository getClienteRepository() {
        return JDBI.onDemand(ClienteRepository.class);
    }
}
