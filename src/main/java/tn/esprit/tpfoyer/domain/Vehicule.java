package tn.esprit.tpfoyer.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import tn.esprit.tpfoyer.domain.enums.CategorieVehicule;
import tn.esprit.tpfoyer.domain.enums.StatutVehicule;

import java.math.BigDecimal;
import java.util.*;

@Entity
@Table(name = "vehicule")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vehicule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVehicule;

    private String immatriculation;
    private String marque;
    private String modele;

    @Enumerated(EnumType.STRING)
    private CategorieVehicule categorie;

    private BigDecimal tarifJournalier;

    @Enumerated(EnumType.STRING)
    private StatutVehicule statut;

    @ManyToOne
    @JoinColumn(name = "agence_id")
    private Agence agence;

    @JsonIgnore
    @OneToMany(mappedBy = "vehicule")
    private List<Reservation> reservations = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "vehicule")
    private List<Maintenance> maintenances = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "vehicule_equipements",
            joinColumns = @JoinColumn(name = "vehicule_id"),
            inverseJoinColumns = @JoinColumn(name = "equipement_id"))
    private List<Equipement> equipements = new ArrayList<>();
}