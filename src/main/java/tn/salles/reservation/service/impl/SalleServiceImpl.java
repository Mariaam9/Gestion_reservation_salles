package tn.salles.reservation.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.salles.reservation.designpatterns.singleton.JournalReservations;
import tn.salles.reservation.exception.RessourceIntrouvableException;
import tn.salles.reservation.model.Responsable;
import tn.salles.reservation.model.Salle;
import tn.salles.reservation.repository.ResponsableRepository;
import tn.salles.reservation.repository.SalleRepository;
import tn.salles.reservation.service.SalleService;
import tn.salles.reservation.service.commande.SalleCommande;

@Service
public class SalleServiceImpl implements SalleService {

    private final SalleRepository salles;
    private final ResponsableRepository responsables;

    public SalleServiceImpl(SalleRepository salles, ResponsableRepository responsables) {
        this.salles = salles;
        this.responsables = responsables;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Salle> toutes() {
        return salles.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Salle parId(Long id) {
        return salles.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Salle introuvable."));
    }

    @Override
    @Transactional
    public Salle enregistrer(Long id, SalleCommande commande) {
        boolean creation = id == null;
        Salle salle = creation ? new Salle() : parId(id);
        salle.setNom(commande.nom().trim());
        salle.setCapacite(commande.capacite());
        salle.setLocalisation(commande.localisation().trim());
        salle.setStatut(commande.statut());
        salle.setResponsable(trouverResponsable(commande.responsableId()));

        Salle enregistree = salles.save(salle);
        JournalReservations.getInstance().enregistrer(
                creation ? "SALLE_CREEE" : "SALLE_MODIFIEE", "Salle " + enregistree.getNom());
        return enregistree;
    }

    private Responsable trouverResponsable(Long responsableId) {
        if (responsableId == null) {
            return null;
        }
        return responsables.findById(responsableId)
                .orElseThrow(() -> new RessourceIntrouvableException("Responsable introuvable."));
    }
}
