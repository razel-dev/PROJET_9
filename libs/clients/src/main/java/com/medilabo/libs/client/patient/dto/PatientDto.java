package com.medilabo.libs.client.patient.dto;

import com.medilabo.libs.commons.validation.ValidPhone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;
import com.medilabo.libs.commons.core.Gender;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "id")
public class PatientDto {
    private Long id;
    @NotBlank
    private String prenom;
    @NotBlank
    private String nom;
    @NotNull
    @Past
    private LocalDate dateDeNaissance;
    @NotNull
    private Gender genre;
    private String adressePostale;
    @ValidPhone private String numeroTelephone;
}