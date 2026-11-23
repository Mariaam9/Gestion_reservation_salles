package tn.salles.reservation.designpatterns.strategy;

import tn.salles.reservation.model.Salle;
import tn.salles.reservation.service.commande.ReservationCommande;

/**
 * Données nécessaires aux stratégies de validation.
 *
 * @param salle               salle visée (déjà chargée et verrouillée)
 * @param commande            données saisies par l'utilisateur
 * @param reservationExclueId réservation à ignorer lors de la détection de conflit (modification), sinon {@code null}
 */
public record ContexteReservation(Salle salle, ReservationCommande commande, Long reservationExclueId) {
}
