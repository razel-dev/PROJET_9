package com.medilabo.front.dto;

import java.time.LocalDate;

public record PatientDto(
    Long id,
    String prenom,
    String nom,
    LocalDate dateDeNaissance,
    String genre,
    String adressePostale,
    String numeroTelephone
) {}