package tn.esprit.tpfoyer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.tpfoyer.domain.Reservation;

public interface IReservationRepository extends JpaRepository<Reservation, Long> {
}
