package tn.salles.reservation.service;

import java.time.LocalDateTime;
import java.util.List;
import tn.salles.reservation.model.Salle;

public interface DisponibiliteService {

    boolean verifierDisponibilite(Long salleId, LocalDateTime debut, LocalDateTime fin);

    List<Salle> rechercherSallesDisponibles(LocalDateTime debut, LocalDateTime fin, int capacite);
}
