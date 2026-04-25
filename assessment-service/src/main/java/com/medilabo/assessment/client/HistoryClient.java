package com.medilabo.assessment.client;

import com.medilabo.assessment.client.dto.NoteDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Client OpenFeign pour le service « history ».
 * <p>
 * Fournit des opérations pour interroger les notes cliniques d’un patient via l’API distante.
 * L’URL de base est injectée via la propriété de configuration {@code services.history.base-url}.
 */
@FeignClient(name = "history-service", url = "${services.history.base-url}")
public interface HistoryClient {

    /**
     * Récupère toutes les notes associées à un patient.
     * <p>
     * Appel HTTP: {@code GET /api/notes/patient/{patientId}}.
     *
     * @param patientId identifiant du patient
     * @return liste (éventuellement vide) de {@link NoteDto} représentant les notes du patient
     */
    @GetMapping("/api/notes/patient/{patientId}")
    List<NoteDto> findByPatient(@PathVariable("patientId") Long patientId);
}
