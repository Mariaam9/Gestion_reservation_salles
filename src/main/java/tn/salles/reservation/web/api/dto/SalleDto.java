package tn.salles.reservation.web.api.dto;

import tn.salles.reservation.model.Salle;
import tn.salles.reservation.model.StatutSalle;

/** Représentation JSON d'une salle (n'expose pas l'entité JPA). */
public record SalleDto(Long id, String nom, int capacite, String localisation, StatutSalle statut) {

    public static SalleDto depuis(Salle salle) {
        return new SalleDto(salle.getId(), salle.getNom(), salle.getCapacite(),
                salle.getLocalisation(), salle.getStatut());
    }
}
