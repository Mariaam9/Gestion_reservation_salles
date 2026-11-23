package tn.salles.reservation.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.salles.reservation.model.Periode;
import tn.salles.reservation.model.Reservation;
import tn.salles.reservation.model.Salle;
import tn.salles.reservation.model.StatistiqueUtilisation;
import tn.salles.reservation.model.StatutReservation;
import tn.salles.reservation.repository.ReservationRepository;
import tn.salles.reservation.repository.SalleRepository;
import tn.salles.reservation.service.StatistiqueService;

@Service
public class StatistiqueServiceImpl implements StatistiqueService {

    private static final double TAUX_MAXIMUM = 100.0;

    private final SalleRepository salles;
    private final ReservationRepository reservations;

    public StatistiqueServiceImpl(SalleRepository salles, ReservationRepository reservations) {
        this.salles = salles;
        this.reservations = reservations;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StatistiqueUtilisation> calculerStatistiques(LocalDateTime debut, LocalDateTime fin) {
        Periode periode = new Periode(debut, fin);
        return salles.findAll().stream()
                .map(salle -> calculerPourSalle(salle, periode))
                .toList();
    }

    private StatistiqueUtilisation calculerPourSalle(Salle salle, Periode periode) {
        List<Reservation> liste = reservations.trouverSallePeriode(
                salle.getId(), periode.debut(), periode.fin(), StatutReservation.CONFIRMEE);
        long minutesOccupees = minutesOccupeesSansDoublon(liste, periode);
        long minutesTotales = periode.dureeEnMinutes();
        double taux = minutesTotales == 0 ? 0 : arrondirADeuxDecimales(100.0 * minutesOccupees / minutesTotales);
        return new StatistiqueUtilisation(salle, periode.debut(), periode.fin(),
                liste.size(), Math.min(TAUX_MAXIMUM, taux));
    }

    /**
     * Fusionne les plages qui se recouvrent (réservations triées par date de début)
     * pour ne jamais compter deux fois le même temps.
     */
    private long minutesOccupeesSansDoublon(List<Reservation> reservationsTriees, Periode periode) {
        long minutes = 0;
        LocalDateTime debutFusion = null;
        LocalDateTime finFusion = null;

        for (Reservation reservation : reservationsTriees) {
            LocalDateTime debut = max(reservation.getCreneau().getDateDebut(), periode.debut());
            LocalDateTime fin = min(reservation.getCreneau().getDateFin(), periode.fin());

            if (debutFusion == null) {
                debutFusion = debut;
                finFusion = fin;
            } else if (!debut.isAfter(finFusion)) {
                finFusion = max(finFusion, fin);
            } else {
                minutes += Duration.between(debutFusion, finFusion).toMinutes();
                debutFusion = debut;
                finFusion = fin;
            }
        }
        if (debutFusion != null) {
            minutes += Duration.between(debutFusion, finFusion).toMinutes();
        }
        return minutes;
    }

    private static LocalDateTime max(LocalDateTime a, LocalDateTime b) {
        return a.isAfter(b) ? a : b;
    }

    private static LocalDateTime min(LocalDateTime a, LocalDateTime b) {
        return a.isBefore(b) ? a : b;
    }

    private static double arrondirADeuxDecimales(double valeur) {
        return Math.round(valeur * 100.0) / 100.0;
    }
}
