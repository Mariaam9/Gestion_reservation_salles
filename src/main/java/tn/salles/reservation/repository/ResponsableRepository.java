package tn.salles.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.salles.reservation.model.Responsable;

public interface ResponsableRepository extends JpaRepository<Responsable, Long> {
}
