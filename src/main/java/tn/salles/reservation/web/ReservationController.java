package tn.salles.reservation.web;

import jakarta.validation.Valid;
import java.time.LocalDate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tn.salles.reservation.exception.RegleMetierException;
import tn.salles.reservation.service.EmployeService;
import tn.salles.reservation.service.ReservationService;
import tn.salles.reservation.service.SalleService;
import tn.salles.reservation.web.form.ReservationForm;

@Controller
@RequestMapping("/reservations")
public class ReservationController {

    private static final String VUE_FORMULAIRE = "reservations/form";
    private static final String REDIRECTION_LISTE = "redirect:/reservations";

    private final ReservationService reservations;
    private final EmployeService employes;
    private final SalleService salles;

    public ReservationController(ReservationService reservations, EmployeService employes, SalleService salles) {
        this.reservations = reservations;
        this.employes = employes;
        this.salles = salles;
    }

    @GetMapping
    public String lister(Model model) {
        model.addAttribute("reservations", reservations.toutes());
        return "reservations/list";
    }

    @GetMapping("/nouvelle")
    public String nouvelle(@RequestParam(required = false) Long salleId, Model model) {
        LocalDate demain = LocalDate.now().plusDays(1);
        ReservationForm form = new ReservationForm();
        form.setSalleId(salleId);
        form.setDateDebut(demain.atTime(9, 0));
        form.setDateFin(demain.atTime(10, 0));
        model.addAttribute("reservationForm", form);
        return afficherFormulaire(null, model);
    }

    @GetMapping("/{id}/modifier")
    public String modifier(@PathVariable Long id, Model model) {
        model.addAttribute("reservationForm", ReservationForm.depuis(reservations.parId(id)));
        return afficherFormulaire(id, model);
    }

    @PostMapping
    public String creer(
            @Valid @ModelAttribute("reservationForm") ReservationForm form,
            BindingResult erreurs,
            Model model,
            RedirectAttributes redirection) {

        if (erreurs.hasErrors()) {
            return afficherFormulaire(null, model);
        }

        try {
            reservations.creer(form.versCommande());
            redirection.addFlashAttribute("succes", "Réservation créée et confirmée.");
            return REDIRECTION_LISTE;
        } catch (RegleMetierException exception) {
            erreurs.reject("regle", exception.getMessage());
            return afficherFormulaire(null, model);
        }
    }

    @PostMapping("/{id}/modifier")
    public String enregistrerModification(
            @PathVariable Long id,
            @Valid @ModelAttribute("reservationForm") ReservationForm form,
            BindingResult erreurs,
            Model model,
            RedirectAttributes redirection) {

        if (erreurs.hasErrors()) {
            return afficherFormulaire(id, model);
        }

        try {
            reservations.modifier(id, form.versCommande());
            redirection.addFlashAttribute("succes", "Réservation modifiée.");
            return REDIRECTION_LISTE;
        } catch (RegleMetierException exception) {
            erreurs.reject("regle", exception.getMessage());
            return afficherFormulaire(id, model);
        }
    }

    @PostMapping("/{id}/annuler")
    public String annuler(@PathVariable Long id, RedirectAttributes redirection) {
        try {
            reservations.annuler(id);
            redirection.addFlashAttribute("succes", "Réservation annulée.");
        } catch (RegleMetierException exception) {
            redirection.addFlashAttribute("erreur", exception.getMessage());
        }

        return REDIRECTION_LISTE;
    }

    private String afficherFormulaire(Long id, Model model) {
        model.addAttribute("id", id);
        model.addAttribute("titre", id == null ? "Nouvelle réservation" : "Modifier la réservation");
        model.addAttribute("salles", salles.toutes());
        model.addAttribute("employes", employes.tous());
        return VUE_FORMULAIRE;
    }
}
