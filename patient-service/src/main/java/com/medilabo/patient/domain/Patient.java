package com.medilabo.patient.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.*;

import java.time.LocalDate;
import com.medilabo.libs.commons.core.Gender;

@Entity
@Table(name = "patients")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "first_name", nullable = false, length = 100)
    private String prenom;

    @NotBlank
    @Column(name = "last_name", nullable = false, length = 100)
    private String nom;

    @NotNull
    @Past
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateDeNaissance;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, length = 10)
    private Gender genre; // M, F, OTHER

    @Column(name = "address", length = 255)
    private String adressePostale;

    @Column(name = "phone_number", length = 30)
    private String numeroTelephone;
}