package tn.salles.reservation.repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.salles.reservation.model.Salle;
import tn.salles.reservation.model.StatutSalle;

public interface SalleRepository extends JpaRepository<Salle, Long> {

    /** Verrou pessimiste : empêche deux réservations concurrentes sur la même salle. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Salle s where s.id = :id")
    Optional<Salle> verrouillerParId(@Param("id") Long id);

    List<Salle> findByStatutOrderByNomAsc(StatutSalle statut);
}
