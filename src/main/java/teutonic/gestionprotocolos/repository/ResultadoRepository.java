package teutonic.gestionprotocolos.repository;

import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import teutonic.gestionprotocolos.persistence.records.ResultadoRecord;

import java.util.List;
import java.util.Optional;

@RegisterConstructorMapper(ResultadoRecord.class)
public interface ResultadoRepository {

    @SqlQuery("""
            SELECT
                id_resultado AS idResultado,
                id_muestra AS idMuestra,
                id_analisis AS idAnalisis,
                hora_control AS horaControl,
                valor,
                menor_que AS menorQue,
                colonias,
                presencia,
                conforme,
                pendiente,
                observacion,
                creado_en AS creadoEn
            FROM resultado
            WHERE id_resultado = :id
            """)
    Optional<ResultadoRecord> buscarPorId(@Bind("id") long id);

    @SqlQuery("""
            SELECT
                id_resultado AS idResultado,
                id_muestra AS idMuestra,
                id_analisis AS idAnalisis,
                hora_control AS horaControl,
                valor,
                menor_que AS menorQue,
                colonias,
                presencia,
                conforme,
                pendiente,
                observacion,
                creado_en AS creadoEn
            FROM resultado
            WHERE id_muestra = :idMuestra
            ORDER BY id_resultado
            """)
    List<ResultadoRecord> buscarPorMuestra(
            @Bind("idMuestra") long idMuestra
    );

    @SqlUpdate("""
            INSERT INTO resultado (
                id_muestra,
                id_analisis,
                hora_control,
                valor,
                menor_que,
                colonias,
                presencia,
                conforme,
                pendiente,
                observacion
            )
            VALUES (
                :idMuestra,
                :idAnalisis,
                :horaControl,
                :valor,
                :menorQue,
                :colonias,
                :presencia,
                :conforme,
                :pendiente,
                :observacion
            )
            """)
    @GetGeneratedKeys("id_resultado")
    long insertar(
            @Bind("idMuestra") long idMuestra,
            @Bind("idAnalisis") long idAnalisis,
            @Bind("horaControl") String horaControl,
            @Bind("valor") Double valor,
            @Bind("menorQue") int menorQue,
            @Bind("colonias") Integer colonias,
            @Bind("presencia") Integer presencia,
            @Bind("conforme") Integer conforme,
            @Bind("pendiente") int pendiente,
            @Bind("observacion") String observacion
    );

    @SqlUpdate("""
            UPDATE resultado
            SET
                hora_control = :horaControl,
                valor = :valor,
                menor_que = :menorQue,
                colonias = :colonias,
                presencia = :presencia,
                conforme = :conforme,
                pendiente = :pendiente,
                observacion = :observacion
            WHERE id_resultado = :id
            """)
    int actualizar(
            @Bind("id") long id,
            @Bind("horaControl") String horaControl,
            @Bind("valor") Double valor,
            @Bind("menorQue") int menorQue,
            @Bind("colonias") Integer colonias,
            @Bind("presencia") Integer presencia,
            @Bind("conforme") Integer conforme,
            @Bind("pendiente") int pendiente,
            @Bind("observacion") String observacion
    );
}