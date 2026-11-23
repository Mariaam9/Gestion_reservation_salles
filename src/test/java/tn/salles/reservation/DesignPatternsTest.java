package tn.salles.reservation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import org.junit.jupiter.api.Test;
import tn.salles.reservation.designpatterns.factory.ReservationFactory;
import tn.salles.reservation.designpatterns.singleton.JournalReservations;
import tn.salles.reservation.designpatterns.strategy.ContexteReservation;
import tn.salles.reservation.designpatterns.strategy.RegleReservation;
import tn.salles.reservation.designpatterns.strategy.ValidateurReservation;
import tn.salles.reservation.designpatterns.strategy.regles.CapaciteSalleRegle;
import tn.salles.reservation.designpatterns.strategy.regles.PeriodeValideRegle;
import tn.salles.reservation.designpatterns.strategy.regles.SalleOperationnelleRegle;
import tn.salles.reservation.exception.RegleMetierException;
import tn.salles.reservation.model.Employe;
import tn.salles.reservation.model.Reservation;
import tn.salles.reservation.model.Salle;
import tn.salles.reservation.model.StatutReservation;
import tn.salles.reservation.model.StatutSalle;
import tn.salles.reservation.service.commande.ReservationCommande;

/** Tests unitaires (sans Spring ni base de données) des trois design patterns. */
class DesignPatternsTest {

    private static final LocalDateTime DEBUT = LocalDateTime.of(2030, 1, 10, 9, 0);
    private static final LocalDateTime FIN = DEBUT.plusHours(1);

    private final Salle salle = new Salle("Atlas", 5, "Bâtiment A", StatutSalle.DISPONIBLE, null);

    private ContexteReservation contexte(LocalDateTime debut, LocalDateTime fin, int participants) {
        return new ContexteReservation(salle,
                new ReservationCommande(1L, 1L, debut, fin, "Réunion", participants), null);
    }

    // ---------- Singleton ----------

    @Test
    void singletonRenvoieToujoursLaMemeInstance() {
        assertSame(JournalReservations.getInstance(), JournalReservations.getInstance());
    }

    @Test
    void singletonResteUniqueEnMultiThread() throws InterruptedException {
        Set<JournalReservations> instances = ConcurrentHashMap.newKeySet();
        Thread t1 = new Thread(() -> instances.add(JournalReservations.getInstance()));
        Thread t2 = new Thread(() -> instances.add(JournalReservations.getInstance()));
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        assertEquals(1, instances.size());
    }

    @Test
    void journalEnregistreLesOperations() {
        JournalReservations journal = JournalReservations.getInstance();
        int avant = journal.consulter().size();
        journal.enregistrer("TEST", "opération de test");
        assertEquals(avant + 1, journal.consulter().size());
    }

    // ---------- Strategy ----------

    @Test
    void strategiePeriodeRefuseUneFinAvantLeDebut() {
        assertThrows(RegleMetierException.class,
                () -> new PeriodeValideRegle().verifier(contexte(FIN, DEBUT, 2)));
    }

    @Test
    void strategieCapaciteRefuseTropDeParticipants() {
        assertThrows(RegleMetierException.class,
                () -> new CapaciteSalleRegle().verifier(contexte(DEBUT, FIN, 6)));
    }

    @Test
    void strategieSalleOperationnelleRefuseUneSalleEnMaintenance() {
        salle.setStatut(StatutSalle.MAINTENANCE);
        assertThrows(RegleMetierException.class,
                () -> new SalleOperationnelleRegle().verifier(contexte(DEBUT, FIN, 2)));
    }

    @Test
    void contexteAppliqueToutesLesStrategiesEtAccepteUneNouvelleRegleSansModification() {
        RegleReservation pasLeDimanche = ctx -> {
            if (ctx.commande().dateDebut().getDayOfWeek() == java.time.DayOfWeek.SUNDAY) {
                throw new RegleMetierException("Pas de réservation le dimanche.");
            }
        };
        ValidateurReservation validateur = new ValidateurReservation(
                List.of(new PeriodeValideRegle(), new CapaciteSalleRegle(), pasLeDimanche));

        assertDoesNotThrow(() -> validateur.valider(contexte(DEBUT, FIN, 2)));           // jeudi
        LocalDateTime dimanche = LocalDateTime.of(2030, 1, 13, 9, 0);
        assertThrows(RegleMetierException.class,
                () -> validateur.valider(contexte(dimanche, dimanche.plusHours(1), 2)));
    }

    // ---------- Factory ----------

    @Test
    void factoryCreeUneReservationConfirmeeAvecSonCreneau() {
        Employe employe = new Employe("Sami", "sami@example.com");
        Reservation reservation = new ReservationFactory()
                .creerReservationConfirmee(employe, salle, DEBUT, FIN, "  Réunion  ", 3);

        assertEquals(StatutReservation.CONFIRMEE, reservation.getStatut());
        assertEquals("Réunion", reservation.getMotif());
        assertSame(salle, reservation.getCreneau().getSalle());
        assertEquals(DEBUT, reservation.getCreneau().getDateDebut());
    }
}
