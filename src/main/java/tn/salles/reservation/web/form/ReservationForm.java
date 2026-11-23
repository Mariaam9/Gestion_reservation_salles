package tn.salles.reservation.web.form;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import tn.salles.reservation.model.Reservation;
import tn.salles.reservation.service.commande.ReservationCommande;

/** Objet de formulaire (page Thymeleaf et corps JSON de l'API). */
public class ReservationForm {

    private static final String FORMAT_DATE_HEURE = "yyyy-MM-dd'T'HH:mm";

    @NotNull
    private Long employeId;

    @NotNull
    private Long salleId;

    @NotNull
    @DateTimeFormat(pattern = FORMAT_DATE_HEURE)
    private LocalDateTime dateDebut;

    @NotNull
    @DateTimeFormat(pattern = FORMAT_DATE_HEURE)
    private LocalDateTime dateFin;

    @NotBlank
    @Size(max = 300)
    private String motif;

    @Min(1)
    private int nombreParticipants = 1;

    public static ReservationForm depuis(Reservation reservation) {
        ReservationForm form = new ReservationForm();
        form.employeId = reservation.getEmploye().getId();
        form.salleId = reservation.getCreneau().getSalle().getId();
        form.dateDebut = reservation.getCreneau().getDateDebut();
        form.dateFin = reservation.getCreneau().getDateFin();
        form.motif = reservation.getMotif();
        form.nombreParticipants = reservation.getNombreParticipants();
        return form;
    }

    public ReservationCommande versCommande() {
        return new ReservationCommande(employeId, salleId, dateDebut, dateFin, motif, nombreParticipants);
    }

    public Long getEmployeId() { return employeId; }
    public void setEmployeId(Long employeId) { this.employeId = employeId; }
    public Long getSalleId() { return salleId; }
    public void setSalleId(Long salleId) { this.salleId = salleId; }
    public LocalDateTime getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDateTime dateDebut) { this.dateDebut = dateDebut; }
    public LocalDateTime getDateFin() { return dateFin; }
    public void setDateFin(LocalDateTime dateFin) { this.dateFin = dateFin; }
    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }
    public int getNombreParticipants() { return nombreParticipants; }
    public void setNombreParticipants(int nombreParticipants) { this.nombreParticipants = nombreParticipants; }
}
