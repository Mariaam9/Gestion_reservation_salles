package tn.salles.reservation.service.commande;

import tn.salles.reservation.model.StatutSalle;

/** Données d'entrée d'une création / modification de salle. */
public record SalleCommande(
        String nom,
        int capacite,
        String localisation,
        StatutSalle statut,
        Long responsableId) {
}
