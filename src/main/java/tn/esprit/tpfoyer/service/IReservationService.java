package tn.esprit.tpfoyer.service;

import tn.esprit.tpfoyer.domain.Client;
import tn.esprit.tpfoyer.domain.Reservation;

import java.util.List;

public interface IReservationService {
    Reservation create(Reservation reservation);
    Reservation findById(Long id);
    List<Reservation> findAll();
    Reservation update(Long id, Reservation reservation);
    void deleteById(Long id);

    // Atelier 6 (Observer)
    Reservation creerReservation(Client client);
    Reservation annulerReservation(Reservation reservation);
}