package teutonic.gestionprotocolos.service.Impl;

import teutonic.gestionprotocolos.model.Analisis;
import teutonic.gestionprotocolos.model.AnalisisPlantilla;
import teutonic.gestionprotocolos.model.PlantillaFormulario;
import teutonic.gestionprotocolos.persistence.records.PlantillaCompletaRecord;
import teutonic.gestionprotocolos.repository.PlantillaRepository;
import teutonic.gestionprotocolos.service.PlantillaService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PlantillaServiceImpl implements PlantillaService {

    private final PlantillaRepository plantillaRepository;

    public PlantillaServiceImpl(PlantillaRepository plantillaRepository) {
        this.plantillaRepository = plantillaRepository;
    }

    @Override
    public Optional<PlantillaFormulario> buscarFormularioPorId(long id) {
        List<PlantillaCompletaRecord> registros =
                plantillaRepository.buscarCompletaPorId(id);

        return construirFormularios(registros)
                .stream()
                .findFirst();
    }

    @Override
    public List<PlantillaFormulario> listarFormularios() {
        return construirFormularios(
                plantillaRepository.listarTodasCompletas()
        );
    }

    @Override
    public List<PlantillaFormulario> buscarFormulariosPorProducto(long idProducto) {
        return construirFormularios(
                plantillaRepository.buscarCompletasPorProducto(idProducto)
        );
    }

    private List<PlantillaFormulario> construirFormularios(
            List<PlantillaCompletaRecord> registros
    ) {
        Map<Long, PlantillaBuilder> plantillas = new LinkedHashMap<>();

        for (PlantillaCompletaRecord registro : registros) {
            PlantillaBuilder plantilla = plantillas.computeIfAbsent(
                    registro.idPlantilla(),
                    id -> new PlantillaBuilder(registro)
            );

            if (registro.idAnalisis() != null) {
                plantilla.analisis().add(
                        construirAnalisis(registro)
                );
            }
        }

        return plantillas.values()
                .stream()
                .map(PlantillaBuilder::build)
                .toList();
    }

    private AnalisisPlantilla construirAnalisis(
            PlantillaCompletaRecord registro
    ) {
        Analisis analisis = new Analisis(
                registro.idAnalisis(),
                registro.nombreAnalisis(),
                registro.tipoValor(),
                registro.unidad()
        );

        return new AnalisisPlantilla(
                analisis,
                registro.orden(),
                registro.limiteMax(),
                registro.etiquetaNeg(),
                registro.etiquetaPos()
        );
    }

    private record PlantillaBuilder(
            long idPlantilla,
            String nombre,
            String familia,
            Long idProducto,
            String metodoDefault,
            String equipoDefault,
            Double pesoMuestraG,
            Double superficieCm2,
            boolean usaNumero,
            List<AnalisisPlantilla> analisis
    ) {

        PlantillaBuilder(PlantillaCompletaRecord registro) {
            this(
                    registro.idPlantilla(),
                    registro.nombrePlantilla(),
                    registro.familia(),
                    registro.idProducto(),
                    registro.metodoDefault(),
                    registro.equipoDefault(),
                    registro.pesoMuestraG(),
                    registro.superficieCm2(),
                    registro.usaNumero(),
                    new java.util.ArrayList<>()
            );
        }

        PlantillaFormulario build() {
            return new PlantillaFormulario(
                    idPlantilla,
                    nombre,
                    familia,
                    idProducto,
                    metodoDefault,
                    equipoDefault,
                    pesoMuestraG,
                    superficieCm2,
                    usaNumero,
                    List.copyOf(analisis)
            );
        }
    }
}