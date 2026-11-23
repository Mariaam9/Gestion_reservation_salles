package tn.salles.reservation.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/** Créneau horaire d'une salle. Chaque réservation possède son propre créneau. */
@Entity
@Table(name = "creneaux")
public class Creneau {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dateDebut;

    @Column(nullable = false)
    private LocalDateTime dateFin;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "salle_id", nullable = false)
    private Salle salle;

    protected Creneau() {
        // requis par JPA
    }

    public Creneau(Salle salle, LocalDateTime dateDebut, LocalDateTime dateFin) {
        this.salle = salle;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }

    public boolean estValide() {
        return Periode.estValide(dateDebut, dateFin);
    }

    public boolean chevauche(Creneau autre) {
        return dateDebut.isBefore(autre.dateFin) && autre.dateDebut.isBefore(dateFin);
    }

    /** Déplace le créneau (salle et horaires) en une seule opération cohérente. */
    public void deplacer(Salle nouvelleSalle, LocalDateTime nouveauDebut, LocalDateTime nouvelleFin) {
        this.salle = nouvelleSalle;
        this.dateDebut = nouveauDebut;
        this.dateFin = nouvelleFin;
    }

    public Long getId() { return id; }
    public LocalDateTime getDateDebut() { return dateDebut; }
    public LocalDateTime getDateFin() { return dateFin; }
    public Salle getSalle() { return salle; }
}
