package teutonic.gestionprotocolos.repository;

import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import teutonic.gestionprotocolos.persistence.records.PlantillaRecord;

import java.util.List;
import java.util.Optional;

@RegisterConstructorMapper(PlantillaRecord.class)
public interface PlantillaRepository {

    @SqlQuery("""
            SELECT
                id_plantilla AS idPlantilla,
                nombre,
                familia,
                id_producto AS idProducto,
                metodo_default AS metodoDefault,
                equipo_default AS equipoDefault,
                peso_muestra_g AS pesoMuestraG,
                superficie_cm2 AS superficieCm2,
                usa_numero AS usaNumero
            FROM plantilla
            WHERE id_plantilla = :id
            """)
    Optional<PlantillaRecord> buscarPorId(@Bind("id") long id);

    @SqlQuery("""
            SELECT
                id_plantilla AS idPlantilla,
                nombre,
                familia,
                id_producto AS idProducto,
                metodo_default AS metodoDefault,
                equipo_default AS equipoDefault,
                peso_muestra_g AS pesoMuestraG,
                superficie_cm2 AS superficieCm2,
                usa_numero AS usaNumero
            FROM plantilla
            ORDER BY nombre
            """)
    List<PlantillaRecord> listarTodas();

    @SqlQuery("""
            SELECT
                id_plantilla AS idPlantilla,
                nombre,
                familia,
                id_producto AS idProducto,
                metodo_default AS metodoDefault,
                equipo_default AS equipoDefault,
                peso_muestra_g AS pesoMuestraG,
                superficie_cm2 AS superficieCm2,
                usa_numero AS usaNumero
            FROM plantilla
            WHERE id_producto = :idProducto
            ORDER BY nombre
            """)
    List<PlantillaRecord> buscarPorProducto(@Bind("idProducto") long idProducto);

    @SqlUpdate("""
            INSERT INTO plantilla (
                nombre,
                familia,
                id_producto,
                metodo_default,
                equipo_default,
                peso_muestra_g,
                superficie_cm2,
                usa_numero
            )
            VALUES (
                :nombre,
                :familia,
                :idProducto,
                :metodoDefault,
                :equipoDefault,
                :pesoMuestraG,
                :superficieCm2,
                :usaNumero
            )
            """)
    @GetGeneratedKeys("id_plantilla")
    long insertar(
            @Bind("nombre") String nombre,
            @Bind("familia") String familia,
            @Bind("idProducto") Long idProducto,
            @Bind("metodoDefault") String metodoDefault,
            @Bind("equipoDefault") String equipoDefault,
            @Bind("pesoMuestraG") Double pesoMuestraG,
            @Bind("superficieCm2") Double superficieCm2,
            @Bind("usaNumero") boolean usaNumero
    );

    @SqlUpdate("""
            INSERT INTO plantilla (
                id_plantilla,
                nombre,
                familia,
                id_producto,
                metodo_default,
                equipo_default,
                peso_muestra_g,
                superficie_cm2,
                usa_numero
            )
            VALUES (
                :id,
                :nombre,
                :familia,
                :idProducto,
                :metodoDefault,
                :equipoDefault,
                :pesoMuestraG,
                :superficieCm2,
                :usaNumero
            )
            """)
    int insertRaw(
            @Bind("id") long id,
            @Bind("nombre") String nombre,
            @Bind("familia") String familia,
            @Bind("idProducto") Long idProducto,
            @Bind("metodoDefault") String metodoDefault,
            @Bind("equipoDefault") String equipoDefault,
            @Bind("pesoMuestraG") Double pesoMuestraG,
            @Bind("superficieCm2") Double superficieCm2,
            @Bind("usaNumero") boolean usaNumero
    );

    @SqlUpdate("""
            UPDATE plantilla
            SET
                nombre = :nombre,
                familia = :familia,
                id_producto = :idProducto,
                metodo_default = :metodoDefault,
                equipo_default = :equipoDefault,
                peso_muestra_g = :pesoMuestraG,
                superficie_cm2 = :superficieCm2,
                usa_numero = :usaNumero
            WHERE id_plantilla = :id
            """)
    int actualizar(
            @Bind("id") long id,
            @Bind("nombre") String nombre,
            @Bind("familia") String familia,
            @Bind("idProducto") Long idProducto,
            @Bind("metodoDefault") String metodoDefault,
            @Bind("equipoDefault") String equipoDefault,
            @Bind("pesoMuestraG") Double pesoMuestraG,
            @Bind("superficieCm2") Double superficieCm2,
            @Bind("usaNumero") boolean usaNumero
    );

    @SqlUpdate("""
            DELETE FROM plantilla
            WHERE id_plantilla = :id
            """)
    int eliminar(@Bind("id") long id);

    //metodos de conveniencia compatibles
    default Optional<PlantillaRecord> findById(long id) {
        return buscarPorId(id);
    }

    default List<PlantillaRecord> findAll() {
        return listarTodas();
    }

    default int delete(long id) {
        return eliminar(id);
    }

    default int update(PlantillaRecord plantilla) {
        return actualizar(
                plantilla.idPlantilla(),
                plantilla.nombre(),
                plantilla.familia(),
                plantilla.idProducto(),
                plantilla.metodoDefault(),
                plantilla.equipoDefault(),
                plantilla.pesoMuestraG(),
                plantilla.superficieCm2(),
                plantilla.usaNumero()
        );
    }

    default long insert(PlantillaRecord plantilla) {
        return insertar(
                plantilla.nombre(),
                plantilla.familia(),
                plantilla.idProducto(),
                plantilla.metodoDefault(),
                plantilla.equipoDefault(),
                plantilla.pesoMuestraG(),
                plantilla.superficieCm2(),
                plantilla.usaNumero()
        );
    }
}
