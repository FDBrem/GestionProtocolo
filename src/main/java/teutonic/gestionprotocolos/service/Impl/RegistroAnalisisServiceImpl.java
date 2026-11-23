package teutonic.gestionprotocolos.service.Impl;

import teutonic.gestionprotocolos.database.RepositoryProvider;
import teutonic.gestionprotocolos.persistence.records.AnalisisRecord;
import teutonic.gestionprotocolos.persistence.records.LimiteAnalisisRecord;
import teutonic.gestionprotocolos.persistence.records.ResultadoValidado;
import teutonic.gestionprotocolos.persistence.records.registro.MuestraRequest;
import teutonic.gestionprotocolos.persistence.records.registro.ProtocoloRequest;
import teutonic.gestionprotocolos.persistence.records.registro.RegistroAnalisisRequest;
import teutonic.gestionprotocolos.persistence.records.registro.ResultadoRequest;
import teutonic.gestionprotocolos.repository.AnalisisRepository;
import teutonic.gestionprotocolos.repository.LimiteAnalisisRepository;
import teutonic.gestionprotocolos.repository.MuestraRepository;
import teutonic.gestionprotocolos.repository.ProtocoloRepository;
import teutonic.gestionprotocolos.repository.ResultadoRepository;
import org.jdbi.v3.core.Jdbi;
import teutonic.gestionprotocolos.service.RegistroAnalisisService;
import teutonic.gestionprotocolos.service.ResultadoValidator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistroAnalisisServiceImpl implements RegistroAnalisisService {

    private final ResultadoValidator resultadoValidator;

    public RegistroAnalisisServiceImpl(ResultadoValidator resultadoValidator) {
        this.resultadoValidator = resultadoValidator;
    }
    //voy a comentar porq despues me olvido y se me hace un desastre voler a repasar el flujo
    @Override
    public long registrar(RegistroAnalisisRequest request) {

        //aca validamos los datos generales antes de iniciar la transacción.
        validarRequest(request);

        Jdbi jdbi = RepositoryProvider.getJdbi();

        //toda la operación se ejecuta dentro de una unica transacción
        //si cualquier inserción o validación falla, JDBI hace un rollback
        return jdbi.inTransaction(handle -> {

            //Adjuntamos los repositorios al mismo handle
            //es importante no usar RepositoryProvider.getXRepository()
            //dentro de esta transacción, porque esos repositorios usan onDemand()
            ProtocoloRepository protocoloRepo =
                    handle.attach(ProtocoloRepository.class);

            MuestraRepository muestraRepo =
                    handle.attach(MuestraRepository.class);

            ResultadoRepository resultadoRepo =
                    handle.attach(ResultadoRepository.class);

            AnalisisRepository analisisRepo =
                    handle.attach(AnalisisRepository.class);

            LimiteAnalisisRepository limiteRepo =
                    handle.attach(LimiteAnalisisRepository.class);

            ProtocoloRequest protocolo = request.protocolo();

            //Obtenemos los análisis asociados a la plantilla seleccionada
            //esto permite comprobar que no se intenten registrar análisis
            //que no forman parte del formulario
            List<AnalisisRecord> analisisPlantilla =
                    analisisRepo.buscarAnalisis(protocolo.idPlantilla());

            if (analisisPlantilla.isEmpty()) {
                throw new IllegalStateException(
                        "La plantilla seleccionada no tiene análisis configurados."
                );
            }

            //Creamos un mapa para buscar cada análisis por su ID
            //evitamos recorrer toda la lista por cada resultado
            Map<Long, AnalisisRecord> analisisPorId = new HashMap<>();

            for (AnalisisRecord analisis : analisisPlantilla) {
                analisisPorId.put(analisis.idAnalisis(), analisis);
            }

            //Se genera el siguiente número de protocolo para esta plantilla
            int numeroProtocolo =
                    protocoloRepo.obtenerSiguienteNumero(protocolo.idPlantilla());

            //insertamos el protocolo y recuperamos su ID generado
            long idProtocolo = protocoloRepo.insertar(
                    protocolo.idPlantilla(),
                    numeroProtocolo,
                    protocolo.fecha(),
                    protocolo.idCliente(),
                    protocolo.metodo(),
                    protocolo.equipo(),
                    protocolo.pesoMuestraG(),
                    protocolo.superficieCm2(),
                    protocolo.analista(),
                    protocolo.observaciones()
            );

            //Registramos todas las muestras asociadas al protocolo
            for (MuestraRequest muestra : request.muestras()) {

                //Obtenemos el siguiente correlativo de la serie
                //El correlativo nunca se reinicia
                int correlativo =
                        muestraRepo.obtenerSiguienteCorrelativo(muestra.serieId());

                //El número visible se reinicia cada 1000 muestras:
                //1, 2, ... 1000, 1, 2..., esto es asi porq el laboratorio trabaja asi okk,
                //honestamente creo habria cambiarlo y obligarlos a usar en serie y que quizas se reinicie x comienzo de año
                int numeroMuestra = ((correlativo - 1) % 1000) + 1;

                //insertamos la muestra asociándola al protocolo recién creado
                long idMuestra = muestraRepo.insertar(
                        idProtocolo,
                        muestra.productoId(),
                        muestra.serieId(),
                        correlativo,
                        numeroMuestra,
                        muestra.fechaToma().toString(),
                        muestra.horaToma() != null
                                ? muestra.horaToma().toString()
                                : null,
                        muestra.granjaId(),
                        muestra.lote(),
                        muestra.sectorId(),
                        muestra.elemento(),
                        muestra.bolson(),
                        muestra.temperaturaCarcaza(),
                        muestra.temperaturaProceso(),
                        muestra.temperaturaCongelada(),
                        muestra.temperaturaProducto(),
                        0,
                        null
                );

                //Cada muestra debe tener al menos un resultado,
                //un resultado pendiente también cuenta como resultado si no entendí mal
                for (ResultadoRequest resultado : muestra.resultados()) {

                    //Comprobamos que el análisis pertenezca a la plantilla
                    AnalisisRecord analisis =
                            analisisPorId.get(resultado.idAnalisis());

                    if (analisis == null) {
                        throw new IllegalArgumentException(
                                "El análisis " + resultado.idAnalisis()
                                        + " no pertenece a la plantilla seleccionada."
                        );
                    }

                    //Los análisis de recuento necesitan un límite vigente
                    //para presencia y resultados pendientes no se aplica límite
                    Double limite = null;

                    if ("RECUENTO".equals(analisis.tipoValor())
                            && !resultado.pendiente()) {

                        LimiteAnalisisRecord limiteRecord =
                                limiteRepo.buscarPorAnalisis(analisis.idAnalisis())
                                        .orElseThrow(() ->
                                                new IllegalStateException(
                                                        "No existe un límite configurado para el análisis "
                                                                + analisis.nombre()
                                                )
                                        );

                        limite = limiteRecord.limiteMaximo();
                    }

                    //Delegamos la validación y el cálculo al ResultadoValidator
                    //ahi se determina el valor final, la conformidad y el límite aplicado
                    ResultadoValidado validado = resultadoValidator.validar(
                            resultado,
                            analisis,
                            limite,
                            protocolo.superficieCm2()
                    );

                    //insertamos el resultado ya validado
                    resultadoRepo.insertar(
                            idMuestra,
                            resultado.idAnalisis(),
                            resultado.horaControl() != null
                                    ? resultado.horaControl()
                                    : "",
                            validado.valor(),
                            validado.menorQue() ? 1 : 0,
                            validado.colonias(),
                            validado.presencia(),
                            validado.conforme(),
                            validado.pendiente() ? 1 : 0,
                            validado.observacion(),
                            validado.limiteAplicado()
                    );
                }
            }

            //Aca devolvemos el ID del protocolo.
            //¡Si llegamos hasta acá, significa que, todas las inserciones fueron exitosas!
            return idProtocolo;
        });
    }

    /**
     * validarRequest se usa para validar los datos generales del registro
     * Las validaciones específicas de cada resultado quedan a cargo de ResultadoValidator
     */
    private void validarRequest(RegistroAnalisisRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "El registro de análisis no puede ser null."
            );
        }

        if (request.protocolo() == null) {
            throw new IllegalArgumentException(
                    "El protocolo es obligatorio."
            );
        }

        ProtocoloRequest protocolo = request.protocolo();

        if (protocolo.idPlantilla() <= 0) {
            throw new IllegalArgumentException(
                    "La plantilla seleccionada no es válida."
            );
        }

        if (protocolo.fecha() == null) {
            throw new IllegalArgumentException(
                    "La fecha del protocolo es obligatoria."
            );
        }

        //La superficie es obligatoria y tiene que ser un número positivo y finito
        if (protocolo.superficieCm2() == null
                || !Double.isFinite(protocolo.superficieCm2())
                || protocolo.superficieCm2() <= 0) {

            throw new IllegalArgumentException(
                    "La superficie debe ser un número mayor a cero."
            );
        }

        if (request.muestras() == null || request.muestras().isEmpty()) {
            throw new IllegalArgumentException(
                    "El protocolo debe contener al menos una muestra."
            );
        }

        //Validamos que cada muestra tenga al menos un resultado
        //No validamos acá el contenido de cada resultado:
        //esa responsabilidad es de ResultadoValidator
        for (int i = 0; i < request.muestras().size(); i++) {

            MuestraRequest muestra = request.muestras().get(i);

            if (muestra == null) {
                throw new IllegalArgumentException(
                        "La muestra " + (i + 1) + " no puede ser null."
                );
            }

            if (muestra.serieId() == null || muestra.serieId() <= 0) {
                throw new IllegalArgumentException(
                        "La muestra " + (i + 1) + " debe tener una serie válida."
                );
            }

            if (muestra.fechaToma() == null) {
                throw new IllegalArgumentException(
                        "La muestra " + (i + 1) + " debe tener una fecha de toma."
                );
            }

            if (muestra.resultados() == null || muestra.resultados().isEmpty()) {
                throw new IllegalArgumentException(
                        "La muestra " + (i + 1)
                                + " debe tener al menos un resultado, aunque esté pendiente."
                );
            }
        }
    }
}
