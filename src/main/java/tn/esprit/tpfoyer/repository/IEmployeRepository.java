package tn.esprit.tpfoyer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.tpfoyer.domain.Employe;

public interface IEmployeRepository extends JpaRepository<Employe, Long> {
}
