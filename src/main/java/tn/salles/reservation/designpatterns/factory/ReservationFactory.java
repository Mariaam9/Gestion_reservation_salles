package tn.salles.reservation.designpatterns.factory;

import java.time.LocalDateTime;
import org.springframework.stereotype.Component;
import tn.salles.reservation.model.Creneau;
import tn.salles.reservation.model.Employe;
import tn.salles.reservation.model.Reservation;
import tn.salles.reservation.model.Salle;

/**
 * <b>Design pattern Factory</b> (cours MASI-08).
 * <p>
 * Centralise la création d'une réservation et de son créneau.
 * Le code client (service, jeu de données de démonstration) n'utilise plus
 * {@code new Reservation(...)} / {@code new Creneau(...)} : si la façon de construire
 * une réservation change (statut initial, nettoyage du motif...), seule cette classe est modifiée.
 */
@Component
public class ReservationFactory {

    /** Crée une réservation confirmée : le MVP confirme immédiatement une réservation sans conflit. */
    public Reservation creerReservationConfirmee(Employe employe, Salle salle,
                                                 LocalDateTime debut, LocalDateTime fin,
                                                 String motif, int nombreParticipants) {
        Creneau creneau = new Creneau(salle, debut, fin);
        Reservation reservation = new Reservation(employe, creneau, normaliserMotif(motif), nombreParticipants);
        reservation.confirmer();
        return reservation;
    }

    public String normaliserMotif(String motif) {
        return motif == null ? "" : motif.trim();
    }
}
