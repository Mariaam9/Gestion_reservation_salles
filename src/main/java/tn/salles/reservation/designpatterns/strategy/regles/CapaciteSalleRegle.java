package tn.salles.reservation.designpatterns.strategy.regles;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import tn.salles.reservation.designpatterns.strategy.ContexteReservation;
import tn.salles.reservation.designpatterns.strategy.RegleReservation;
import tn.salles.reservation.exception.RegleMetierException;
import tn.salles.reservation.model.Salle;

/** Stratégie concrète : le nombre de participants doit être compris entre 1 et la capacité de la salle. */
@Component
@Order(3)
public class CapaciteSalleRegle implements RegleReservation {

    @Override
    public void verifier(ContexteReservation contexte) {
        Salle salle = contexte.salle();
        if (!salle.peutAccueillir(contexte.commande().nombreParticipants())) {
            throw new RegleMetierException(
                    "Le nombre de participants doit être compris entre 1 et " + salle.getCapacite() + ".");
        }
    }
}
