package tn.esprit.tpfoyer;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.tpfoyer.domain.Agence;
import tn.esprit.tpfoyer.domain.Contrat;
import tn.esprit.tpfoyer.domain.Employe;
import tn.esprit.tpfoyer.domain.Equipement;
import tn.esprit.tpfoyer.domain.Paiement;
import tn.esprit.tpfoyer.domain.enums.ModePaiement;
import tn.esprit.tpfoyer.domain.enums.RoleEmploye;
import tn.esprit.tpfoyer.repository.IAgenceRepository;
import tn.esprit.tpfoyer.repository.IContratRepository;
import tn.esprit.tpfoyer.repository.IEmployeRepository;
import tn.esprit.tpfoyer.repository.IEquipementRepository;
import tn.esprit.tpfoyer.repository.IPaiementRepository;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class RepositoryExercicesTests {

    @Autowired
    private IEquipementRepository equipementRepository;

    @Autowired
    private IEmployeRepository employeRepository;

    @Autowired
    private IAgenceRepository agenceRepository;

    @Autowired
    private IContratRepository contratRepository;

    @Autowired
    private IPaiementRepository paiementRepository;

    @Test
    void testEquipementRepository() {
        long avant = equipementRepository.count();

        Equipement equipement = new Equipement();
        equipement.setLibelle("GPS");
        Equipement saved = equipementRepository.save(equipement);
        Long id = saved.getIdEquipement();
        assertNotNull(id);
        log.info("save() -> id={}, libelle={}", id, saved.getLibelle());

        assertEquals(avant + 1, equipementRepository.findAll().size());
        assertEquals("GPS", equipementRepository.findById(id).orElseThrow().getLibelle());
        assertTrue(equipementRepository.existsById(id));

        equipementRepository.deleteById(id);
        assertFalse(equipementRepository.existsById(id));
        log.info("deleteById({}) -> équipement supprimé", id);
    }

    @Test
    void testEmployeRepository() {
        Agence agence = new Agence();
        agence.setNom("AutoLoc Sousse");
        agence.setVille("Sousse");
        Agence agenceSauvee = agenceRepository.save(agence);

        Employe employe = new Employe();
        employe.setNom("Gharbi");
        employe.setPrenom("Yassine");
        employe.setRole(RoleEmploye.MANAGER);
        employe.setAgence(agenceSauvee);

        Employe saved = employeRepository.save(employe);
        Long id = saved.getIdEmploye();
        assertNotNull(id);
        log.info("save() -> id={}, role={}", id, saved.getRole());

        Employe relu = employeRepository.findById(id).orElseThrow();
        assertEquals(RoleEmploye.MANAGER, relu.getRole());
        assertEquals(agenceSauvee.getIdAgence(), relu.getAgence().getIdAgence());
        assertTrue(employeRepository.existsById(id));

        employeRepository.deleteById(id);
        assertFalse(employeRepository.existsById(id));
        agenceRepository.deleteById(agenceSauvee.getIdAgence());
    }

    /**
     * Fil rouge Contrat <-> Paiement : cascade PERSIST, orphanRemoval, cascade REMOVE.
     * @Transactional : nécessaire pour lire la liste des paiements (chargement paresseux).
     */
    @Test
    @Transactional
    void testContratPaiementCascade() {
        long paiementsAvant = paiementRepository.count();

        Contrat contrat = new Contrat();
        contrat.setDateSignature(LocalDate.now());
        contrat.setMontantTotal(new BigDecimal("300.00"));
        contrat.setValide(true);
        contrat.addPaiement(creerPaiement("100.00", ModePaiement.CARTE));
        contrat.addPaiement(creerPaiement("200.00", ModePaiement.ESPECES));

        // 1) cascade PERSIST
        Contrat saved = contratRepository.saveAndFlush(contrat);
        assertEquals(paiementsAvant + 2, paiementRepository.count());
        log.info("saveAndFlush(contrat) -> id={}, 2 paiements insérés", saved.getIdContrat());

        // 2) orphanRemoval
        Paiement premier = saved.getPaiements().get(0);
        Long idPremier = premier.getIdPaiement();
        saved.removePaiement(premier);
        contratRepository.saveAndFlush(saved);
        assertFalse(paiementRepository.existsById(idPremier));
        assertEquals(paiementsAvant + 1, paiementRepository.count());
        log.info("orphanRemoval -> paiement {} supprimé", idPremier);

        // 3) cascade REMOVE
        Long idContrat = saved.getIdContrat();
        contratRepository.deleteById(idContrat);
        contratRepository.flush();
        assertFalse(contratRepository.existsById(idContrat));
        assertEquals(paiementsAvant, paiementRepository.count());
        log.info("deleteById({}) -> contrat et paiements supprimés", idContrat);
    }

    private Paiement creerPaiement(String montant, ModePaiement mode) {
        Paiement paiement = new Paiement();
        paiement.setMontant(new BigDecimal(montant));
        paiement.setDatePaiement(LocalDate.now());
        paiement.setModePaiement(mode);
        return paiement;
    }
}