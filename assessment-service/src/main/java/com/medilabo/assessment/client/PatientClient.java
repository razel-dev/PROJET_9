package com.medilabo.assessment.client;

import com.medilabo.assessment.client.dto.PatientDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Client OpenFeign pour le service « patient ».
 * <p>
 * Fournit des opérations pour interroger les informations d’un patient via l’API distante.
 * L’URL de base est injectée via la propriété de configuration {@code services.patient.base-url}.
 */
@FeignClient(name = "patient-service", url = "${services.patient.base-url}")
public interface PatientClient {

    /**
     * Récupère un patient par son identifiant.
     * <p>
     * Appel HTTP: {@code GET /api/patients/{id}}.
     *
     * @param id identifiant unique du patient
     * @return {@link PatientDto} représentant le patient
     */
    @GetMapping("/api/patients/{id}")
    PatientDto get(@PathVariable("id") Long id);
}
