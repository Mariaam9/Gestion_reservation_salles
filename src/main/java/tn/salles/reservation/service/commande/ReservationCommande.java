package tn.salles.reservation.service.commande;

import java.time.LocalDateTime;

/**
 * Données d'entrée d'une création / modification de réservation.
 * Découple la couche service de la couche web (le service ne connaît plus les formulaires).
 */
public record ReservationCommande(
        Long employeId,
        Long salleId,
        LocalDateTime dateDebut,
        LocalDateTime dateFin,
        String motif,
        int nombreParticipants) {
}
