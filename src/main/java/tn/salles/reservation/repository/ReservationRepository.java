package tn.salles.reservation.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.salles.reservation.model.Reservation;
import tn.salles.reservation.model.StatutReservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("""
            select count(r) from Reservation r
            where r.creneau.salle.id = :salleId and r.statut = :statut
              and r.creneau.dateDebut < :fin and r.creneau.dateFin > :debut
            """)
    long compterConflits(@Param("salleId") Long salleId,
                         @Param("debut") LocalDateTime debut,
                         @Param("fin") LocalDateTime fin,
                         @Param("statut") StatutReservation statut);

    @Query("""
            select count(r) from Reservation r
            where r.creneau.salle.id = :salleId and r.statut = :statut
              and r.creneau.dateDebut < :fin and r.creneau.dateFin > :debut
              and r.id <> :exclureId
            """)
    long compterConflitsSansReservation(@Param("salleId") Long salleId,
                                        @Param("debut") LocalDateTime debut,
                                        @Param("fin") LocalDateTime fin,
                                        @Param("statut") StatutReservation statut,
                                        @Param("exclureId") Long exclureId);

    /** Point d'entrée unique : ignore la réservation {@code exclureId} si elle est fournie (cas modification). */
    default long compterConflitsConfirmes(Long salleId, LocalDateTime debut, LocalDateTime fin, Long exclureId) {
        return exclureId == null
                ? compterConflits(salleId, debut, fin, StatutReservation.CONFIRMEE)
                : compterConflitsSansReservation(salleId, debut, fin, StatutReservation.CONFIRMEE, exclureId);
    }

    @Query("""
            select r from Reservation r
            where r.statut = :statut
              and r.creneau.dateDebut < :fin and r.creneau.dateFin > :debut
            order by r.creneau.dateDebut asc
            """)
    List<Reservation> trouverSurPeriode(@Param("debut") LocalDateTime debut,
                                        @Param("fin") LocalDateTime fin,
                                        @Param("statut") StatutReservation statut);

    @Query("""
            select r from Reservation r
            where r.creneau.salle.id = :salleId and r.statut = :statut
              and r.creneau.dateDebut < :fin and r.creneau.dateFin > :debut
            order by r.creneau.dateDebut asc
            """)
    List<Reservation> trouverSallePeriode(@Param("salleId") Long salleId,
                                          @Param("debut") LocalDateTime debut,
                                          @Param("fin") LocalDateTime fin,
                                          @Param("statut") StatutReservation statut);

    List<Reservation> findAllByOrderByIdDesc();

    long countByStatut(StatutReservation statut);
}
