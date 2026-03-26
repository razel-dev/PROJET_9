package com.medilabo.patient.service;

import com.medilabo.patient.domain.Patient;
import com.medilabo.patient.dto.PatientDto;
import com.medilabo.patient.mapper.PatientMapper;
import com.medilabo.patient.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientService {

    private final PatientRepository repository;
    private final PatientMapper mapper;

    @Transactional(readOnly = true)
    public List<Patient> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Patient findById(Long id) {
        return repository.findById(id).orElseThrow(() ->
            new EntityNotFoundException("Patient introuvable id=" + id));
    }

    public Patient create(@Valid Patient p) {
        p.setId(null);
        return repository.save(p);
    }

    public Patient update(Long id, @Valid Patient payload) {
        Patient existing = findById(id);
        existing.setPrenom(payload.getPrenom());
        existing.setNom(payload.getNom());
        existing.setDateDeNaissance(payload.getDateDeNaissance());
        existing.setGenre(payload.getGenre());
        existing.setAdressePostale(payload.getAdressePostale());
        existing.setNumeroTelephone(payload.getNumeroTelephone());
        return repository.save(existing);
    }

    // Mise à jour partielle basée sur MapStruct: n’écrase que les champs non nuls du DTO
    public Patient updatePartial(Long id, PatientDto dto) {
        Patient existing = findById(id);
        mapper.updateEntity(existing, dto); // applique uniquement les valeurs non nulles
        return repository.save(existing);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}