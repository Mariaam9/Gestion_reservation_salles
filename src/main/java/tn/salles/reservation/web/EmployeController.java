package tn.salles.reservation.web;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import tn.salles.reservation.exception.RegleMetierException;
import tn.salles.reservation.model.Employe;
import tn.salles.reservation.service.EmployeService;

@Controller
@RequestMapping("/employes")
public class EmployeController {

    private static final String VUE_LISTE = "employes/list";

    private final EmployeService employes;

    public EmployeController(EmployeService employes) {
        this.employes = employes;
    }

    @GetMapping
    public String lister(Model model) {
        model.addAttribute("employe", new Employe());
        return afficherListe(model);
    }

    @PostMapping
    public String creer(@Valid @ModelAttribute("employe") Employe employe, BindingResult erreurs, Model model) {
        if (erreurs.hasErrors()) {
            return afficherListe(model);
        }
        try {
            employes.creer(employe);
        } catch (RegleMetierException e) {
            erreurs.rejectValue("email", "email.existe", e.getMessage());
            return afficherListe(model);
        }
        return "redirect:/employes";
    }

    private String afficherListe(Model model) {
        model.addAttribute("employes", employes.tous());
        return VUE_LISTE;
    }
}
