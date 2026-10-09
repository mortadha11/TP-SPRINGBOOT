package tn.esprit.tpfoyer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.tpfoyer.domain.Equipement;

public interface IEquipementRepository extends JpaRepository<Equipement, Long> {
}
