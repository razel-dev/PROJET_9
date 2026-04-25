package com.medilabo.assessment.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class TriggerTermsTest {

    @ParameterizedTest(name = "[{index}] détecte terme simple: ''{0}'' -> 1")
    @DisplayName("Détection de termes simples/variations (accents, casse)")
    @ValueSource(strings = {
            // détecte un terme simple : Poids
            "Poids",
            // détecte les accents : Hémoglobine A1C
            "Hémoglobine A1C",
            // détecte sans accents : hemoglobine a1c
            "hemoglobine a1c",
            // détecte indépendamment de la casse : CHOLESTÉROL
            "CHOLESTÉROL"
    })
    void countTriggers_detects_simple_and_normalized(String note) {
        assertEquals(1, TriggerTerms.countTriggers(note));
    }

    @Test
    @DisplayName("Compte plusieurs déclencheurs distincts dans la même note")
    void countTriggers_multiple_distinct_terms_in_one_note() {
        String note = "Le patient présente un Poids élevé, une microalbumine positive et un taux de cholestérol.";
        // termes attendus: "poids", "microalbumine", "cholesterol" -> 3
        assertEquals(3, TriggerTerms.countTriggers(note));
    }

    @Test
    @DisplayName("Retourne 0 si aucun terme n’est présent")
    void countTriggers_none_present_returns_zero() {
        String note = "Aucun indicateur pertinent relevé dans cette observation.";
        assertEquals(0, TriggerTerms.countTriggers(note));
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("Retourne 0 si texte null")
    void countTriggers_null_returns_zero(String note) {
        assertEquals(0, TriggerTerms.countTriggers(note));
    }
}