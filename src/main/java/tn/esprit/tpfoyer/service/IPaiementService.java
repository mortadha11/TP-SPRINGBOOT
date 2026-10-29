package tn.esprit.tpfoyer.service;

import tn.esprit.tpfoyer.domain.Paiement;

import java.util.List;

public interface IPaiementService {
    Paiement findById(Long id);
    List<Paiement> findAll();
}