package tn.salles.reservation.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tn.salles.reservation.exception.RegleMetierException;
import tn.salles.reservation.service.ResponsableService;

@Controller
@RequestMapping("/responsables")
public class ResponsableController {

    private final ResponsableService responsables;

    public ResponsableController(ResponsableService responsables) {
        this.responsables = responsables;
    }

    @GetMapping
    public String lister(Model model) {
        model.addAttribute("responsables", responsables.tous());
        return "responsables/list";
    }

    @PostMapping
    public String creer(@RequestParam String nom) {
        try {
            responsables.creer(nom);
        } catch (RegleMetierException ignoree) {
            // nom vide : on revient simplement à la liste, comme auparavant
        }
        return "redirect:/responsables";
    }
}
