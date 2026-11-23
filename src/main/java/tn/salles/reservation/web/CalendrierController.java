package tn.salles.reservation.web;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tn.salles.reservation.service.CalendrierService;

@Controller
public class CalendrierController {

    private final CalendrierService calendrier;

    public CalendrierController(CalendrierService calendrier) {
        this.calendrier = calendrier;
    }

    @GetMapping("/calendrier")
    public String afficher(@RequestParam(required = false) String mois, Model model) {
        YearMonth moisAffiche = lireMois(mois);
        model.addAttribute("mois", moisAffiche.toString());
        model.addAttribute("moisPrecedent", moisAffiche.minusMonths(1).toString());
        model.addAttribute("moisSuivant", moisAffiche.plusMonths(1).toString());
        model.addAttribute("jours", calendrier.afficherMois(moisAffiche));
        return "calendrier/index";
    }

    private static YearMonth lireMois(String mois) {
        if (mois == null) {
            return YearMonth.now();
        }
        try {
            return YearMonth.parse(mois);
        } catch (DateTimeParseException e) {
            return YearMonth.now();
        }
    }
}
