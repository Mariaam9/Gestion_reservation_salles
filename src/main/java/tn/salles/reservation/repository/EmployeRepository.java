package tn.salles.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.salles.reservation.model.Employe;

public interface EmployeRepository extends JpaRepository<Employe, Long> {

    boolean existsByEmailIgnoreCase(String email);
}
