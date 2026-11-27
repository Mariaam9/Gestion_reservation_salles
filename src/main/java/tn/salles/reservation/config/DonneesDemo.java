package tn.salles.reservation.config;

import java.time.LocalDate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import tn.salles.reservation.designpatterns.factory.ReservationFactory;
import tn.salles.reservation.model.Administrateur;
import tn.salles.reservation.model.Employe;
import tn.salles.reservation.model.Responsable;
import tn.salles.reservation.model.Salle;
import tn.salles.reservation.model.StatutSalle;
import tn.salles.reservation.repository.AdministrateurRepository;
import tn.salles.reservation.repository.EmployeRepository;
import tn.salles.reservation.repository.ReservationRepository;
import tn.salles.reservation.repository.ResponsableRepository;
import tn.salles.reservation.repository.SalleRepository;
import tn.salles.reservation.service.commande.ReservationCommande;

/** Jeu de données de démonstration chargé uniquement avec le profil {@code dev}. */
@Configuration
@Profile("dev")
public class DonneesDemo {

    @Bean
    CommandLineRunner initialiser(EmployeRepository employes,
                                  ResponsableRepository responsables,
                                  SalleRepository salles,
                                  ReservationRepository reservations,
                                  AdministrateurRepository administrateurs,
                                  ReservationFactory fabrique) {
        return args -> {
            if (salles.count() > 0) {
                return;
            }
            Responsable nadia = responsables.save(new Responsable("Nadia Mansour"));
            Salle atlas = salles.save(new Salle("Atlas", 12, "Bâtiment A · étage 1", StatutSalle.DISPONIBLE, nadia));
            Salle carthage = salles.save(new Salle("Carthage", 6, "Bâtiment B · étage 2", StatutSalle.DISPONIBLE, nadia));
            salles.save(new Salle("Sfax", 20, "Bâtiment C · RDC", StatutSalle.DISPONIBLE, null));
            salles.save(new Salle("Studio", 4, "Bâtiment A · étage 2", StatutSalle.MAINTENANCE, nadia));

            Employe sami = employes.save(new Employe("Sami Trabelsi", "sami@example.com"));
            Employe amira = employes.save(new Employe("Amira Ben Salah", "amira@example.com"));
            employes.save(new Employe("Youssef Haddad", "youssef@example.com"));
            employes.save(new Employe("Nadia Mansour", "responsable@example.com"));
            administrateurs.save(new Administrateur("Administration", "admin@example.com"));

            LocalDate demain = LocalDate.now().plusDays(1);
            ReservationCommande reunionEquipe = new ReservationCommande(
                    sami.getId(), atlas.getId(),
                    demain.atTime(9, 0), demain.atTime(10, 30),
                    "Réunion équipe produit", 6);
            ReservationCommande pointAvancement = new ReservationCommande(
                    amira.getId(), carthage.getId(),
                    demain.atTime(14, 0), demain.atTime(15, 0),
                    "Point d'avancement", 4);

            reservations.save(fabrique.creerReservationConfirmee(sami, atlas, reunionEquipe));
            reservations.save(fabrique.creerReservationConfirmee(amira, carthage, pointAvancement));
        };
    }
}
