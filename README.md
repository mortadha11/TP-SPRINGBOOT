# AutoLoc — API de gestion de location de véhicules

## Présentation

AutoLoc est une API REST Java conçue pour gérer les activités d’une agence de location automobile. Elle couvre les agences, les employés, les clients, les véhicules et leurs équipements, les réservations, les contrats, les paiements et les maintenances. Le dépôt contient le backend; aucune interface web ou mobile n’est fournie.

## Fonctionnalités

- Opérations CRUD sur les agences, clients, employés, véhicules, équipements, maintenances, réservations et contrats.
- Consultation des paiements (liste et détail); leur création passe par un contrat.
- Persistance MySQL avec Spring Data JPA et Hibernate.
- Documentation de l’API par OpenAPI et Swagger UI.
- Gestion centralisée de certaines erreurs métier en JSON.

## Technologies

- Java 17 et Spring Boot 4.1.1.
- Spring Web MVC pour les routes HTTP et les réponses JSON.
- Spring Data JPA et Hibernate pour la persistance.
- MySQL Connector/J comme pilote de base de données.
- Spring Security pour la chaîne de sécurité configurée.
- springdoc OpenAPI 3.0.0 pour la documentation interactive.
- Lombok pour réduire le code répétitif.
- Maven et Maven Wrapper pour la compilation.

## Organisation

- src/main/java/tn/esprit/tpfoyer/config : configurations OpenAPI et sécurité.
- domain : entités JPA et énumérations métier.
- exception : exception indiquant qu’une ressource est introuvable.
- repository : interfaces d’accès aux données Spring Data.
- service : interfaces métier et leurs implémentations dans service/impl.
- web/controller : contrôleurs REST et gestion globale des erreurs.
- TpFoyerApplication.java : point d’entrée Spring Boot.
- src/main/resources/application.properties : configuration de l’application et de MySQL.
- src/test/java : tests de démarrage, repositories et services.
- pom.xml : dépendances et configuration Maven.
- mvnw et mvnw.cmd : Maven Wrapper pour Linux/macOS et Windows.

Une requête arrive au contrôleur, qui délègue au service. Le service applique les vérifications métier puis utilise un repository pour lire ou enregistrer les entités dans MySQL. Les contrôleurs convertissent les données JSON en objets du domaine et retournent les résultats au format JSON.

## Modèle métier

| Élément | Description |
|---|---|
| Agence | Nom, ville, adresse et téléphone. Une agence est associée à plusieurs employés et véhicules. |
| Employé | Nom, prénom, rôle et agence d’affectation. |
| Client | Coordonnées, numéro de permis et date d’inscription; un client peut avoir plusieurs réservations. |
| Véhicule | Immatriculation, marque, modèle, catégorie, tarif journalier et statut. Rattaché à une agence; associé à des équipements, réservations et maintenances. |
| Équipement | Libellé; relation plusieurs-à-plusieurs avec les véhicules. |
| Réservation | Dates, statut, client, véhicule et contrat éventuel. |
| Contrat | Date de signature, montant total, validité et paiements associés. |
| Paiement | Montant, date et mode de paiement; rattaché à un contrat. |
| Maintenance | Dates, description et véhicule concerné. |

Les identifiants sont générés automatiquement. Les associations inverses sont filtrées ou encadrées avec des annotations Jackson pour éviter des références JSON circulaires.

### Valeurs possibles

- Catégorie de véhicule : CITADINE, BERLINE, SUV, UTILITAIRE.
- Statut de véhicule : DISPONIBLE, LOUE, MAINTENANCE.
- Statut de réservation : EN_ATTENTE, CONFIRMEE, ANNULEE, TERMINEE.
- Mode de paiement : CARTE, ESPECES, VIREMENT.
- Rôle d’employé : AGENT, MANAGER.

Les valeurs doivent respecter exactement la casse et l’orthographe des énumérations dans les requêtes JSON.

## Pré-requis et base de données

Il faut un JDK 17 et un serveur MySQL démarré. Maven peut être utilisé directement, ou remplacé par le Maven Wrapper inclus.

Par défaut, la connexion pointe vers localhost:3306, base tpfoyerdb, utilisateur root et mot de passe vide. L’URL demande au connecteur de créer la base si elle n’existe pas, sous réserve des permissions MySQL. Modifiez les propriétés dans src/main/resources/application.properties si votre environnement utilise d’autres paramètres.

Hibernate utilise la stratégie update pour synchroniser le schéma. Les requêtes SQL sont affichées dans les journaux.

## Installation et lancement

Depuis la racine du projet, démarrez MySQL puis exécutez :

### Windows

    .mvnw.cmd clean package
    .mvnw.cmd spring-boot:run

### Linux ou macOS

    chmod +x mvnw
    ./mvnw clean package
    ./mvnw spring-boot:run

L’application écoute par défaut sur le port 8081, avec le préfixe /api. La racine d’une requête est donc http://localhost:8081/api.

## Routes REST

Les opérations standard sont GET pour lecture, POST pour création, PUT pour mise à jour et DELETE pour suppression. Toutes les routes ci-dessous utilisent le préfixe global /api.

| Ressource | Chemin | Opérations disponibles |
|---|---|---|
| Agences | /agences | Liste, détail par identifiant, création, modification, suppression |
| Clients | /clients | Liste, détail par identifiant, création, modification, suppression |
| Employés | /employes | Liste, détail par identifiant, création, modification, suppression |
| Véhicules | /vehicules | Liste, détail par identifiant, création, modification, suppression |
| Équipements | /equipements | Liste, détail par identifiant, création, modification, suppression |
| Maintenances | /maintenances | Liste, détail par identifiant, création, modification, suppression |
| Réservations | /reservations | Liste, détail par identifiant, création, modification, suppression |
| Contrats | /contrats | Liste, détail par identifiant, création, modification, suppression |
| Paiements | /paiements | Liste et détail par identifiant |

Pour les opérations par identifiant, ajoutez /{id} au chemin. Les créations renvoient normalement 201 Created, les suppressions 204 No Content, et les lectures ou modifications 200 OK.

### Exemple de création d’agence

    POST http://localhost:8081/api/agences
    Content-Type: application/json

    {
      "nom": "Agence Centre",
      "ville": "Tunis",
      "adresse": "Avenue Habib Bourguiba",
      "telephone": "+216 71 000 000"
    }

### Exemple de lecture des véhicules

    GET http://localhost:8081/api/vehicules
    Accept: application/json

Les dates doivent utiliser le format ISO YYYY-MM-DD. Les montants sont transmis comme nombres décimaux JSON. Pour relier des entités, transmettez les références ayant leurs identifiants dans la structure JSON attendue par les entités.

## Documentation interactive

Lorsque l’application est démarrée, Swagger UI est disponible à l’adresse http://localhost:8081/api/swagger-ui/index.html. Le document OpenAPI JSON est exposé à http://localhost:8081/api/v3/api-docs.

## Erreurs

Le gestionnaire global produit pour les erreurs prises en charge un objet JSON contenant statut, message et horodatage.

- 404 Not Found : ressource demandée absente.
- 400 Bad Request : arguments ou données métier invalides, par exemple un nom obligatoire manquant ou un identifiant fourni lors d’une création.

Les détails précis des validations sont définis dans les services. Les erreurs techniques non couvertes par ce gestionnaire dépendent du comportement Spring.

## Sécurité

La configuration actuelle désactive CSRF, HTTP Basic et le formulaire de connexion, puis autorise toutes les requêtes. L’API ne met donc pas en place d’authentification ou d’autorisation; cette configuration convient à un usage de développement et doit être réévaluée avant une exposition publique.

## Tests

Les tests se trouvent dans src/test/java. Pour exécuter la suite :

Windows :

    .mvnw.cmd test

Linux ou macOS :

    ./mvnw test

Les classes présentes couvrent notamment le démarrage de l’application, les opérations CRUD des repositories et des exercices de services.

## Paramètres principaux

Dans src/main/resources/application.properties :

- server.port : port HTTP, 8081 par défaut.
- server.servlet.context-path : racine de l’API, /api.
- spring.datasource.url, spring.datasource.username et spring.datasource.password : connexion MySQL.
- spring.jpa.hibernate.ddl-auto : stratégie de mise à jour du schéma, update.
- spring.jpa.show-sql et les niveaux de journalisation : visibilité des requêtes SQL.

Évitez d’enregistrer des mots de passe réels dans un dépôt partagé; utilisez une configuration adaptée à l’environnement de déploiement.

## Limites observables

- Le dépôt ne fournit pas d’interface utilisateur.
- L’accès HTTP est actuellement ouvert et sans authentification.
- Les paiements sont exposés en lecture dans leur contrôleur; leur gestion est liée au contrat.
- Les contrôles complexes de disponibilité, de chevauchement des périodes et de calcul de tarif ne sont pas garantis par la seule présence des endpoints; consultez la logique métier correspondante avant de dépendre de ces règles.

## Licence

Aucun fichier de licence n’est présent dans le dépôt. Les conditions de réutilisation restent donc à définir par les responsables du projet.
