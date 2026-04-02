package com.medilabo.history.service.impl;

import com.medilabo.history.domain.Note;
import com.medilabo.history.repository.NoteRepository;
import com.medilabo.history.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final NoteRepository repository;

    @Override
    public List<Note> findByPatientIdDesc(Long patientId) {
        return repository.findByPatientIdOrderByCreatedAtDesc(patientId);
    }

    @Override
    public Note create(Note toCreate) {
        if (toCreate == null) {
            throw new IllegalArgumentException("La note ne peut pas être nulle.");
        }
        // Défense minimale: éviter toute mise à jour si un id est fourni.
        toCreate.setId(null);

        // nous garantissons un insert (nouvelle note) au lieu
        // d’un update potentiel sur une note existante
        String content = toCreate.getContent();
        if (content != null) {
            toCreate.setContent(content.trim());
        }
        String author = toCreate.getAuthor();
        if (author != null) {
            author = author.trim();
            toCreate.setAuthor(author.isEmpty() ? null : author);
        }

        // createdAt: conserver la valeur fournie pour les imports historiques,
        // sinon fixer la date côté serveur
        if (toCreate.getCreatedAt() == null) {
            toCreate.setCreatedAt(Instant.now());
        }

        return repository.save(toCreate);
    }
}