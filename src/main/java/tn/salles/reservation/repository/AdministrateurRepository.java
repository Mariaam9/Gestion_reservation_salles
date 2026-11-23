package tn.salles.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.salles.reservation.model.Administrateur;

public interface AdministrateurRepository extends JpaRepository<Administrateur, Long> {
}
