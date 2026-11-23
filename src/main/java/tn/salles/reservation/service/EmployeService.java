package tn.salles.reservation.service;

import java.util.List;
import tn.salles.reservation.model.Employe;

public interface EmployeService {

    List<Employe> tous();

    /** @throws tn.salles.reservation.exception.RegleMetierException si l'e-mail est déjà utilisé */
    Employe creer(Employe employe);
}
