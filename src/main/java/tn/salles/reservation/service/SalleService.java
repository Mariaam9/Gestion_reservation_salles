package tn.salles.reservation.service;

import java.util.List;
import tn.salles.reservation.model.Salle;
import tn.salles.reservation.service.commande.SalleCommande;

/** Cas d'utilisation disponibles pour la gestion des salles. */
public interface SalleService {

    List<Salle> toutes();

    Salle parId(Long id);

    Salle creer(SalleCommande commande);

    Salle modifier(Long id, SalleCommande commande);
}
