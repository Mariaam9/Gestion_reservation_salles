package tn.salles.reservation.service.impl;

import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.salles.reservation.exception.RegleMetierException;
import tn.salles.reservation.model.Employe;
import tn.salles.reservation.repository.EmployeRepository;
import tn.salles.reservation.service.EmployeService;

@Service
public class EmployeServiceImpl implements EmployeService {

    private static final String EMAIL_DEJA_UTILISE = "Cette adresse e-mail existe déjà.";

    private final EmployeRepository employes;

    public EmployeServiceImpl(EmployeRepository employes) {
        this.employes = employes;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Employe> tous() {
        return employes.findAll();
    }

    @Override
    @Transactional
    public Employe creer(Employe employe) {
        if (employes.existsByEmailIgnoreCase(employe.getEmail())) {
            throw new RegleMetierException(EMAIL_DEJA_UTILISE);
        }
        try {
            return employes.saveAndFlush(employe);
        } catch (DataIntegrityViolationException e) {
            // cas rare : deux créations simultanées avec le même e-mail
            throw new RegleMetierException(EMAIL_DEJA_UTILISE);
        }
    }
}
