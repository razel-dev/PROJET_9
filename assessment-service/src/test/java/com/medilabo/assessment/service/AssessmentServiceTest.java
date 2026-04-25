package com.medilabo.assessment.service;

import com.medilabo.assessment.model.RiskLevel;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

import com.medilabo.assessment.client.HistoryClient;
import com.medilabo.assessment.client.NoteDto;
import com.medilabo.assessment.client.PatientClient;
import com.medilabo.assessment.client.PatientDto;
import com.medilabo.assessment.dto.AssessmentDto;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import static org.mockito.Mockito.*;

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

    @Test
    void assess_withMocks_returns_expected_dto_and_metrics() {
        // Arrange: mocks
        PatientClient patientClient = mock(PatientClient.class);
        HistoryClient historyClient = mock(HistoryClient.class);
        AssessmentService service = new AssessmentService(patientClient, historyClient);

        Long patientId = 42L;
        LocalDate birthDate = LocalDate.now().minusYears(40); // > 30 ans (stable pour le risque)
        PatientDto patient = new PatientDto(
                patientId,
                "Jean",
                "Dupont",
                birthDate,
                "M",
                "1 rue de la Santé",
                "0102030405"
        );
        when(patientClient.get(patientId)).thenReturn(patient);

        // Notes: 6 déclencheurs DISTINCTS dans l’ensemble -> IN_DANGER pour >30 ans
        // Terme multiples et normalisation testées ici aussi
        List<NoteDto> notes = List.of(
                new NoteDto("n1", patientId, "doc", "Poids élevé, taille moyenne.", Instant.now()),                 // poids, taille
                new NoteDto("n2", patientId, "doc", "Microalbumine détectée, anormal.", Instant.now()),             // microalbumine, anormal
                new NoteDto("n3", patientId, "doc", "Taux de cholestérol, patient fumeur.", Instant.now()),         // cholesterol, fumeur
                new NoteDto("n4", patientId, "doc", null, Instant.now())                                            // null -> 0
        );
        when(historyClient.findByPatient(patientId)).thenReturn(notes);

        // Act
        AssessmentDto dto = service.assess(patientId);

        // Assert: valider intégration (âge, triggers, risque) et données patient recopiées
        int expectedAge = AssessmentService.computeAge(birthDate, LocalDate.now()); // même logique de calcul
        assertAll(
                () -> assertEquals(patientId, dto.patientId()),
                () -> assertEquals("Dupont", dto.nom()),
                () -> assertEquals("Jean", dto.prenom()),
                () -> assertEquals(birthDate, dto.dateDeNaissance()),
                () -> assertEquals("M", dto.genre()),
                () -> assertEquals(expectedAge, dto.age()),
                () -> assertEquals(6, dto.triggerCount()),
                () -> assertEquals(RiskLevel.IN_DANGER, dto.risk())
        );

        // Vérifier les interactions principales (orchestration)
        verify(patientClient).get(patientId);
        verify(historyClient).findByPatient(patientId);
        verifyNoMoreInteractions(patientClient, historyClient);
    }
}