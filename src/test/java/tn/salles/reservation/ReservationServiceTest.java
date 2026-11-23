package tn.salles.reservation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import tn.salles.reservation.exception.RegleMetierException;
import tn.salles.reservation.model.Employe;
import tn.salles.reservation.model.Reservation;
import tn.salles.reservation.model.Salle;
import tn.salles.reservation.model.StatistiqueUtilisation;
import tn.salles.reservation.model.StatutReservation;
import tn.salles.reservation.model.StatutSalle;
import tn.salles.reservation.repository.EmployeRepository;
import tn.salles.reservation.repository.SalleRepository;
import tn.salles.reservation.service.DisponibiliteService;
import tn.salles.reservation.service.ReservationService;
import tn.salles.reservation.service.StatistiqueService;
import tn.salles.reservation.service.commande.ReservationCommande;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ReservationServiceTest {

    @Autowired ReservationService service;
    @Autowired DisponibiliteService disponibilite;
    @Autowired StatistiqueService statistiques;
    @Autowired EmployeRepository employes;
    @Autowired SalleRepository salles;

    private Long salleId;
    private Long employeId;
    private LocalDateTime debut;
    private LocalDateTime fin;

    @BeforeEach
    void init() {
        Salle salle = salles.save(new Salle("Salle test", 5, "Bureau 1", StatutSalle.DISPONIBLE, null));
        Employe employe = employes.save(new Employe("Test User", "test" + System.nanoTime() + "@example.org"));
        salleId = salle.getId();
        employeId = employe.getId();
        debut = LocalDate.now().plusDays(3).atTime(10, 0);
        fin = debut.plusHours(2);
    }

    private ReservationCommande commande(LocalDateTime d, LocalDateTime f, int participants) {
        return new ReservationCommande(employeId, salleId, d, f, "Réunion test", participants);
    }

    @Test
    void creationSansConflitConfirmeLaReservation() {
        Reservation r = service.creer(commande(debut, fin, 3));
        assertNotNull(r.getId());
        assertEquals(StatutReservation.CONFIRMEE, r.getStatut());
        assertFalse(disponibilite.verifierDisponibilite(salleId, debut.plusMinutes(30), fin));
    }

    @Test
    void deuxReservationsQuiSeChevauchentSontRefusees() {
        service.creer(commande(debut, fin, 3));
        assertThrows(RegleMetierException.class,
                () -> service.creer(commande(debut.plusHours(1), fin.plusHours(1), 2)));
    }

    @Test
    void reservationsContiguesSontPermises() {
        service.creer(commande(debut, fin, 2));
        assertDoesNotThrow(() -> service.creer(commande(fin, fin.plusHours(1), 2)));
    }

    @Test
    void annulationLibereLaSalle() {
        Reservation r = service.creer(commande(debut, fin, 2));
        service.annuler(r.getId());
        assertTrue(disponibilite.verifierDisponibilite(salleId, debut, fin));
        assertDoesNotThrow(() -> service.creer(commande(debut, fin, 2)));
    }

    @Test
    void capaciteEstRespectee() {
        assertThrows(RegleMetierException.class, () -> service.creer(commande(debut, fin, 6)));
    }

    @Test
    void datesInvalidesSontRefusees() {
        assertThrows(RegleMetierException.class, () -> service.creer(commande(fin, debut, 2)));
    }

    @Test
    void modifierIgnoreSonPropreCreneauMaisRefuseLeConflit() {
        Reservation r = service.creer(commande(debut, fin, 2));
        assertDoesNotThrow(() -> service.modifier(r.getId(), commande(debut, fin.plusMinutes(30), 3)));
        assertEquals(3, service.parId(r.getId()).getNombreParticipants());

        Reservation autre = service.creer(commande(fin.plusHours(2), fin.plusHours(3), 2));
        assertThrows(RegleMetierException.class,
                () -> service.modifier(autre.getId(), commande(debut, fin, 2)));
    }

    @Test
    void lesStatistiquesCalculentDeuxHeuresSurVingtQuatre() {
        service.creer(commande(debut, fin, 2));
        List<StatistiqueUtilisation> rapports = statistiques.calculerStatistiques(
                debut.toLocalDate().atStartOfDay(), debut.toLocalDate().plusDays(1).atStartOfDay());
        StatistiqueUtilisation resultat = rapports.stream()
                .filter(s -> s.salle().getId().equals(salleId))
                .findFirst()
                .orElseThrow();
        assertEquals(1, resultat.nombreReservations());
        assertEquals(8.33, resultat.tauxOccupation(), 0.01);
    }
}
