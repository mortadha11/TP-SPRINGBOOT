package tn.esprit.tpfoyer.service;

import tn.esprit.tpfoyer.domain.Vehicule;

import java.util.List;

public interface IVehiculeService {

    Vehicule create(Vehicule vehicule);            // refuse un id non null

    Vehicule findById(Long id);                    // ResourceNotFoundException si absent

    List<Vehicule> findAll();

    Vehicule update(Long id, Vehicule vehicule);   // charge l'existant, recopie, save

    void deleteById(Long id);                      // ResourceNotFoundException si absent
}