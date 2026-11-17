package teutonic.gestionprotocolos.service;

import teutonic.gestionprotocolos.persistence.records.registro.RegistroAnalisisRequest;

public interface RegistroAnalisisService {

    long registrar(RegistroAnalisisRequest request);

}