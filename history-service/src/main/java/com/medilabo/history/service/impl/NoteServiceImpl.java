package com.medilabo.history.service.impl;

import com.medilabo.history.domain.Note;
import com.medilabo.history.repository.NoteRepository;
import com.medilabo.history.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * Implémentation de l'interface {@link NoteService} fournissant les fonctionnalités
 * de gestion des notes associées aux patients.
 *
 * Ce service prend en charge les opérations de récupération des notes d'un patient
 * par ordre décroissant de leur horodatage de création ainsi que la création de nouvelles notes.
 * L'implémentation effectue des traitements et validations minimaux des attributs de la note
 * avant sa persistance.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NoteServiceImpl implements NoteService {

    /**
     * Dépôt utilisé pour effectuer les opérations CRUD sur les entités {@link Note}.
     *
     * Ce dépôt expose notamment des méthodes permettant d'accéder et de manipuler les notes
     * dans la base de données sous-jacente, y compris des requêtes personnalisées telles que
     * la récupération des notes d'un patient triées par date de création décroissante.
     *
     * Il constitue le point d'accès principal aux opérations de persistance des notes
     * depuis la couche service.
     */
    private final NoteRepository repository;

    /**
     * Récupère la liste des notes associées à un patient donné, triées par date de création
     * décroissante (les plus récentes en premier).
     *
     * @param patientId l'identifiant unique du patient dont on souhaite récupérer les notes
     * @return une liste de {@link Note} pour le patient fourni, triée par date de création décroissante
     */
    @Override
    public List<Note> findByPatientIdDesc(Long patientId) {
        log.debug("Demande de récupération des notes pour le patient {}", patientId);
        List<Note> notes = repository.findByPatientIdOrderByCreatedAtDesc(patientId);
        log.info("Récupération de {} note(s) pour le patient {}", notes.size(), patientId);
        return notes;
    }

    /**
     * Crée une nouvelle note et l'enregistre via le dépôt.
     * La méthode applique des normalisations simples avant la persistance :
     * - l'identifiant est remis à {@code null} pour garantir une insertion (et non une mise à jour) ;
     * - les champs contenu et auteur sont tronqués (suppression des espaces de tête et de fin) ;
     * - si l'auteur est vide après troncature, il est positionné à {@code null} ;
     * - l'horodatage de création est fixé à l'instant courant s'il n'est pas fourni.
     *
     * @param toCreate l'objet {@link Note} à créer et à enregistrer ; ne doit pas être {@code null}
     * @return la {@link Note} persistée, avec les éventuels ajustements appliqués
     * @throws IllegalArgumentException si l'objet {@link Note} fourni est {@code null}
     */
    @Override
    public Note create(Note toCreate) {
        if (toCreate == null) {
            log.warn("Tentative de création d'une note nulle.");
            throw new IllegalArgumentException("La note ne peut pas être nulle.");
        }
        log.debug("Création d'une note pour patient {} par '{}'", toCreate.getPatientId(), toCreate.getAuthor());

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
            log.debug("Horodatage de création défini côté serveur pour le patient {}", toCreate.getPatientId());
        } else {
            log.debug("Horodatage de création fourni conservé pour le patient {}", toCreate.getPatientId());
        }

        log.info("Enregistrement d'une nouvelle note pour patient {} (auteur: {})",
                toCreate.getPatientId(), toCreate.getAuthor());
        Note saved = repository.save(toCreate);
        log.info("Note créée: id={}, patient={}, createdAt={}",
                saved.getId(), saved.getPatientId(), saved.getCreatedAt());
        return saved;
    }
}