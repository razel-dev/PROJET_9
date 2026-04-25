package com.medilabo.assessment.dto;

import com.medilabo.assessment.model.RiskLevel;
import java.time.LocalDate;

public record AssessmentDto(
        Long patientId,
        String nom,
        String prenom,
        LocalDate dateDeNaissance,
        String genre, // "M", "F" ou "OTHER"
        int age,
        int triggerCount,
        RiskLevel risk
) {}