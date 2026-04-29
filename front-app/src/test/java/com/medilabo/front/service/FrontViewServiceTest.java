package com.medilabo.front.service;

import com.medilabo.front.client.AssessmentGatewayClient;
import com.medilabo.front.client.HistoryGatewayClient;
import com.medilabo.front.client.PatientGatewayClient;
import com.medilabo.front.dto.AssessmentDto;
import com.medilabo.front.dto.PatientDto;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class FrontViewServiceTest {

    private PatientGatewayClient patients;
    private HistoryGatewayClient history;
    private AssessmentGatewayClient assessment;
    private FrontViewService service;

    @BeforeEach
    void setUp() {
        patients = mock(PatientGatewayClient.class);
        history = mock(HistoryGatewayClient.class);
        assessment = mock(AssessmentGatewayClient.class);
        service = new FrontViewService(patients, history, assessment);
    }

    @Test
    void getPatient_returnsNull_when404() {
        Long id = 42L;
        when(patients.get(id)).thenThrow(mock(FeignException.NotFound.class));

        assertNull(service.getPatient(id));
        verify(patients).get(id);
    }

    @Test
    void getAssessment_returnsNull_when404() {
        Long id = 99L;
        when(assessment.getByPatient(id)).thenThrow(mock(FeignException.NotFound.class));

        AssessmentDto result = service.getAssessment(id);

        assertNull(result);
        verify(assessment).getByPatient(id);
    }

    @Test
    void createPatient_throwsFriendlyMessage_when500() {
        PatientDto payload = new PatientDto(
                null,
                "John",
                "Doe",
                LocalDate.parse("1990-01-01"),
                "M",
                null,
                null
        );
        when(patients.create(payload)).thenThrow(mock(FeignException.InternalServerError.class));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.createPatient(payload));

        assertEquals("Le service patient a refuse la creation. Verifiez le formulaire.", ex.getMessage());
        verify(patients).create(payload);
    }
}