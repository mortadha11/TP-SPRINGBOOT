package tn.esprit.tpfoyer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.tpfoyer.domain.Agence;

public interface IAgenceRepository extends JpaRepository<Agence, Long> {
}
