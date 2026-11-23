package tn.salles.reservation.web.api.dto;

import java.time.LocalDateTime;
import tn.salles.reservation.model.Creneau;
import tn.salles.reservation.model.Reservation;
import tn.salles.reservation.model.StatutReservation;

/** Représentation JSON d'une réservation (format inchangé pour le site Salleo). */
public record ReservationDto(
        Long id,
        Long employeId,
        Long salleId,
        String employe,
        String salle,
        LocalDateTime dateDebut,
        LocalDateTime dateFin,
        String motif,
        int participants,
        StatutReservation statut) {

    public static ReservationDto depuis(Reservation reservation) {
        Creneau creneau = reservation.getCreneau();
        return new ReservationDto(
                reservation.getId(),
                reservation.getEmploye().getId(),
                creneau.getSalle().getId(),
                reservation.getEmploye().getNom(),
                creneau.getSalle().getNom(),
                creneau.getDateDebut(),
                creneau.getDateFin(),
                reservation.getMotif(),
                reservation.getNombreParticipants(),
                reservation.getStatut());
    }
}
