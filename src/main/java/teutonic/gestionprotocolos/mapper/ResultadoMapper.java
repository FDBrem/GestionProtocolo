package teutonic.gestionprotocolos.mapper;

import teutonic.gestionprotocolos.model.Resultado;
import teutonic.gestionprotocolos.persistence.records.ResultadoRecord;

public final class ResultadoMapper {

    private ResultadoMapper() {}

    public static Resultado toModel(ResultadoRecord record) {
        if (record == null) {
            return null;
        }

        if (record.idResultado() == null
                || record.idMuestra() == null
                || record.idAnalisis() == null
                || record.menorQue() == null
                || record.pendiente() == null) {
            throw new IllegalArgumentException(
                    "Faltan campos obligatorios para construir Resultado"
            );
        }

        return new Resultado(
                record.idResultado(),
                record.idMuestra(),
                record.idAnalisis(),
                record.horaControl(),
                record.valor(),
                record.menorQue(),
                record.colonias(),
                record.presencia(),
                record.conforme(),
                record.pendiente(),
                record.observacion(),
                record.creadoEn()
        );
    }

    public static ResultadoRecord toRecord(Resultado resultado) {
        if (resultado == null) {
            return null;
        }

        return new ResultadoRecord(
                resultado.idResultado(),
                resultado.idMuestra(),
                resultado.idAnalisis(),
                resultado.horaControl(),
                resultado.valor(),
                resultado.menorQue(),
                resultado.colonias(),
                resultado.presencia(),
                resultado.conforme(),
                resultado.pendiente(),
                resultado.observacion(),
                resultado.creadoEn()
        );
    }
}
