package tn.salles.reservation.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import tn.salles.reservation.model.Reservation;

public interface CalendrierService {

    List<Reservation> afficherCalendrier(LocalDateTime debut, LocalDateTime fin);

    Map<LocalDate, List<Reservation>> afficherMois(YearMonth mois);
}
