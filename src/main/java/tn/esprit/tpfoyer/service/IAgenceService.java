package tn.esprit.tpfoyer.service;

import tn.esprit.tpfoyer.domain.Agence;

import java.util.List;

public interface IAgenceService {

    Agence create(Agence agence);              // refuse un id non null

    Agence findById(Long id);                  // ResourceNotFoundException si absent

    List<Agence> findAll();

    Agence update(Long id, Agence agence);     // charge l'existant, recopie, save

    void deleteById(Long id);                  // ResourceNotFoundException si absent
}