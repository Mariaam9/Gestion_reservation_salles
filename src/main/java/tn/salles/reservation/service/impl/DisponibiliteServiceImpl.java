package tn.salles.reservation.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.salles.reservation.exception.RegleMetierException;
import tn.salles.reservation.model.Periode;
import tn.salles.reservation.model.Salle;
import tn.salles.reservation.model.StatutSalle;
import tn.salles.reservation.repository.ReservationRepository;
import tn.salles.reservation.repository.SalleRepository;
import tn.salles.reservation.service.DisponibiliteService;

@Service
public class DisponibiliteServiceImpl implements DisponibiliteService {

    private final SalleRepository salles;
    private final ReservationRepository reservations;

    public DisponibiliteServiceImpl(SalleRepository salles, ReservationRepository reservations) {
        this.salles = salles;
        this.reservations = reservations;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verifierDisponibilite(Long salleId, LocalDateTime debut, LocalDateTime fin) {
        if (!Periode.estValide(debut, fin)) {
            return false;
        }
        return salles.findById(salleId)
                .filter(Salle::estDisponible)
                .filter(salle -> estLibre(salle, debut, fin))
                .isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Salle> rechercherSallesDisponibles(LocalDateTime debut, LocalDateTime fin, int capacite) {
        new Periode(debut, fin);
        if (capacite < 1) {
            throw new RegleMetierException("Capacité invalide.");
        }
        return salles.findByStatutOrderByNomAsc(StatutSalle.DISPONIBLE).stream()
                .filter(salle -> salle.getCapacite() >= capacite)
                .filter(salle -> estLibre(salle, debut, fin))
                .toList();
    }

    private boolean estLibre(Salle salle, LocalDateTime debut, LocalDateTime fin) {
        return reservations.compterConflitsConfirmes(salle.getId(), debut, fin, null) == 0;
    }
}
