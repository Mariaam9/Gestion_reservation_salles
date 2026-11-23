package tn.salles.reservation.web;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tn.salles.reservation.exception.RegleMetierException;
import tn.salles.reservation.service.StatistiqueService;

@Controller
public class StatistiqueController {

    private final StatistiqueService statistiques;

    public StatistiqueController(StatistiqueService statistiques) {
        this.statistiques = statistiques;
    }

    @GetMapping("/statistiques")
    public String afficher(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin,
            Model model) {
        LocalDate dateDebut = debut == null ? LocalDate.now().withDayOfMonth(1) : debut;
        LocalDate dateFin = fin == null ? dateDebut.plusMonths(1) : fin;
        model.addAttribute("debut", dateDebut);
        model.addAttribute("fin", dateFin);
        try {
            model.addAttribute("rapports",
                    statistiques.calculerStatistiques(dateDebut.atStartOfDay(), dateFin.atStartOfDay()));
        } catch (RegleMetierException e) {
            model.addAttribute("erreur", e.getMessage());
        }
        return "statistiques/index";
    }
}
