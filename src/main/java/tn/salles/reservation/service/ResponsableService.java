package tn.salles.reservation.service;

import java.util.List;
import tn.salles.reservation.model.Responsable;

public interface ResponsableService {

    List<Responsable> tous();

    Responsable creer(String nom);
}
