package tn.salles.reservation.web;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tn.salles.reservation.exception.RegleMetierException;
import tn.salles.reservation.service.DisponibiliteService;

@Controller
public class DisponibiliteController {

    private static final String FORMAT_DATE_HEURE = "yyyy-MM-dd'T'HH:mm";

    private final DisponibiliteService disponibilites;

    public DisponibiliteController(DisponibiliteService disponibilites) {
        this.disponibilites = disponibilites;
    }

    @GetMapping("/disponibilites")
    public String rechercher(
            @RequestParam(required = false) @DateTimeFormat(pattern = FORMAT_DATE_HEURE) LocalDateTime debut,
            @RequestParam(required = false) @DateTimeFormat(pattern = FORMAT_DATE_HEURE) LocalDateTime fin,
            @RequestParam(defaultValue = "1") int capacite,
            Model model) {
        LocalDate demain = LocalDate.now().plusDays(1);
        model.addAttribute("debut", debut == null ? demain.atTime(9, 0) : debut);
        model.addAttribute("fin", fin == null ? demain.atTime(10, 0) : fin);
        model.addAttribute("capacite", capacite);

        if (debut != null && fin != null) {
            try {
                model.addAttribute("salles", disponibilites.rechercherSallesDisponibles(debut, fin, capacite));
                model.addAttribute("rechercheEffectuee", true);
            } catch (RegleMetierException e) {
                model.addAttribute("erreur", e.getMessage());
            }
        }
        return "disponibilites/index";
    }
}
