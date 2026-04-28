package com.medilabo.history.controller;

import com.medilabo.history.dto.NoteDto;
import com.medilabo.history.mapper.NoteMapper;
import com.medilabo.history.service.NoteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des entités Note.
 *
 * Cette classe expose des points de terminaison pour récupérer et créer des notes.
 * Elle utilise le {@link NoteService} pour effectuer des opérations sur les données
 * sous-jacentes et le {@link NoteMapper} pour mapper entre les représentations entité et DTO.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService service;
    private final NoteMapper mapper;



    /**
     * Récupère la liste des notes associées à un patient spécifique, identifié par son identifiant.
     * Les notes sont renvoyées par ordre décroissant en fonction de leur horodatage de création.
     *
     * @param patientId l'identifiant unique du patient dont on souhaite récupérer les notes
     * @return une liste de {@link NoteDto} contenant les détails des notes du patient
     */
    @GetMapping("/patient/{patientId}")
    public List<NoteDto> listByPatient(@PathVariable Long patientId) {
        return mapper.toDtos(service.findByPatientIdDesc(patientId));
    }

    /**
     * Crée une nouvelle entité Note à partir de la charge utile fournie et renvoie sa représentation DTO correspondante.
     * La charge utile doit être un objet NoteDto valide ; elle sera convertie en entité, persistée, puis remappée en DTO.
     *
     * @param payload l'objet {@link NoteDto} contenant les données de la nouvelle note à créer ;
     *                ne doit pas être nul et doit respecter les contraintes de validation
     * @return l'objet {@link NoteDto} représentant la note nouvellement créée
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NoteDto create(@Valid @RequestBody NoteDto payload) {
        return mapper.toDto(service.create(mapper.toEntity(payload)));
    }
}