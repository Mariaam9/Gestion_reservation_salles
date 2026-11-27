package tn.salles.reservation.designpatterns.factory;

import org.springframework.stereotype.Component;
import tn.salles.reservation.model.Creneau;
import tn.salles.reservation.model.Employe;
import tn.salles.reservation.model.Reservation;
import tn.salles.reservation.model.Salle;
import tn.salles.reservation.service.commande.ReservationCommande;

/**
 * Factory chargée de construire une réservation et son créneau associé.
 * La construction est centralisée afin que les règles d'initialisation
 * restent identiques dans les services et les données de démonstration.
 */
@Component
public class ReservationFactory {

    /**
     * Crée une réservation confirmée à partir des données validées de la commande.
     *
     * @param employe employé titulaire de la réservation
     * @param salle salle réservée
     * @param commande données de la réservation
     * @return réservation confirmée avec son créneau associé
     */
    public Reservation creerReservationConfirmee(
            Employe employe,
            Salle salle,
            ReservationCommande commande) {

        Creneau creneau = new Creneau(
                salle,
                commande.dateDebut(),
                commande.dateFin()
        );

        Reservation reservation = new Reservation(
                employe,
                creneau,
                normaliserMotif(commande.motif()),
                commande.nombreParticipants()
        );

        reservation.confirmer();
        return reservation;
    }

    public String normaliserMotif(String motif) {
        return motif == null ? "" : motif.trim();
    }
}
