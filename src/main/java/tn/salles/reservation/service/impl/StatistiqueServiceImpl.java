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
    private static final double FACTEUR_POURCENTAGE = 100.0;
    private static final double FACTEUR_ARRONDI = 100.0;

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
        List<Reservation> reservationsDeLaSalle = reservations.trouverSallePeriode(
                salle.getId(), periode.debut(), periode.fin(), StatutReservation.CONFIRMEE);
        long minutesOccupees = minutesOccupeesSansDoublon(reservationsDeLaSalle, periode);
        long minutesTotales = periode.dureeEnMinutes();
        double tauxOccupation = calculerTauxOccupation(minutesOccupees, minutesTotales);

        return new StatistiqueUtilisation(
                salle,
                periode.debut(),
                periode.fin(),
                reservationsDeLaSalle.size(),
                Math.min(TAUX_MAXIMUM, tauxOccupation)
        );
    }

    private double calculerTauxOccupation(long minutesOccupees, long minutesTotales) {
        if (minutesTotales == 0) {
            return 0;
        }

        double taux = FACTEUR_POURCENTAGE * minutesOccupees / minutesTotales;
        return arrondirADeuxDecimales(taux);
    }

    /**
     * Fusionne les plages qui se recouvrent (réservations triées par date de début)
     * pour ne jamais compter deux fois le même temps.
     */
    private long minutesOccupeesSansDoublon(List<Reservation> reservationsTriees, Periode periode) {
        long minutesOccupees = 0;
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
                minutesOccupees += Duration.between(debutFusion, finFusion).toMinutes();
                debutFusion = debut;
                finFusion = fin;
            }
        }

        if (debutFusion != null) {
            minutesOccupees += Duration.between(debutFusion, finFusion).toMinutes();
        }

        return minutesOccupees;
    }

    private static LocalDateTime max(LocalDateTime first, LocalDateTime second) {
        return first.isAfter(second) ? first : second;
    }

    private static LocalDateTime min(LocalDateTime first, LocalDateTime second) {
        return first.isBefore(second) ? first : second;
    }

    private static double arrondirADeuxDecimales(double valeur) {
        return Math.round(valeur * FACTEUR_ARRONDI) / FACTEUR_ARRONDI;
    }
}
