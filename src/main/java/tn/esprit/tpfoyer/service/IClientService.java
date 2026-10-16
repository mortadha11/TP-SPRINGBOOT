package tn.esprit.tpfoyer.service;

import tn.esprit.tpfoyer.domain.Client;

import java.util.List;

public interface IClientService {

    Client create(Client client);              // refuse un id non null

    Client findById(Long id);                  // ResourceNotFoundException si absent

    List<Client> findAll();

    Client update(Long id, Client client);     // charge l'existant, recopie, save

    void deleteById(Long id);                  // ResourceNotFoundException si absent
}