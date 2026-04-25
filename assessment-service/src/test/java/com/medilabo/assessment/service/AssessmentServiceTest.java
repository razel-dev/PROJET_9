package com.medilabo.assessment.service;

import com.medilabo.assessment.model.RiskLevel;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class AssessmentServiceTest {

    @ParameterizedTest(name = "[{index}] age={0}, genre={1}, triggers={2} -> {3}")
    @CsvSource({
            // NONE si triggers = 0
            "40, M, 0, NONE",

            // BORDERLINE si patient de plus de 30 ans avec 2 déclencheurs
            "35, M, 2, BORDERLINE",

            // BORDERLINE si patient de plus de 30 ans avec 5 déclencheurs
            "50, F, 5, BORDERLINE",

            // IN_DANGER pour homme de moins de 30 ans avec 3 déclencheurs
            "25, M, 3, IN_DANGER",

            // EARLY_ONSET pour homme de moins de 30 ans avec 5 déclencheurs
            "29, M, 5, EARLY_ONSET",

            // IN_DANGER pour femme de moins de 30 ans avec 4 déclencheurs
            "24, F, 4, IN_DANGER",

            // EARLY_ONSET pour femme de moins de 30 ans avec 7 déclencheurs
            "19, F, 7, EARLY_ONSET",

            // IN_DANGER pour patient de plus de 30 ans avec 6 déclencheurs
            "45, M, 6, IN_DANGER",

            // EARLY_ONSET pour patient de plus de 30 ans avec 8 déclencheurs
            "60, F, 8, EARLY_ONSET"
    })
    void computeRisk(int age, String genre, int triggers, RiskLevel expected) {
        assertEquals(expected, AssessmentService.computeRisk(age, genre, triggers));
    }
}