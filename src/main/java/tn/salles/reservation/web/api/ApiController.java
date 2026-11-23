package tn.salles.reservation.web.api;

import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tn.salles.reservation.designpatterns.singleton.JournalReservations;
import tn.salles.reservation.designpatterns.singleton.JournalReservations.EntreeJournal;
import tn.salles.reservation.model.Reservation;
import tn.salles.reservation.service.DisponibiliteService;
import tn.salles.reservation.service.ReservationService;
import tn.salles.reservation.service.SalleService;
import tn.salles.reservation.web.api.dto.ReservationDto;
import tn.salles.reservation.web.api.dto.SalleDto;
import tn.salles.reservation.web.form.ReservationForm;

/** API REST consommée par le site Salleo. Ne contient aucune règle métier. */
@RestController
@RequestMapping("/api/v1")
public class ApiController {

    private final SalleService salles;
    private final ReservationService reservations;
    private final DisponibiliteService disponibilites;

    public ApiController(SalleService salles,
                         ReservationService reservations,
                         DisponibiliteService disponibilites) {
        this.salles = salles;
        this.reservations = reservations;
        this.disponibilites = disponibilites;
    }

    @GetMapping("/salles")
    public List<SalleDto> listerSalles() {
        return salles.toutes().stream().map(SalleDto::depuis).toList();
    }

    @GetMapping("/disponibilites")
    public List<SalleDto> rechercherDisponibilites(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin,
            @RequestParam(defaultValue = "1") int capacite) {
        return disponibilites.rechercherSallesDisponibles(debut, fin, capacite).stream()
                .map(SalleDto::depuis)
                .toList();
    }

    @GetMapping("/reservations")
    public List<ReservationDto> listerReservations() {
        return reservations.toutes().stream().map(ReservationDto::depuis).toList();
    }

    @GetMapping("/reservations/{id}")
    public ReservationDto trouverReservation(@PathVariable Long id) {
        return ReservationDto.depuis(reservations.parId(id));
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationDto> creerReservation(@Valid @RequestBody ReservationForm form) {
        Reservation creee = reservations.creer(form.versCommande());
        return ResponseEntity
                .created(URI.create("/api/v1/reservations/" + creee.getId()))
                .body(ReservationDto.depuis(creee));
    }

    @PutMapping("/reservations/{id}")
    public ReservationDto modifierReservation(@PathVariable Long id, @Valid @RequestBody ReservationForm form) {
        return ReservationDto.depuis(reservations.modifier(id, form.versCommande()));
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> annulerReservation(@PathVariable Long id) {
        reservations.annuler(id);
        return ResponseEntity.noContent().build();
    }

    /** Consultation du journal (Singleton) des opérations effectuées. */
    @GetMapping("/journal")
    public List<EntreeJournal> consulterJournal() {
        return JournalReservations.getInstance().consulter();
    }
}
