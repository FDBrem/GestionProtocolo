package teutonic.gestionprotocolos.repository;

import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import teutonic.gestionprotocolos.persistence.records.MuestraRecord;

import java.util.List;
import java.util.Optional;

@RegisterConstructorMapper(MuestraRecord.class)
public interface MuestraRepository {

    @SqlQuery("""
            SELECT
                id_muestra AS idMuestra,
                id_protocolo AS idProtocolo,
                id_producto AS idProducto,
                id_serie AS idSerie,
                numero_correlativo AS numeroCorrelativo,
                numero_muestra AS numeroMuestra,
                fecha_toma AS fechaToma,
                hora_toma AS horaToma,
                id_granja AS idGranja,
                lote,
                id_sector AS idSector,
                elemento,
                bolson,
                t_carcaza_c AS temperaturaCarcaza,
                t_proceso_c AS temperaturaProceso,
                t_congelada_c AS temperaturaCongelada,
                t_producto_c AS temperaturaProducto,
                anulado,
                anulado_motivo AS anuladoMotivo,
                creado_en AS creadoEn
            FROM muestra
            WHERE id_muestra = :id
            """)
    Optional<MuestraRecord> buscarPorId(@Bind("id") long id);

    @SqlQuery("""
            SELECT
                id_muestra AS idMuestra,
                id_protocolo AS idProtocolo,
                id_producto AS idProducto,
                id_serie AS idSerie,
                numero_correlativo AS numeroCorrelativo,
                numero_muestra AS numeroMuestra,
                fecha_toma AS fechaToma,
                hora_toma AS horaToma,
                id_granja AS idGranja,
                lote,
                id_sector AS idSector,
                elemento,
                bolson,
                t_carcaza_c AS temperaturaCarcaza,
                t_proceso_c AS temperaturaProceso,
                t_congelada_c AS temperaturaCongelada,
                t_producto_c AS temperaturaProducto,
                anulado,
                anulado_motivo AS anuladoMotivo,
                creado_en AS creadoEn
            FROM muestra
            WHERE anulado = 0
            ORDER BY fecha_toma DESC, id_muestra DESC
            """)
    List<MuestraRecord> listarActivas();

    @SqlQuery("""
            SELECT
                id_muestra AS idMuestra,
                id_protocolo AS idProtocolo,
                id_producto AS idProducto,
                id_serie AS idSerie,
                numero_correlativo AS numeroCorrelativo,
                numero_muestra AS numeroMuestra,
                fecha_toma AS fechaToma,
                hora_toma AS horaToma,
                id_granja AS idGranja,
                lote,
                id_sector AS idSector,
                elemento,
                bolson,
                t_carcaza_c AS temperaturaCarcaza,
                t_proceso_c AS temperaturaProceso,
                t_congelada_c AS temperaturaCongelada,
                t_producto_c AS temperaturaProducto,
                anulado,
                anulado_motivo AS anuladoMotivo,
                creado_en AS creadoEn
            FROM muestra
            ORDER BY fecha_toma DESC, id_muestra DESC
            """)
    List<MuestraRecord> listarTodas();

    @SqlQuery("""
        INSERT INTO contador_muestra (id_serie, ultimo_numero)
        VALUES (:idSerie, 1)
        ON CONFLICT (id_serie)
        DO UPDATE SET ultimo_numero = ultimo_numero + 1
        RETURNING ultimo_numero
        """)
    int obtenerSiguienteCorrelativo(
            @Bind("idSerie") long idSerie
    );

    @SqlQuery("""
            SELECT
                id_muestra AS idMuestra,
                id_protocolo AS idProtocolo,
                id_producto AS idProducto,
                id_serie AS idSerie,
                numero_correlativo AS numeroCorrelativo,
                numero_muestra AS numeroMuestra,
                fecha_toma AS fechaToma,
                hora_toma AS horaToma,
                id_granja AS idGranja,
                lote,
                id_sector AS idSector,
                elemento,
                bolson,
                t_carcaza_c AS temperaturaCarcaza,
                t_proceso_c AS temperaturaProceso,
                t_congelada_c AS temperaturaCongelada,
                t_producto_c AS temperaturaProducto,
                anulado,
                anulado_motivo AS anuladoMotivo,
                creado_en AS creadoEn
            FROM muestra
            WHERE id_protocolo = :idProtocolo
            ORDER BY id_muestra
            """)
    List<MuestraRecord> buscarPorProtocolo(@Bind("idProtocolo") long idProtocolo);

    @SqlUpdate("""
            INSERT INTO muestra (
                id_protocolo,
                id_producto,
                id_serie,
                numero_correlativo,
                numero_muestra,
                fecha_toma,
                hora_toma,
                id_granja,
                lote,
                id_sector,
                elemento,
                bolson,
                t_carcaza_c,
                t_proceso_c,
                t_congelada_c,
                t_producto_c,
                anulado,
                anulado_motivo
            ) VALUES (
                :idProtocolo,
                :idProducto,
                :idSerie,
                :numeroCorrelativo,
                :numeroMuestra,
                :fechaToma,
                :horaToma,
                :idGranja,
                :lote,
                :idSector,
                :elemento,
                :bolson,
                :temperaturaCarcaza,
                :temperaturaProceso,
                :temperaturaCongelada,
                :temperaturaProducto,
                :anulado,
                :anuladoMotivo
            )
            """)
    @GetGeneratedKeys("id_muestra")
    long insertar(
            @Bind("idProtocolo") Long idProtocolo,
            @Bind("idProducto") Long idProducto,
            @Bind("idSerie") Long idSerie,
            @Bind("numeroCorrelativo") Integer numeroCorrelativo,
            @Bind("numeroMuestra") Integer numeroMuestra,
            @Bind("fechaToma") String fechaToma,
            @Bind("horaToma") String horaToma,
            @Bind("idGranja") Long idGranja,
            @Bind("lote") String lote,
            @Bind("idSector") Long idSector,
            @Bind("elemento") String elemento,
            @Bind("bolson") String bolson,
            @Bind("temperaturaCarcaza") Double temperaturaCarcaza,
            @Bind("temperaturaProceso") Double temperaturaProceso,
            @Bind("temperaturaCongelada") Double temperaturaCongelada,
            @Bind("temperaturaProducto") Double temperaturaProducto,
            @Bind("anulado") Integer anulado,
            @Bind("anuladoMotivo") String anuladoMotivo
    );

    default long insertar(MuestraRecord muestra) {
        return insertar(
                muestra.idProtocolo(),
                muestra.idProducto(),
                muestra.idSerie(),
                muestra.numeroCorrelativo(),
                muestra.numeroMuestra(),
                muestra.fechaToma(),
                muestra.horaToma(),
                muestra.idGranja(),
                muestra.lote(),
                muestra.idSector(),
                muestra.elemento(),
                muestra.bolson(),
                muestra.temperaturaCarcaza(),
                muestra.temperaturaProceso(),
                muestra.temperaturaCongelada(),
                muestra.temperaturaProducto(),
                muestra.anulado(),
                muestra.anuladoMotivo()
        );
    }

    @SqlUpdate("""
            UPDATE muestra
            SET
                id_protocolo = :idProtocolo,
                id_producto = :idProducto,
                fecha_toma = :fechaToma,
                hora_toma = :horaToma,
                id_granja = :idGranja,
                lote = :lote,
                id_sector = :idSector,
                elemento = :elemento,
                bolson = :bolson,
                t_carcaza_c = :temperaturaCarcaza,
                t_proceso_c = :temperaturaProceso,
                t_congelada_c = :temperaturaCongelada,
                t_producto_c = :temperaturaProducto,
                anulado = :anulado,
                anulado_motivo = :anuladoMotivo
            WHERE id_muestra = :id
            """)
    int actualizar(
            @Bind("id") long id,
            @Bind("idProtocolo") Long idProtocolo,
            @Bind("idProducto") Long idProducto,
            @Bind("fechaToma") String fechaToma,
            @Bind("horaToma") String horaToma,
            @Bind("idGranja") Long idGranja,
            @Bind("lote") String lote,
            @Bind("idSector") Long idSector,
            @Bind("elemento") String elemento,
            @Bind("bolson") String bolson,
            @Bind("temperaturaCarcaza") Double temperaturaCarcaza,
            @Bind("temperaturaProceso") Double temperaturaProceso,
            @Bind("temperaturaCongelada") Double temperaturaCongelada,
            @Bind("temperaturaProducto") Double temperaturaProducto,
            @Bind("anulado") Integer anulado,
            @Bind("anuladoMotivo") String anuladoMotivo
    );

    default int actualizar(MuestraRecord muestra) {
        return actualizar(
                muestra.idMuestra(),
                muestra.idProtocolo(),
                muestra.idProducto(),
                muestra.fechaToma(),
                muestra.horaToma(),
                muestra.idGranja(),
                muestra.lote(),
                muestra.idSector(),
                muestra.elemento(),
                muestra.bolson(),
                muestra.temperaturaCarcaza(),
                muestra.temperaturaProceso(),
                muestra.temperaturaCongelada(),
                muestra.temperaturaProducto(),
                muestra.anulado(),
                muestra.anuladoMotivo()
        );
    }

    @SqlUpdate("""
            UPDATE muestra
            SET anulado = 1,
                anulado_motivo = :motivo
            WHERE id_muestra = :id
            """)
    int anular(
            @Bind("id") long id,
            @Bind("motivo") String motivo
    );

    @SqlUpdate("""
            DELETE FROM muestra
            WHERE id_muestra = :id
            """)
    int eliminar(@Bind("id") long id);

    // Métodos de conveniencia compatibles
    default Optional<MuestraRecord> findById(long id) {
        return buscarPorId(id);
    }

    default List<MuestraRecord> findAll() {
        return listarTodas();
    }

    default int delete(long id) {
        return eliminar(id);
    }
}