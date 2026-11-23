package tn.salles.reservation.service;

import java.time.LocalDateTime;
import java.util.List;
import tn.salles.reservation.model.StatistiqueUtilisation;

public interface StatistiqueService {

    List<StatistiqueUtilisation> calculerStatistiques(LocalDateTime debut, LocalDateTime fin);
}
