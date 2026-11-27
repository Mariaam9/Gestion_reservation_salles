package tn.salles.reservation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import tn.salles.reservation.model.Salle;
import tn.salles.reservation.model.StatutSalle;
import tn.salles.reservation.service.SalleService;
import tn.salles.reservation.service.commande.SalleCommande;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SalleServiceTest {

    @Autowired
    private SalleService salles;

    @Test
    void creerEnregistreUneNouvelleSalle() {
        SalleCommande commande = new SalleCommande(
                "Salle Clean Code", 8, "Bâtiment A", StatutSalle.DISPONIBLE, null);

        Salle salleCreee = salles.creer(commande);

        assertNotNull(salleCreee.getId());
        assertEquals("Salle Clean Code", salleCreee.getNom());
        assertEquals(8, salleCreee.getCapacite());
    }

    @Test
    void modifierMetAJourUneSalleExistante() {
        Salle salleExistante = salles.creer(new SalleCommande(
                "Salle initiale", 4, "Bâtiment B", StatutSalle.DISPONIBLE, null));
        SalleCommande commandeModifiee = new SalleCommande(
                "Salle modifiée", 10, "Bâtiment C", StatutSalle.DISPONIBLE, null);

        Salle salleModifiee = salles.modifier(salleExistante.getId(), commandeModifiee);

        assertEquals(salleExistante.getId(), salleModifiee.getId());
        assertEquals("Salle modifiée", salleModifiee.getNom());
        assertEquals(10, salleModifiee.getCapacite());
        assertEquals("Bâtiment C", salleModifiee.getLocalisation());
    }
}
