package tn.esprit.tpfoyer.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.tpfoyer.domain.Employe;
import tn.esprit.tpfoyer.exception.ResourceNotFoundException;
import tn.esprit.tpfoyer.repository.IEmployeRepository;
import tn.esprit.tpfoyer.service.IEmployeService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeServiceImpl implements IEmployeService {

    private final IEmployeRepository employeRepository;

    @Override
    public Employe create(Employe employe) {
        if (employe.getIdEmploye() != null) {
            throw new IllegalArgumentException("Un nouvel employé ne doit pas avoir d'identifiant");
        }
        verifier(employe);
        return employeRepository.save(employe);
    }

    @Override
    public Employe findById(Long id) {
        return employeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employé", id));
    }

    @Override
    public List<Employe> findAll() {
        return employeRepository.findAll();
    }

    @Override
    public Employe update(Long id, Employe employe) {
        Employe existant = findById(id);
        verifier(employe);
        existant.setNom(employe.getNom());
        existant.setPrenom(employe.getPrenom());
        existant.setRole(employe.getRole());
        existant.setAgence(employe.getAgence());
        return employeRepository.save(existant);
    }

    @Override
    public void deleteById(Long id) {
        if (!employeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employé", id);
        }
        employeRepository.deleteById(id);
    }

    private void verifier(Employe employe) {
        if (employe.getNom() == null || employe.getNom().isBlank()
                || employe.getPrenom() == null || employe.getPrenom().isBlank()) {
            throw new IllegalArgumentException("Le nom et le prénom de l'employé sont obligatoires");
        }
    }
}