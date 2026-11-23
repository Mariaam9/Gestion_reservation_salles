package tn.salles.reservation.service;

import java.util.List;
import tn.salles.reservation.model.Reservation;
import tn.salles.reservation.service.commande.ReservationCommande;

/** Abstraction dont dépendent les contrôleurs (DIP). */
public interface ReservationService {

    List<Reservation> toutes();

    Reservation parId(Long id);

    Reservation creer(ReservationCommande commande);

    Reservation modifier(Long id, ReservationCommande commande);

    void annuler(Long id);
}
