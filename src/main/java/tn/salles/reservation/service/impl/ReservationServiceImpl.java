package tn.salles.reservation.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.salles.reservation.designpatterns.factory.ReservationFactory;
import tn.salles.reservation.designpatterns.singleton.JournalReservations;
import tn.salles.reservation.designpatterns.strategy.ContexteReservation;
import tn.salles.reservation.designpatterns.strategy.ValidateurReservation;
import tn.salles.reservation.exception.RegleMetierException;
import tn.salles.reservation.exception.RessourceIntrouvableException;
import tn.salles.reservation.model.Employe;
import tn.salles.reservation.model.Reservation;
import tn.salles.reservation.model.Salle;
import tn.salles.reservation.repository.EmployeRepository;
import tn.salles.reservation.repository.ReservationRepository;
import tn.salles.reservation.repository.SalleRepository;
import tn.salles.reservation.service.ReservationService;
import tn.salles.reservation.service.commande.ReservationCommande;

/**
 * Orchestration du cycle de vie d'une réservation.
 * Les règles métier sont déléguées aux stratégies (Strategy), la construction
 * à la fabrique (Factory) et la traçabilité au journal (Singleton).
 */
@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservations;
    private final SalleRepository salles;
    private final EmployeRepository employes;
    private final ValidateurReservation validateur;
    private final ReservationFactory fabrique;

    public ReservationServiceImpl(ReservationRepository reservations,
                                  SalleRepository salles,
                                  EmployeRepository employes,
                                  ValidateurReservation validateur,
                                  ReservationFactory fabrique) {
        this.reservations = reservations;
        this.salles = salles;
        this.employes = employes;
        this.validateur = validateur;
        this.fabrique = fabrique;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reservation> toutes() {
        return reservations.findAllByOrderByIdDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public Reservation parId(Long id) {
        return reservations.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Réservation introuvable."));
    }

    /** Verrouiller la salle avant de chercher des conflits évite deux confirmations concurrentes. */
    @Override
    @Transactional
    public Reservation creer(ReservationCommande commande) {
        Salle salle = verrouillerSalle(commande.salleId());
        validateur.valider(new ContexteReservation(salle, commande, null));
        Employe employe = trouverEmploye(commande.employeId());

        Reservation reservation = fabrique.creerReservationConfirmee(
                employe, salle, commande);
        Reservation enregistree = reservations.save(reservation);

        journaliser("CREATION", enregistree);
        return enregistree;
    }

    @Override
    @Transactional
    public Reservation modifier(Long id, ReservationCommande commande) {
        Salle salle = verrouillerSalle(commande.salleId());
        Reservation reservation = parId(id);
        verifierModificationAutorisee(reservation, commande);
        validateur.valider(new ContexteReservation(salle, commande, id));

        reservation.getCreneau().deplacer(salle, commande.dateDebut(), commande.dateFin());
        reservation.modifierDetails(fabrique.normaliserMotif(commande.motif()), commande.nombreParticipants());
        Reservation enregistree = reservations.save(reservation);

        journaliser("MODIFICATION", enregistree);
        return enregistree;
    }

    @Override
    @Transactional
    public void annuler(Long id) {
        Reservation reservation = parId(id);
        if (reservation.estAnnulee()) {
            return; // opération idempotente
        }
        reservation.annuler();
        journaliser("ANNULATION", reservation);
    }

    private Salle verrouillerSalle(Long salleId) {
        return salles.verrouillerParId(salleId)
                .orElseThrow(() -> new RessourceIntrouvableException("Salle introuvable."));
    }

    private Employe trouverEmploye(Long employeId) {
        return employes.findById(employeId)
                .orElseThrow(() -> new RessourceIntrouvableException("Employé introuvable."));
    }

    private void verifierModificationAutorisee(Reservation reservation, ReservationCommande commande) {
        if (!reservation.estModifiable()) {
            throw new RegleMetierException("Une réservation annulée ne peut plus être modifiée.");
        }
        if (!reservation.getEmploye().getId().equals(commande.employeId())) {
            throw new RegleMetierException("Le titulaire de la réservation ne peut pas être changé.");
        }
    }

    private void journaliser(String operation, Reservation reservation) {
        JournalReservations.getInstance().enregistrer(operation,
                "Réservation #" + reservation.getId()
                        + " — salle " + reservation.getCreneau().getSalle().getNom()
                        + " — " + reservation.getEmploye().getNom());
    }
}
