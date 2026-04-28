package com.medilabo.front.dto;

public record AssessmentDto(
        Long patientId,
        String nom,
        String prenom,
        java.time.LocalDate dateDeNaissance,
        String genre,
        int age,
        int triggerCount,
        String risk
) {
}
