package com.medilabo.assessment.client.dto;

import java.time.LocalDate;



public record PatientDto(
        Long id,
        String prenom,
        String nom,
        LocalDate dateDeNaissance,
        String genre,           // "M", "F" ou "OTHER"
        String adressePostale,
        String numeroTelephone
) {}

