package com.medilabo.patient.controller;

import com.medilabo.patient.dto.PatientDto;
import com.medilabo.patient.mapper.PatientMapper;
import com.medilabo.patient.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour gérer les opérations CRUD sur l'entité Patient.
 * Ce contrôleur expose des endpoints pour :
 * - lister tous les patients,
 * - récupérer un patient par son identifiant,
 * - créer un nouveau patient,
 * - mettre à jour intégralement un patient existant,
 * - mettre à jour partiellement les informations d'un patient,
 * - supprimer un patient.
 *
 * Le contrôleur s'appuie sur {@link PatientService} pour la logique métier
 * et {@link PatientMapper} pour la conversion entité ↔ DTO.
 *
 * Mappings:
 * - GET/api/patients : Récupère la liste de tous les patients.
 * - GET/api/patients/{id} : Récupère le détail d'un patient par identifiant.
 * - POST/api/patients : Crée un nouveau patient.
 * - PUT /api/patients/{id} : Met à jour intégralement un patient existant.
 * - PATCH /api/patients/{id} : Met à jour partiellement un patient (ignore les champs nuls).
 * - DELETE /api/patients/{id} : Supprime un patient par identifiant.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService service;
    private final PatientMapper mapper;

    /**
     * Récupère la liste de tous les patients.
     *
     * @return une liste de {@link PatientDto} représentant tous les patients du système
     */
    @GetMapping
    public List<PatientDto> list() {

        return mapper.toDtos(service.findAll());
    }


    /**
     * Récupère un patient à partir de son identifiant unique.
     *
     * @param id l'identifiant unique du patient à récupérer
     * @return un {@link PatientDto} contenant les informations du patient
     */
    @GetMapping("/{id}")
    public PatientDto get(@PathVariable Long id) {
        return mapper.toDto(service.findById(id));
    }


    /**
     * Crée un nouveau patient dans le système et retourne les informations du patient créé.
     *
     * @param payload l'objet {@link PatientDto} contenant les informations du patient à créer
     * @return un {@link PatientDto} représentant le patient nouvellement créé
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PatientDto create(@Valid @RequestBody PatientDto payload) {
        return mapper.toDto(service.create(mapper.toEntity(payload)));
    }


    /**
     * Met à jour intégralement un patient existant avec les informations fournies.
     *
     * @param id l'identifiant unique du patient à mettre à jour
     * @param payload l'objet {@link PatientDto} contenant les nouvelles informations du patient
     * @return un {@link PatientDto} représentant le patient mis à jour
     */
    @PutMapping("/{id}")
    public PatientDto update(@PathVariable Long id, @Valid @RequestBody PatientDto payload) {
        return mapper.toDto(service.update(id, mapper.toEntity(payload)));
    }


    /**
     * Met à jour partiellement les champs d'un patient existant identifié par son identifiant.
     * Seuls les champs non nuls du {@link PatientDto} fourni seront appliqués.
     *
     * @param id l'identifiant unique du patient à mettre à jour
     * @param payload l'objet {@link PatientDto} contenant les champs à mettre à jour
     * @return un {@link PatientDto} représentant le patient mis à jour
     */

    @PatchMapping("/{id}")
    public PatientDto patch(@PathVariable Long id, @RequestBody PatientDto payload) {

        return mapper.toDto(service.updatePartial(id, payload));
    }


    /**
     * Supprime un patient du système à partir de son identifiant unique.
     * Cette opération retire définitivement l'enregistrement.
     *
     * @param id l'identifiant unique du patient à supprimer
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}