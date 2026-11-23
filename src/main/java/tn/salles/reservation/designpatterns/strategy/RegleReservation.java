package tn.salles.reservation.designpatterns.strategy;

import tn.salles.reservation.exception.RegleMetierException;

/**
 * <b>Design pattern Strategy</b> (cours MASI-06) — interface « Strategy ».
 * <p>
 * Chaque règle métier de réservation est un algorithme de vérification
 * encapsulé dans sa propre classe et interchangeable.
 * Ajouter une règle = ajouter une classe, sans modifier le code existant (OCP).
 * Interface volontairement réduite à une seule méthode (ISP).
 */
public interface RegleReservation {

    /**
     * @throws RegleMetierException si la règle n'est pas respectée
     */
    void verifier(ContexteReservation contexte);
}
