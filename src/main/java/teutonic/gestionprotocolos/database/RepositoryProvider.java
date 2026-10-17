package teutonic.gestionprotocolos.database;

import org.jdbi.v3.core.Jdbi;
import teutonic.gestionprotocolos.config.DatabaseConfig;
import teutonic.gestionprotocolos.repository.PlantillaRepository;
import teutonic.gestionprotocolos.repository.SectorRepository;

public final class RepositoryProvider {

    private static final Jdbi JDBI = DatabaseConfig.getJdbi();

    private RepositoryProvider() {}

    public static SectorRepository getSectorRepository() {
        return JDBI.onDemand(SectorRepository.class);
    }

    public static PlantillaRepository getPlantillaRepository() {
        return JDBI.onDemand(PlantillaRepository.class);
    }

}
