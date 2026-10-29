package tn.esprit.tpfoyer.domain;

import jakarta.persistence.*;
import lombok.*;
import tn.esprit.tpfoyer.domain.enums.StatutReservation;

import java.time.LocalDate;

@Entity
@Table(name = "reservation")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReservation;

    private LocalDate dateDebut;
    private LocalDate dateFin;

    @Enumerated(EnumType.STRING)
    private StatutReservation statut;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "vehicule_id")
    private Vehicule vehicule;

    @OneToOne
    @JoinColumn(name = "contrat_id")
    private Contrat contrat;
}