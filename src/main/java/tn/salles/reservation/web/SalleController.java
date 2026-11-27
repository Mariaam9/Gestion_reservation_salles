package tn.salles.reservation.web;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import tn.salles.reservation.exception.RegleMetierException;
import tn.salles.reservation.model.StatutSalle;
import tn.salles.reservation.service.ResponsableService;
import tn.salles.reservation.service.SalleService;
import tn.salles.reservation.web.form.SalleForm;

@Controller
@RequestMapping("/salles")
public class SalleController {

    private static final String VUE_FORMULAIRE = "salles/form";
    private static final String REDIRECTION_LISTE = "redirect:/salles";

    private final SalleService salles;
    private final ResponsableService responsables;

    public SalleController(SalleService salles, ResponsableService responsables) {
        this.salles = salles;
        this.responsables = responsables;
    }

    @GetMapping
    public String lister(Model model) {
        model.addAttribute("salles", salles.toutes());
        return "salles/list";
    }

    @GetMapping("/nouvelle")
    public String nouvelle(Model model) {
        model.addAttribute("salleForm", new SalleForm());
        return afficherFormulaire(null, model);
    }

    @GetMapping("/{id}/modifier")
    public String modifier(@PathVariable Long id, Model model) {
        model.addAttribute("salleForm", SalleForm.depuis(salles.parId(id)));
        return afficherFormulaire(id, model);
    }

    @PostMapping
    public String creer(
            @Valid @ModelAttribute("salleForm") SalleForm form,
            BindingResult erreurs,
            Model model) {

        if (erreurs.hasErrors()) {
            return afficherFormulaire(null, model);
        }

        try {
            salles.creer(form.versCommande());
            return REDIRECTION_LISTE;
        } catch (RegleMetierException exception) {
            erreurs.reject("regle", exception.getMessage());
            return afficherFormulaire(null, model);
        }
    }

    @PostMapping("/{id}/modifier")
    public String enregistrerModification(
            @PathVariable Long id,
            @Valid @ModelAttribute("salleForm") SalleForm form,
            BindingResult erreurs,
            Model model) {

        if (erreurs.hasErrors()) {
            return afficherFormulaire(id, model);
        }

        try {
            salles.modifier(id, form.versCommande());
            return REDIRECTION_LISTE;
        } catch (RegleMetierException exception) {
            erreurs.reject("regle", exception.getMessage());
            return afficherFormulaire(id, model);
        }
    }

    private String afficherFormulaire(Long id, Model model) {
        model.addAttribute("id", id);
        model.addAttribute("titre", id == null ? "Ajouter une salle" : "Modifier une salle");
        model.addAttribute("responsables", responsables.tous());
        model.addAttribute("statuts", StatutSalle.values());
        return VUE_FORMULAIRE;
    }
}
