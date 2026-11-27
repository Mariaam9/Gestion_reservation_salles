package tn.salles.reservation.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tn.salles.reservation.exception.RegleMetierException;
import tn.salles.reservation.service.ResponsableService;

@Controller
@RequestMapping("/responsables")
public class ResponsableController {

    private static final String VUE_LISTE = "responsables/list";
    private static final String REDIRECTION_LISTE = "redirect:/responsables";

    private final ResponsableService responsables;

    public ResponsableController(ResponsableService responsables) {
        this.responsables = responsables;
    }

    @GetMapping
    public String lister(Model model) {
        model.addAttribute("responsables", responsables.tous());
        return VUE_LISTE;
    }

    @PostMapping
    public String creer(
            @RequestParam String nom,
            RedirectAttributes redirection) {
        try {
            responsables.creer(nom);
            redirection.addFlashAttribute("succes", "Responsable créé avec succès.");
        } catch (RegleMetierException exception) {
            redirection.addFlashAttribute("erreur", exception.getMessage());
        }

        return REDIRECTION_LISTE;
    }
}
