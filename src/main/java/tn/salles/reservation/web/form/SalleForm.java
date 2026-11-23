package tn.salles.reservation.web.form;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import tn.salles.reservation.model.Salle;
import tn.salles.reservation.model.StatutSalle;
import tn.salles.reservation.service.commande.SalleCommande;

public class SalleForm {

    @NotBlank
    private String nom;

    @Min(1)
    private int capacite = 2;

    @NotBlank
    private String localisation;

    @NotNull
    private StatutSalle statut = StatutSalle.DISPONIBLE;

    private Long responsableId;

    public static SalleForm depuis(Salle salle) {
        SalleForm form = new SalleForm();
        form.nom = salle.getNom();
        form.capacite = salle.getCapacite();
        form.localisation = salle.getLocalisation();
        form.statut = salle.getStatut();
        form.responsableId = salle.getResponsable() == null ? null : salle.getResponsable().getId();
        return form;
    }

    public SalleCommande versCommande() {
        return new SalleCommande(nom, capacite, localisation, statut, responsableId);
    }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public int getCapacite() { return capacite; }
    public void setCapacite(int capacite) { this.capacite = capacite; }
    public String getLocalisation() { return localisation; }
    public void setLocalisation(String localisation) { this.localisation = localisation; }
    public StatutSalle getStatut() { return statut; }
    public void setStatut(StatutSalle statut) { this.statut = statut; }
    public Long getResponsableId() { return responsableId; }
    public void setResponsableId(Long responsableId) { this.responsableId = responsableId; }
}
