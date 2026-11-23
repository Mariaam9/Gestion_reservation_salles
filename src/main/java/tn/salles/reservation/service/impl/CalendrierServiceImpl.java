package tn.salles.reservation.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.salles.reservation.model.Reservation;
import tn.salles.reservation.model.StatutReservation;
import tn.salles.reservation.repository.ReservationRepository;
import tn.salles.reservation.service.CalendrierService;

@Service
public class CalendrierServiceImpl implements CalendrierService {

    private final ReservationRepository reservations;

    public CalendrierServiceImpl(ReservationRepository reservations) {
        this.reservations = reservations;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reservation> afficherCalendrier(LocalDateTime debut, LocalDateTime fin) {
        return reservations.trouverSurPeriode(debut, fin, StatutReservation.CONFIRMEE);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<LocalDate, List<Reservation>> afficherMois(YearMonth mois) {
        LocalDateTime debut = mois.atDay(1).atStartOfDay();
        LocalDateTime fin = mois.plusMonths(1).atDay(1).atStartOfDay();
        return afficherCalendrier(debut, fin).stream()
                .collect(Collectors.groupingBy(
                        reservation -> reservation.getCreneau().getDateDebut().toLocalDate(),
                        TreeMap::new,
                        Collectors.toList()));
    }
}
