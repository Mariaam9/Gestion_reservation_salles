package tn.salles.reservation.designpatterns.singleton;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <b>Design pattern Singleton</b> (cours MASI-03).
 * <p>
 * Journal unique des opérations effectuées sur les réservations et les salles
 * (création, modification, annulation). Il doit exister une seule instance
 * pour que toutes les opérations soient tracées au même endroit et dans l'ordre.
 * <ul>
 *   <li>constructeur privé : instanciation externe impossible ;</li>
 *   <li>{@code getInstance()} statique et {@code synchronized} : point d'accès unique,
 *       sûr en environnement multi-thread (un serveur web traite plusieurs requêtes en parallèle).</li>
 * </ul>
 */
public final class JournalReservations {

    private static JournalReservations instance;

    private final List<EntreeJournal> entrees = new ArrayList<>();

    private JournalReservations() {
    }

    public static synchronized JournalReservations getInstance() {
        if (instance == null) {
            instance = new JournalReservations();
        }
        return instance;
    }

    public synchronized void enregistrer(String operation, String details) {
        entrees.add(new EntreeJournal(LocalDateTime.now(), operation, details));
    }

    /** Copie défensive : le journal ne peut pas être modifié depuis l'extérieur. */
    public synchronized List<EntreeJournal> consulter() {
        return List.copyOf(entrees);
    }

    public record EntreeJournal(LocalDateTime horodatage, String operation, String details) {
    }
}
