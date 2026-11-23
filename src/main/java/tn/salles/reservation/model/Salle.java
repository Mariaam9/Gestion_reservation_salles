package tn.salles.reservation.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "salles")
public class Salle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String nom;

    @Min(1)
    @Column(nullable = false)
    private int capacite;

    @NotBlank
    private String localisation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutSalle statut = StatutSalle.DISPONIBLE;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "responsable_id")
    private Responsable responsable;

    public Salle() {
    }

    public Salle(String nom, int capacite, String localisation, StatutSalle statut, Responsable responsable) {
        this.nom = nom;
        this.capacite = capacite;
        this.localisation = localisation;
        this.statut = statut;
        this.responsable = responsable;
    }

    public boolean estDisponible() {
        return statut == StatutSalle.DISPONIBLE;
    }

    public boolean peutAccueillir(int nombreParticipants) {
        return nombreParticipants >= 1 && nombreParticipants <= capacite;
    }

    public Long getId() { return id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public int getCapacite() { return capacite; }
    public void setCapacite(int capacite) { this.capacite = capacite; }
    public String getLocalisation() { return localisation; }
    public void setLocalisation(String localisation) { this.localisation = localisation; }
    public StatutSalle getStatut() { return statut; }
    public void setStatut(StatutSalle statut) { this.statut = statut; }
    public Responsable getResponsable() { return responsable; }
    public void setResponsable(Responsable responsable) { this.responsable = responsable; }
}
