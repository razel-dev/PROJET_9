package com.medilabo.assessment.service;

import com.medilabo.assessment.client.HistoryClient;
import com.medilabo.assessment.client.PatientClient;
import com.medilabo.assessment.client.dto.PatientDto;
import com.medilabo.assessment.util.TriggerTerms;
import com.medilabo.assessment.dto.AssessmentDto;
import com.medilabo.assessment.model.RiskLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import lombok.extern.slf4j.Slf4j;

/**
 * Service métier chargé de construire l'Assessment d'un patient
 * en agrégeant des données issues des services "patients" et "history",
 * puis en appliquant des règles de risque.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AssessmentService {

    private final PatientClient patients;
    private final HistoryClient history;

    /**
     * Construit l'évaluation d'un patient.
     *
     * Étapes:
     * 1) Récupère les informations du patient
     * 2) Calcule l'âge au jour courant
     * 3) Récupère les notes et compte les "triggers"
     * 4) Évalue le niveau de risque selon l'âge/genre/triggers
     *
     * @param patientId identifiant du patient
     * @return DTO d'évaluation prêt pour exposition API
     */
    public AssessmentDto assess(Long patientId) {
        log.info("Calcul d'assessment: patientId={}", patientId);

        PatientDto p = patients.get(patientId);
        log.debug("Patient récupéré: id={}, genre={}, naissance={}", p.id(), p.genre(), p.dateDeNaissance());

        int age = computeAge(p.dateDeNaissance(), LocalDate.now());
        var notes = history.findByPatient(patientId);
        log.debug("Notes récupérées: count={}", notes.size());

        int triggers = notes.stream()
                .map(n -> n.content() == null ? "" : n.content())
                .map(TriggerTerms::countTriggers)
                .reduce(0, Integer::sum);

        RiskLevel risk = computeRisk(age, p.genre(), triggers);
        log.info("Assessment: patientId={}, age={}, triggers={}, risk={}", patientId, age, triggers, risk);

        return new AssessmentDto(
                p.id(), p.nom(), p.prenom(), p.dateDeNaissance(),
                p.genre(), age, triggers, risk
        );
    }

    /**
     * Calcule l'âge en années complètes, borné à 0 si données incomplètes.
     *
     * @param birth date de naissance (peut être null)
     * @param now   date de référence
     * @return âge en années (>= 0)
     */
    static int computeAge(LocalDate birth, LocalDate now) {
        return birth == null ? 0 : Math.max(0, Period.between(birth, now).getYears());
    }

    /**
     * Évalue le niveau de risque selon l'âge, le genre et le nombre de triggers.
     * Règles:
     * - Si triggers == 0 -> NONE
     * - > 30 ans: 2..5 -> BORDERLINE, 6..7 -> IN_DANGER, >=8 -> EARLY_ONSET
     * - ≤ 30 ans: seuils différenciés par genre (voir implémentation)
     *
     * @param age âge du patient
     * @param genre "M", "F" ou autre/NULL (normalisé en majuscule)
     * @param triggers nombre total de triggers détectés
     * @return niveau de risque
     */
    static RiskLevel computeRisk(int age, String genre, int triggers) {
        String g = genre == null ? "" : genre.toUpperCase();
        boolean male = "M".equals(g);
        boolean female = "F".equals(g);

        if (triggers == 0) return RiskLevel.NONE;

        if (age > 30) {
            if (triggers >= 8) return RiskLevel.EARLY_ONSET;
            if (triggers >= 6) return RiskLevel.IN_DANGER;
            if (triggers >= 2) return RiskLevel.BORDERLINE;
            return RiskLevel.NONE;
        } else {
            if (male) {
                if (triggers >= 5) return RiskLevel.EARLY_ONSET;
                if (triggers >= 3) return RiskLevel.IN_DANGER;
                return RiskLevel.NONE;
            } else if (female) {
                if (triggers >= 7) return RiskLevel.EARLY_ONSET;
                if (triggers >= 4) return RiskLevel.IN_DANGER;
                return RiskLevel.NONE;
            } else {
                // Genre autre/non renseigné < 30 ans: appliquer la règle la plus stricte (côté prudence)
                if (triggers >= 7) return RiskLevel.EARLY_ONSET;
                if (triggers >= 4) return RiskLevel.IN_DANGER;
                return RiskLevel.NONE;
            }
        }
    }
}