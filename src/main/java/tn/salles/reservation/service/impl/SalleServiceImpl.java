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

    private static final String OPERATION_CREATION = "SALLE_CREEE";
    private static final String OPERATION_MODIFICATION = "SALLE_MODIFIEE";

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
    public Salle creer(SalleCommande commande) {
        return enregistrer(new Salle(), commande, OPERATION_CREATION);
    }

    @Override
    @Transactional
    public Salle modifier(Long id, SalleCommande commande) {
        Salle salleExistante = parId(id);
        return enregistrer(salleExistante, commande, OPERATION_MODIFICATION);
    }

    private Salle enregistrer(Salle salle, SalleCommande commande, String operation) {
        appliquerCommande(salle, commande);

        Salle salleEnregistree = salles.save(salle);
        JournalReservations.getInstance().enregistrer(
                operation,
                "Salle " + salleEnregistree.getNom()
        );

        return salleEnregistree;
    }

    private void appliquerCommande(Salle salle, SalleCommande commande) {
        salle.setNom(commande.nom().trim());
        salle.setCapacite(commande.capacite());
        salle.setLocalisation(commande.localisation().trim());
        salle.setStatut(commande.statut());
        salle.setResponsable(trouverResponsable(commande.responsableId()));
    }

    private Responsable trouverResponsable(Long responsableId) {
        if (responsableId == null) {
            return null;
        }

        return responsables.findById(responsableId)
                .orElseThrow(() -> new RessourceIntrouvableException("Responsable introuvable."));
    }
}
