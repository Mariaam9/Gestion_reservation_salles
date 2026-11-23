package tn.salles.reservation.exception;

/**
 * Violation d'une règle métier (conflit, capacité, période invalide...).
 * Traduite en HTTP 409 par l'API.
 */
public class RegleMetierException extends RuntimeException {

    public RegleMetierException(String message) {
        super(message);
    }
}
