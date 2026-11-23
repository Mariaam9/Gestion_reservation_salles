package tn.salles.reservation.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** Redirige vers le site Salleo (les statistiques du tableau de bord sont chargées via l'API). */
@Controller
public class HomeController {

    @GetMapping("/")
    public String accueil() {
        return "redirect:/site/index.html";
    }
}
