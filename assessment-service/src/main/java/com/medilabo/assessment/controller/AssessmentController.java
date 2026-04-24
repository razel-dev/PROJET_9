package com.medilabo.assessment.controller;

import com.medilabo.assessment.dto.AssessmentDto;
import com.medilabo.assessment.service.AssessmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/assessments")
@Slf4j
public class AssessmentController {

    private final AssessmentService service;

    /**
     * Récupère l'évaluation (assessment) d'un patient par son identifiant.
     * GET /api/assessments/patient/{patientId}
     *
     * @param patientId identifiant unique du patient
     * @return l'évaluation agrégée du patient (données prêtes à l'exposition API)
     */
    @GetMapping("/patient/{patientId}")
    public AssessmentDto getByPatient(@PathVariable Long patientId) {
        log.info("Demande d'assessment pour patientId={}", patientId);
        AssessmentDto dto = service.assess(patientId);
        log.debug("Assessment calculé pour patientId={} -> risk={}, triggers={}", patientId, dto.risk(), dto.triggerCount());
        return dto;
    }
}