package tn.salles.reservation.designpatterns.strategy.regles;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import tn.salles.reservation.designpatterns.strategy.ContexteReservation;
import tn.salles.reservation.designpatterns.strategy.RegleReservation;
import tn.salles.reservation.exception.RegleMetierException;

/** Stratégie concrète : une salle indisponible ou en maintenance ne peut pas être réservée. */
@Component
@Order(2)
public class SalleOperationnelleRegle implements RegleReservation {

    @Override
    public void verifier(ContexteReservation contexte) {
        if (!contexte.salle().estDisponible()) {
            throw new RegleMetierException("La salle est indisponible ou en maintenance.");
        }
    }
}
