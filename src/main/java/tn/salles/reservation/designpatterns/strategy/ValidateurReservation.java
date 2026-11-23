package tn.salles.reservation.designpatterns.strategy;

import java.util.List;
import org.springframework.stereotype.Component;

/**
 * <b>Design pattern Strategy</b> — classe « Context ».
 * <p>
 * Applique les stratégies {@link RegleReservation} sans connaître leurs classes concrètes.
 * Spring injecte automatiquement toutes les implémentations, triées par {@code @Order} :
 * il n'y a ni {@code if/else} ni {@code switch} à maintenir.
 */
@Component
public class ValidateurReservation {

    private final List<RegleReservation> regles;

    public ValidateurReservation(List<RegleReservation> regles) {
        this.regles = List.copyOf(regles);
    }

    public void valider(ContexteReservation contexte) {
        regles.forEach(regle -> regle.verifier(contexte));
    }
}
