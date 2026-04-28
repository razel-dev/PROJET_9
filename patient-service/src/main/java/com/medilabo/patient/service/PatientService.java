package com.medilabo.patient.service;

import com.medilabo.patient.domain.Patient;
import com.medilabo.patient.dto.PatientDto;
import com.medilabo.patient.mapper.PatientMapper;
import com.medilabo.patient.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service métier pour la gestion des patients.
 * <p>
 * Ce service centralise les opérations CRUD sur l'entité {@link Patient} et
 * s'appuie sur {@link PatientRepository} pour la persistance ainsi que sur
 * {@link PatientMapper} pour les opérations de mise à jour partielle à partir d'un {@link PatientDto}.
 * <p>
 * La classe est annotée avec {@link Transactional} afin de garantir l'intégrité des opérations.
 * Les méthodes de lecture utilisent un contexte transactionnel en lecture seule.
 */

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PatientService {

    private final PatientRepository repository;
    private final PatientMapper mapper;

    /**
     * Récupère l'ensemble des patients.
     *
     * <p>Transaction: en lecture seule.</p>
     *
     * @return la liste de tous les patients persistés (liste vide si aucun)
     */
    @Transactional(readOnly = true)
    public List<Patient> findAll() {
        log.debug("Recherche de tous les patients");
        return repository.findAll();
    }

    /**
     * Recherche un patient par identifiant.
     *
     * <p>Transaction: en lecture seule.</p>
     *
     * @param id l'identifiant unique du patient
     * @return le patient correspondant
     * @throws EntityNotFoundException si aucun patient ne correspond à l'identifiant fourni
     */
    @Transactional(readOnly = true)
    public Patient findById(Long id) {
        log.debug("Recherche du patient id={}", id);
        return repository.findById(id).orElseThrow(() -> {
            log.warn("Patient introuvable id={}", id);
            return new EntityNotFoundException("Patient introuvable id=" + id);
        });
    }

    /**
     * Crée un nouveau patient.
     * <p>
     * L'identifiant du patient fourni est remis à {@code null} avant persistance pour
     * forcer la création d'un nouvel enregistrement.
     *
     * @param p le patient à créer (validé)
     * @return le patient persistant créé (avec identifiant renseigné)
     */
    public Patient create(@Valid Patient p) {
        log.info("Création d'un nouveau patient");
        p.setId(null);
        Patient saved = repository.save(p);
        log.info("Patient créé id={}", saved.getId());
        return saved;
    }

    /**
     * Met à jour intégralement un patient existant.
     * <p>
     * Toutes les propriétés gérées sont remplacées par celles du {@code payload}.
     *
     * @param id      l'identifiant du patient à mettre à jour
     * @param payload les nouvelles valeurs du patient (validées)
     * @return le patient mis à jour et persistant
     * @throws EntityNotFoundException si le patient n'existe pas
     */
    public Patient update(Long id, @Valid Patient payload) {
        log.info("Mise à jour complète du patient id={}", id);
        Patient existing = findById(id);
        existing.setPrenom(payload.getPrenom());
        existing.setNom(payload.getNom());
        existing.setDateDeNaissance(payload.getDateDeNaissance());
        existing.setGenre(payload.getGenre());
        existing.setAdressePostale(payload.getAdressePostale());
        existing.setNumeroTelephone(payload.getNumeroTelephone());
        Patient saved = repository.save(existing);
        log.info("Patient mis à jour (complet) id={}", saved.getId());
        return saved;
    }

    /**
     * Met à jour partiellement un patient existant à partir d'un DTO.
     * <p>
     * Seuls les champs non nuls du {@code dto} sont appliqués (comportement délégué au mapper).
     *
     * @param id  l'identifiant du patient à mettre à jour
     * @param dto le DTO contenant les champs à appliquer
     * @return le patient mis à jour et persistant
     * @throws EntityNotFoundException si le patient n'existe pas
     */

    public Patient updatePartial(Long id, PatientDto dto) {
        log.info("Mise à jour partielle du patient id={}", id);
        Patient existing = findById(id);
        mapper.updateEntity(existing, dto);
        Patient saved = repository.save(existing);
        log.info("Patient mis à jour (partiel) id={}", saved.getId());
        return saved;
    }

    /**
     * Supprime un patient par identifiant.
     *
     * @param id l'identifiant du patient à supprimer
     */
    public void delete(Long id) {
        log.info("Suppression du patient id={}", id);
        repository.deleteById(id);
        log.info("Patient supprimé id={}", id);
    }
}