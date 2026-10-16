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
import tn.esprit.tpfoyer.exception.ResourceNotFoundException;
import tn.esprit.tpfoyer.service.IAgenceService;
import tn.esprit.tpfoyer.service.IClientService;
import tn.esprit.tpfoyer.service.IVehiculeService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class ServiceCrudTests {

    @Autowired
    private IAgenceService agenceService;

    @Autowired
    private IClientService clientService;

    @Autowired
    private IVehiculeService vehiculeService;

    // ------------------------------------------------------------------
    // Partie 2 : AgenceService
    // ------------------------------------------------------------------
    @Test
    void testAgenceService() {
        executer("testAgenceService", () -> {
            int avant = agenceService.findAll().size();

            Agence agence = new Agence();
            agence.setNom("AutoLoc Tunis Centre");
            agence.setVille("Tunis");
            agence.setAdresse("12 avenue Habib Bourguiba");
            agence.setTelephone("71000000");

            Agence saved = agenceService.create(agence);
            log.info("create() -> id={}, nom={}", saved.getIdAgence(), saved.getNom());
            assertNotNull(saved.getIdAgence(), "create() n'a pas généré d'ID pour l'agence");

            List<Agence> agences = agenceService.findAll();
            log.info("findAll() -> {} agence(s)", agences.size());
            assertEquals(avant + 1, agences.size(), "findAll() ne retourne pas le bon nombre d'agences");

            Long id = saved.getIdAgence();
            Agence trouvee = agenceService.findById(id);
            log.info("findById({}) -> {}", id, trouvee.getNom());
            assertNotNull(trouvee, "findById() a retourné null");
            assertEquals("Tunis", trouvee.getVille(), "La ville de l'agence est incorrecte");

            trouvee.setVille("Sfax");
            Agence modifiee = agenceService.update(id, trouvee);
            log.info("update({}) -> ville={}", id, modifiee.getVille());
            assertEquals("Sfax", agenceService.findById(id).getVille(), "La modification n'a pas été persistée");

            log.info("count() -> {}", agenceService.findAll().size());
            assertEquals(avant + 1, agenceService.findAll().size(), "Le nombre d'agences est incorrect");

            agenceService.deleteById(id);
            assertThrows(ResourceNotFoundException.class, () -> agenceService.findById(id),
                    "L'agence " + id + " existe encore après deleteById()");
            assertEquals(avant, agenceService.findAll().size(), "Le nombre d'agences est incorrect après suppression");
            log.info("deleteById({}) -> agence supprimée", id);

            // Règle de gestion : nom obligatoire
            Agence sansNom = new Agence();
            sansNom.setNom("");
            sansNom.setVille("Tunis");
            assertThrows(IllegalArgumentException.class, () -> agenceService.create(sansNom),
                    "Une agence sans nom aurait dû être refusée");
            log.info("create() avec nom vide -> refusé (IllegalArgumentException)");
        });
    }

    // ------------------------------------------------------------------
    // Partie 3 : ClientService
    // ------------------------------------------------------------------
    @Test
    void testClientService() {
        executer("testClientService", () -> {
            int avant = clientService.findAll().size();

            // Valeurs uniques à chaque exécution (évite les erreurs de contrainte unique)
            String suffixe = String.valueOf(System.currentTimeMillis());

            Client c1 = creerClient("Ben Salah", "Amine", "amine." + suffixe + "@mail.tn", "20111222", "PERM-A-" + suffixe);
            Client c2 = creerClient("Trabelsi", "Sarra", "sarra." + suffixe + "@mail.tn", "22333444", "PERM-S-" + suffixe);

            Client saved1 = clientService.create(c1);
            Client saved2 = clientService.create(c2);
            log.info("create() -> ids {} et {}", saved1.getIdClient(), saved2.getIdClient());
            assertNotNull(saved1.getIdClient(), "create() n'a pas généré d'ID pour le client 1");
            assertNotNull(saved2.getIdClient(), "create() n'a pas généré d'ID pour le client 2");

            List<Client> clients = clientService.findAll();
            log.info("findAll() -> {} client(s)", clients.size());
            assertEquals(avant + 2, clients.size(), "findAll() ne retourne pas le bon nombre de clients");

            Client trouve = clientService.findById(saved1.getIdClient());
            log.info("findById({}) -> {} {}", saved1.getIdClient(), trouve.getNom(), trouve.getPrenom());
            assertEquals("Ben Salah", trouve.getNom(), "Le nom du client est incorrect");

            trouve.setTelephone("99887766");
            clientService.update(saved1.getIdClient(), trouve);
            assertEquals("99887766", clientService.findById(saved1.getIdClient()).getTelephone(),
                    "La modification du téléphone n'a pas été persistée");
            log.info("update({}) -> téléphone modifié", saved1.getIdClient());

            log.info("count() -> {}", clientService.findAll().size());
            assertEquals(avant + 2, clientService.findAll().size(), "Le nombre de clients est incorrect");

            // Suppression du client 1 ; le client 2 reste en base pour phpMyAdmin
            Long idSupprime = saved1.getIdClient();
            clientService.deleteById(idSupprime);
            assertThrows(ResourceNotFoundException.class, () -> clientService.findById(idSupprime),
                    "Le client " + idSupprime + " existe encore après deleteById()");
            assertEquals(avant + 1, clientService.findAll().size(), "Le nombre de clients est incorrect après suppression");
            log.info("deleteById({}) -> client supprimé", idSupprime);

            // Règle de gestion : nom obligatoire
            Client sansNom = creerClient("", "Test", "vide." + suffixe + "@mail.tn", "20000000", "PERM-V-" + suffixe);
            assertThrows(IllegalArgumentException.class, () -> clientService.create(sansNom),
                    "Un client sans nom aurait dû être refusé");
            log.info("create() avec nom vide -> refusé (IllegalArgumentException)");
        });
    }

    // ------------------------------------------------------------------
    // Partie 4 : VehiculeService
    // ------------------------------------------------------------------
    @Test
    void testVehiculeService() {
        executer("testVehiculeService", () -> {
            int avant = vehiculeService.findAll().size();

            Vehicule vehicule = new Vehicule();
            vehicule.setImmatriculation("123 TUN " + (1000 + (int) (Math.random() * 8999)));
            vehicule.setMarque("Renault");
            vehicule.setModele("Clio");
            vehicule.setCategorie(CategorieVehicule.CITADINE);
            vehicule.setTarifJournalier(new BigDecimal("60.00"));
            vehicule.setStatut(StatutVehicule.DISPONIBLE);

            Vehicule saved = vehiculeService.create(vehicule);
            Long id = saved.getIdVehicule();
            log.info("create() -> id={}, immatriculation={}", id, saved.getImmatriculation());
            assertNotNull(id, "create() n'a pas généré d'ID pour le véhicule");

            List<Vehicule> vehicules = vehiculeService.findAll();
            log.info("findAll() -> {} véhicule(s)", vehicules.size());
            assertEquals(avant + 1, vehicules.size(), "findAll() ne retourne pas le bon nombre de véhicules");

            Vehicule trouve = vehiculeService.findById(id);
            log.info("findById({}) -> {} {}", id, trouve.getMarque(), trouve.getModele());
            assertEquals("Renault", trouve.getMarque(), "La marque du véhicule est incorrecte");

            trouve.setStatut(StatutVehicule.LOUE);
            Vehicule modifie = vehiculeService.update(id, trouve);
            assertEquals(StatutVehicule.LOUE, modifie.getStatut(), "Le statut retourné par update() n'est pas LOUE");
            assertEquals(StatutVehicule.LOUE, vehiculeService.findById(id).getStatut(),
                    "Le statut LOUE n'a pas été persisté en base");
            log.info("update({}) -> statut={}", id, modifie.getStatut());

            log.info("count() -> {}", vehiculeService.findAll().size());
            assertEquals(avant + 1, vehiculeService.findAll().size(), "Le nombre de véhicules est incorrect");

            vehiculeService.deleteById(id);
            assertThrows(ResourceNotFoundException.class, () -> vehiculeService.findById(id),
                    "Le véhicule " + id + " existe encore après deleteById()");
            assertEquals(avant, vehiculeService.findAll().size(), "Le nombre de véhicules est incorrect après suppression");
            log.info("deleteById({}) -> véhicule supprimé", id);

            // Règle de gestion : tarif négatif refusé
            Vehicule tarifNegatif = new Vehicule();
            tarifNegatif.setImmatriculation("999 TUN 0001");
            tarifNegatif.setMarque("Peugeot");
            tarifNegatif.setModele("208");
            tarifNegatif.setCategorie(CategorieVehicule.CITADINE);
            tarifNegatif.setTarifJournalier(new BigDecimal("-10.00"));
            tarifNegatif.setStatut(StatutVehicule.DISPONIBLE);
            assertThrows(IllegalArgumentException.class, () -> vehiculeService.create(tarifNegatif),
                    "Un tarif journalier négatif aurait dû être refusé");
            log.info("create() avec tarif négatif -> refusé (IllegalArgumentException)");
        });
    }

    /**
     * Exécute le corps d'un test et affiche ✅ si tout va bien,
     * ❌ avec la raison sinon. L'erreur est relancée pour que JUnit marque le test en échec.
     */
    private void executer(String nomTest, Runnable test) {
        try {
            test.run();
            log.info("✅ {} : tout est OK", nomTest);
        } catch (AssertionError | RuntimeException e) {
            log.error("❌ {} : quelque chose ne va pas -> {}", nomTest, e.getMessage());
            throw e;
        }
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