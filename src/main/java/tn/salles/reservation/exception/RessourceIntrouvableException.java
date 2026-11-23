package tn.salles.reservation.exception;

/**
 * Ressource demandée inexistante. Traduite en HTTP 404 par l'API.
 * Sous-type substituable à {@link RegleMetierException} (LSP).
 */
public class RessourceIntrouvableException extends RegleMetierException {

    public RessourceIntrouvableException(String message) {
        super(message);
    }
}
