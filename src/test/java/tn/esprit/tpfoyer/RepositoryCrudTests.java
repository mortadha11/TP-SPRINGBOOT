package tn.esprit.tpfoyer;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tn.esprit.tpfoyer.domain.Agence;
import tn.esprit.tpfoyer.domain.Client;
import tn.esprit.tpfoyer.domain.Vehicule;
import tn.esprit.tpfoyer.domain.enums.CategorieVehicule;
import tn.esprit.tpfoyer.domain.enums.StatutVehicule;
import tn.esprit.tpfoyer.repository.IAgenceRepository;
import tn.esprit.tpfoyer.repository.IClientRepository;
import tn.esprit.tpfoyer.repository.IVehiculeRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class RepositoryCrudTests {

    @Autowired
    private IAgenceRepository agenceRepository;

    @Autowired
    private IClientRepository clientRepository;

    @Autowired
    private IVehiculeRepository vehiculeRepository;

    @Test
    void testAgenceRepository() {
        long avant = agenceRepository.count();

        Agence agence = new Agence();
        agence.setNom("AutoLoc Tunis Centre");
        agence.setVille("Tunis");
        agence.setAdresse("12 avenue Habib Bourguiba");
        agence.setTelephone("71000000");

        Agence saved = agenceRepository.save(agence);
        log.info("save() -> id={}, nom={}", saved.getIdAgence(), saved.getNom());
        assertNotNull(saved.getIdAgence());

        List<Agence> agences = agenceRepository.findAll();
        log.info("findAll() -> {} agence(s)", agences.size());
        assertEquals(avant + 1, agences.size());

        Long id = saved.getIdAgence();
        Optional<Agence> trouvee = agenceRepository.findById(id);
        log.info("findById({}) -> present={}", id, trouvee.isPresent());
        assertTrue(trouvee.isPresent());
        assertEquals("Tunis", trouvee.get().getVille());

        assertTrue(agenceRepository.existsById(id));
        log.info("count() -> {}", agenceRepository.count());
        assertEquals(avant + 1, agenceRepository.count());

        agenceRepository.deleteById(id);
        assertFalse(agenceRepository.existsById(id));
        assertEquals(avant, agenceRepository.count());
        log.info("deleteById({}) -> agence supprimée", id);
    }

    @Test
    void testClientRepository() {
        long avant = clientRepository.count();

        Client c1 = creerClient("Ben Salah", "Amine", "amine.bensalah@mail.tn", "20111222", "TN-PERM-001");
        Client c2 = creerClient("Trabelsi", "Sarra", "sarra.trabelsi@mail.tn", "22333444", "TN-PERM-002");

        Client saved1 = clientRepository.save(c1);
        Client saved2 = clientRepository.save(c2);
        log.info("save() -> ids {} et {}", saved1.getIdClient(), saved2.getIdClient());
        assertNotNull(saved1.getIdClient());
        assertNotNull(saved2.getIdClient());

        List<Client> clients = clientRepository.findAll();
        log.info("findAll() -> {} client(s)", clients.size());
        assertEquals(avant + 2, clients.size());

        Optional<Client> trouve = clientRepository.findById(saved1.getIdClient());
        assertTrue(trouve.isPresent());
        assertEquals("Ben Salah", trouve.get().getNom());

        Long idInexistant = saved2.getIdClient() + 1_000_000L;
        assertTrue(clientRepository.existsById(saved1.getIdClient()));
        assertFalse(clientRepository.existsById(idInexistant));
        log.info("existsById({}) -> true, existsById({}) -> false", saved1.getIdClient(), idInexistant);

        assertEquals(avant + 2, clientRepository.count());
        log.info("count() -> {}", clientRepository.count());
        // Les clients restent en base pour les voir dans phpMyAdmin.
    }

    @Test
    void testVehiculeRepository() {
        long avant = vehiculeRepository.count();

        Vehicule vehicule = new Vehicule();
        vehicule.setImmatriculation("123 TUN 4567");
        vehicule.setMarque("Renault");
        vehicule.setModele("Clio");
        vehicule.setCategorie(CategorieVehicule.CITADINE);
        vehicule.setTarifJournalier(new BigDecimal("60.00"));
        vehicule.setStatut(StatutVehicule.DISPONIBLE);

        Vehicule saved = vehiculeRepository.save(vehicule);
        Long id = saved.getIdVehicule();
        log.info("save() -> id={}", id);
        assertNotNull(id);

        log.info("findAll() -> {} véhicule(s)", vehiculeRepository.findAll().size());
        assertEquals(avant + 1, vehiculeRepository.count());

        saved.setStatut(StatutVehicule.LOUE);
        Vehicule modifie = vehiculeRepository.save(saved);
        assertEquals(StatutVehicule.LOUE, modifie.getStatut());
        assertEquals(StatutVehicule.LOUE, vehiculeRepository.findById(id).orElseThrow().getStatut());
        log.info("save() après modification -> statut={}", modifie.getStatut());

        vehiculeRepository.deleteById(id);
        assertFalse(vehiculeRepository.existsById(id));
        assertEquals(avant, vehiculeRepository.count());
        log.info("deleteById({}) -> véhicule supprimé", id);
    }

    private Client creerClient(String nom, String prenom, String email, String telephone, String numPermis) {
        Client client = new Client();
        client.setNom(nom);
        client.setPrenom(prenom);
        client.setEmail(email);
        client.setTelephone(telephone);
        client.setNumPermis(numPermis);
        client.setDateInscription(LocalDate.now());
        return client;
    }
}