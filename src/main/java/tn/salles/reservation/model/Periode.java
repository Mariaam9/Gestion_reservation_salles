package tn.salles.reservation.model;

import java.time.Duration;
import java.time.LocalDateTime;
import tn.salles.reservation.exception.RegleMetierException;

/**
 * Objet valeur représentant un intervalle [debut, fin[.
 * Centralise la validation des périodes, auparavant dupliquée dans trois services.
 */
public record Periode(LocalDateTime debut, LocalDateTime fin) {

    public Periode {
        if (!estValide(debut, fin)) {
            throw new RegleMetierException("La date de début doit précéder la date de fin.");
        }
    }

    public static boolean estValide(LocalDateTime debut, LocalDateTime fin) {
        return debut != null && fin != null && debut.isBefore(fin);
    }

    public long dureeEnMinutes() {
        return Duration.between(debut, fin).toMinutes();
    }
}
