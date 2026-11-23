package tn.salles.reservation.designpatterns.strategy.regles;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import tn.salles.reservation.designpatterns.strategy.ContexteReservation;
import tn.salles.reservation.designpatterns.strategy.RegleReservation;
import tn.salles.reservation.model.Periode;

/** Stratégie concrète : la date de début doit précéder la date de fin. */
@Component
@Order(1)
public class PeriodeValideRegle implements RegleReservation {

    @Override
    public void verifier(ContexteReservation contexte) {
        new Periode(contexte.commande().dateDebut(), contexte.commande().dateFin());
    }
}
