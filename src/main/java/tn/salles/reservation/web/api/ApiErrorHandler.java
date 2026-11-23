package tn.salles.reservation.web.api;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tn.salles.reservation.exception.RegleMetierException;
import tn.salles.reservation.exception.RessourceIntrouvableException;

/** Traduit les exceptions en réponses HTTP lisibles par le site (format {"erreur": "..."}). */
@RestControllerAdvice(basePackages = "tn.salles.reservation.web.api")
public class ApiErrorHandler {

    @ExceptionHandler(RessourceIntrouvableException.class)
    public ResponseEntity<Map<String, String>> ressourceIntrouvable(RessourceIntrouvableException e) {
        return erreur(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(RegleMetierException.class)
    public ResponseEntity<Map<String, String>> regleMetier(RegleMetierException e) {
        return erreur(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> donneesInvalides(MethodArgumentNotValidException e) {
        return erreur(HttpStatus.BAD_REQUEST,
                "Données invalides : vérifier dates, salle, employé, motif et participants.");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> jsonIllisible(HttpMessageNotReadableException e) {
        return erreur(HttpStatus.BAD_REQUEST, "JSON invalide ou date mal formatée.");
    }

    private static ResponseEntity<Map<String, String>> erreur(HttpStatus statut, String message) {
        return ResponseEntity.status(statut).body(Map.of("erreur", message));
    }
}
