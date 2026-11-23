package tn.salles.reservation.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

/**
 * Classe d'association UML Employé &lt;-&gt; Créneau.
 * Les transitions d'état (confirmer, annuler) sont encapsulées ici :
 * le statut ne peut pas être modifié arbitrairement depuis l'extérieur.
 */
@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "employe_id", nullable = false)
    private Employe employe;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, optional = false)
    @JoinColumn(name = "creneau_id", nullable = false, unique = true)
    private Creneau creneau;

    @Column(nullable = false, length = 300)
    private String motif;

    @Column(nullable = false)
    private int nombreParticipants = 1;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutReservation statut = StatutReservation.EN_ATTENTE;

    @Version
    private Long version;

    protected Reservation() {
        // requis par JPA
    }

    public Reservation(Employe employe, Creneau creneau, String motif, int nombreParticipants) {
        this.employe = employe;
        this.creneau = creneau;
        this.motif = motif;
        this.nombreParticipants = nombreParticipants;
    }

    public void confirmer() {
        if (estAnnulee()) {
            throw new IllegalStateException("Réservation annulée");
        }
        statut = StatutReservation.CONFIRMEE;
    }

    public void annuler() {
        if (estAnnulee()) {
            throw new IllegalStateException("Déjà annulée");
        }
        statut = StatutReservation.ANNULEE;
    }

    public boolean estAnnulee() {
        return statut == StatutReservation.ANNULEE;
    }

    public boolean estModifiable() {
        return !estAnnulee();
    }

    public void modifierDetails(String nouveauMotif, int nouveauNombreParticipants) {
        this.motif = nouveauMotif;
        this.nombreParticipants = nouveauNombreParticipants;
    }

    public Long getId() { return id; }
    public Employe getEmploye() { return employe; }
    public Creneau getCreneau() { return creneau; }
    public String getMotif() { return motif; }
    public int getNombreParticipants() { return nombreParticipants; }
    public StatutReservation getStatut() { return statut; }
}
