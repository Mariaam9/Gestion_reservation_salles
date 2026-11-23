package tn.salles.reservation.designpatterns.strategy.regles;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import tn.salles.reservation.designpatterns.strategy.ContexteReservation;
import tn.salles.reservation.designpatterns.strategy.RegleReservation;
import tn.salles.reservation.exception.RegleMetierException;
import tn.salles.reservation.repository.ReservationRepository;
import tn.salles.reservation.service.commande.ReservationCommande;

/** Stratégie concrète : aucune réservation confirmée ne doit chevaucher le créneau demandé. */
@Component
@Order(4)
public class AbsenceConflitRegle implements RegleReservation {

    private final ReservationRepository reservations;

    public AbsenceConflitRegle(ReservationRepository reservations) {
        this.reservations = reservations;
    }

    @Override
    public void verifier(ContexteReservation contexte) {
        ReservationCommande commande = contexte.commande();
        long conflits = reservations.compterConflitsConfirmes(
                contexte.salle().getId(), commande.dateDebut(), commande.dateFin(), contexte.reservationExclueId());
        if (conflits > 0) {
            throw new RegleMetierException(
                    "Conflit : cette salle possède déjà une réservation confirmée pendant cette période.");
        }
    }
}
