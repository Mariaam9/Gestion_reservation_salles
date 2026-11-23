package tn.salles.reservation.service;

import java.util.List;
import tn.salles.reservation.model.Salle;
import tn.salles.reservation.service.commande.SalleCommande;

public interface SalleService {

    List<Salle> toutes();

    Salle parId(Long id);

    /** Crée la salle si {@code id} est {@code null}, sinon la met à jour. */
    Salle enregistrer(Long id, SalleCommande commande);
}
