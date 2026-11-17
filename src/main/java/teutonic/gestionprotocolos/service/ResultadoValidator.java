package teutonic.gestionprotocolos.service;


import teutonic.gestionprotocolos.persistence.records.AnalisisRecord;
import teutonic.gestionprotocolos.persistence.records.ResultadoValidado;
import teutonic.gestionprotocolos.persistence.records.registro.ResultadoRequest;

public class ResultadoValidator {

    public ResultadoValidado validar(
            ResultadoRequest request,
            AnalisisRecord analisis,
            Double limite,
            Double superficieCm2
    ) {
        if (request == null || analisis == null) {
            throw new IllegalArgumentException(
                    "El resultado y el análisis son obligatorios"
            );
        }

        // Los resultados pendientes no necesitan cálculos.
        if (request.pendiente()) {
            validarPendiente(request);

            return new ResultadoValidado(
                    null,
                    false,
                    null,
                    null,
                    null,
                    true,
                    request.observacion(),
                    null
            );
        }

        return switch (analisis.tipoValor()) {
            case "RECUENTO" -> validarRecuento(
                    request, limite, superficieCm2
            );
            case "PRESENCIA" -> validarPresencia(request);
            default -> throw new IllegalArgumentException(
                    "Tipo de análisis desconocido: " + analisis.tipoValor()
            );
        };
    }

    private ResultadoValidado validarRecuento(
            ResultadoRequest request,
            Double limite,
            Double superficieCm2
    ) {
        validarPositivo(limite, "El límite del análisis");
        validarPositivo(superficieCm2, "La superficie del protocolo");

        if (request.presencia() != null) {
            throw new IllegalArgumentException(
                    "Un resultado de recuento no puede tener presencia/ausencia"
            );
        }

        if (request.menorQue()) {
            validarPositivo(request.valor(), "El umbral del resultado");

            Integer conforme = request.valor() <= limite ? 1 : null;

            return new ResultadoValidado(
                    request.valor(),
                    true,
                    request.colonias(),
                    null,
                    conforme,
                    false,
                    request.observacion(),
                    limite
            );
        }

        if (request.colonias() == null || request.colonias() < 0) {
            throw new IllegalArgumentException(
                    "El recuento debe tener una cantidad de colonias válida"
            );
        }

        double valor = request.colonias() / superficieCm2;

        if (!Double.isFinite(valor)) {
            throw new IllegalArgumentException(
                    "El valor calculado del resultado no es válido"
            );
        }

        Integer conforme = valor < limite ? 1 : 0;

        return new ResultadoValidado(
                valor,
                false,
                request.colonias(),
                null,
                conforme,
                false,
                request.observacion(),
                limite
        );
    }

    private ResultadoValidado validarPresencia(ResultadoRequest request) {
        if (request.presencia() == null ||
                (request.presencia() != 0 && request.presencia() != 1)) {
            throw new IllegalArgumentException(
                    "El resultado de presencia debe ser 0 o 1"
            );
        }

        if (request.valor() != null ||
                request.colonias() != null ||
                request.menorQue()) {
            throw new IllegalArgumentException(
                    "Un resultado de presencia no puede tener valores de recuento"
            );
        }

        return new ResultadoValidado(
                null,
                false,
                null,
                request.presencia(),
                request.presencia() == 0 ? 1 : 0,
                false,
                request.observacion(),
                null
        );
    }

    private void validarPendiente(ResultadoRequest request) {
        if (request.valor() != null ||
                request.colonias() != null ||
                request.presencia() != null ||
                request.menorQue()) {
            throw new IllegalArgumentException(
                    "Un resultado pendiente no puede tener valores registrados"
            );
        }
    }

    private void validarPositivo(Double valor, String campo) {
        if (valor == null || !Double.isFinite(valor) || valor <= 0) {
            throw new IllegalArgumentException(
                    campo + " debe ser un número mayor que cero"
            );
        }
    }
}