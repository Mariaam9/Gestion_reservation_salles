package tn.salles.reservation.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.salles.reservation.exception.RegleMetierException;
import tn.salles.reservation.model.Responsable;
import tn.salles.reservation.repository.ResponsableRepository;
import tn.salles.reservation.service.ResponsableService;

@Service
public class ResponsableServiceImpl implements ResponsableService {

    private final ResponsableRepository responsables;

    public ResponsableServiceImpl(ResponsableRepository responsables) {
        this.responsables = responsables;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Responsable> tous() {
        return responsables.findAll();
    }

    @Override
    @Transactional
    public Responsable creer(String nom) {
        if (nom == null || nom.isBlank()) {
            throw new RegleMetierException("Le nom du responsable est obligatoire.");
        }
        return responsables.save(new Responsable(nom.trim()));
    }
}
