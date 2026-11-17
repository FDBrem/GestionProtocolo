package teutonic.gestionprotocolos.repository;

import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import teutonic.gestionprotocolos.persistence.records.AnalisisRecord;

import java.util.List;
import java.util.Optional;

@RegisterConstructorMapper(AnalisisRecord.class)
public interface AnalisisRepository {

    @SqlQuery("""
            SELECT
                id_analisis AS idAnalisis,
                nombre,
                tipo_valor AS tipoValor,
                unidad
            FROM analisis
            WHERE id_analisis = :id
            """)
    Optional<AnalisisRecord> buscarPorId(@Bind("id") long id);

    @SqlQuery("""
            SELECT
                id_analisis AS idAnalisis,
                nombre,
                tipo_valor AS tipoValor,
                unidad
            FROM analisis
            ORDER BY nombre
            """)
    List<AnalisisRecord> listarTodas();

    //este es para obtener los analiis que pertenecen a una plantilla
    @SqlQuery("""
            SELECT
                a.id_analisis AS idAnalisis,
                a.nombre,
                a.tipo_valor AS tipoValor,
                a.unidad
            FROM analisis a
            INNER JOIN plantilla_analisis pa
                ON pa.id_analisis = a.id_analisis
            WHERE pa.id_plantilla = :idPlantilla
            ORDER BY pa.orden
            """)
    List<AnalisisRecord> buscarAnalisis(
            @Bind("idPlantilla") long idPlantilla
    );

    @SqlUpdate("""
            INSERT INTO analisis (nombre, tipo_valor, unidad)
            VALUES (:nombre, :tipoValor, :unidad)
            """)
    @GetGeneratedKeys("id_analisis")
    long insertar(
            @Bind("nombre") String nombre,
            @Bind("tipoValor") String tipoValor,
            @Bind("unidad") String unidad
    );

    @SqlUpdate("""
            INSERT INTO analisis (id_analisis, nombre, tipo_valor, unidad)
            VALUES (:id, :nombre, :tipoValor, :unidad)
            """)
    int insertRaw(
            @Bind("id") long id,
            @Bind("nombre") String nombre,
            @Bind("tipoValor") String tipoValor,
            @Bind("unidad") String unidad
    );

    @SqlUpdate("""
            UPDATE analisis
            SET nombre = :nombre,
                tipo_valor = :tipoValor,
                unidad = :unidad
            WHERE id_analisis = :id
            """)
    int actualizar(
            @Bind("id") long id,
            @Bind("nombre") String nombre,
            @Bind("tipoValor") String tipoValor,
            @Bind("unidad") String unidad
    );

    @SqlUpdate("""
            DELETE FROM analisis WHERE id_analisis = :id
            """)
    int eliminar(@Bind("id") long id);

    // Métodos de conveniencia compatibles
    default Optional<AnalisisRecord> findById(long id) {
        return buscarPorId(id);
    }

    default List<AnalisisRecord> findAll() {
        return listarTodas();
    }

    default int delete(long id) {
        return eliminar(id);
    }

    default int update(AnalisisRecord analisis) {
        return actualizar(
                analisis.idAnalisis(),
                analisis.nombre(),
                analisis.tipoValor(),
                analisis.unidad()
        );
    }

    default long insert(AnalisisRecord analisis) {
        return insertar(
                analisis.nombre(),
                analisis.tipoValor(),
                analisis.unidad()
        );
    }
}
