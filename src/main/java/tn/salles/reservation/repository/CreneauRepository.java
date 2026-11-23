package tn.salles.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.salles.reservation.model.Creneau;

public interface CreneauRepository extends JpaRepository<Creneau, Long> {
}
