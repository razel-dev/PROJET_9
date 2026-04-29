package com.medilabo.assessment.service;

import com.medilabo.assessment.client.HistoryClient;
import com.medilabo.assessment.client.PatientClient;
import com.medilabo.assessment.client.dto.PatientDto;
import com.medilabo.assessment.dto.AssessmentDto;
import com.medilabo.assessment.model.RiskLevel;
import com.medilabo.assessment.util.TriggerTerms;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssessmentService {

    private final PatientClient patients;
    private final HistoryClient history;

    public AssessmentDto assess(Long patientId) {
        log.info("Calcul d'assessment: patientId={}", patientId);

        PatientDto patient = patients.get(patientId);
        log.debug("Patient recupere: id={}, genre={}, naissance={}",
                patient.id(), patient.genre(), patient.dateDeNaissance());

        int age = computeAge(patient.dateDeNaissance(), LocalDate.now());
        var notes = history.findByPatient(patientId);
        log.debug("Notes recuperees: count={}", notes.size());

        String latestNoteContent = notes.stream()
                .findFirst()
                .map(note -> note.content() == null ? "" : note.content())
                .orElse("");

        int triggers = TriggerTerms.countTriggers(latestNoteContent);
        RiskLevel risk = computeRisk(age, patient.genre(), triggers);
        log.info("Assessment: patientId={}, age={}, triggers={}, risk={}", patientId, age, triggers, risk);

        return new AssessmentDto(
                patient.id(),
                patient.nom(),
                patient.prenom(),
                patient.dateDeNaissance(),
                patient.genre(),
                age,
                triggers,
                risk
        );
    }

    static int computeAge(LocalDate birth, LocalDate now) {
        return birth == null ? 0 : Math.max(0, Period.between(birth, now).getYears());
    }

    static RiskLevel computeRisk(int age, String genre, int triggers) {
        String g = genre == null ? "" : genre.toUpperCase();
        boolean male = "M".equals(g);
        boolean female = "F".equals(g);

        if (triggers == 0) {
            return RiskLevel.NONE;
        }

        if (age > 30) {
            if (triggers >= 8) return RiskLevel.EARLY_ONSET;
            if (triggers >= 6) return RiskLevel.IN_DANGER;
            if (triggers >= 2) return RiskLevel.BORDERLINE;
            return RiskLevel.NONE;
        }

        if (male) {
            if (triggers >= 5) return RiskLevel.EARLY_ONSET;
            if (triggers >= 3) return RiskLevel.IN_DANGER;
            return RiskLevel.NONE;
        }

        if (female) {
            if (triggers >= 7) return RiskLevel.EARLY_ONSET;
            if (triggers >= 4) return RiskLevel.IN_DANGER;
            return RiskLevel.NONE;
        }

        if (triggers >= 7) return RiskLevel.EARLY_ONSET;
        if (triggers >= 4) return RiskLevel.IN_DANGER;
        return RiskLevel.NONE;
    }
}
