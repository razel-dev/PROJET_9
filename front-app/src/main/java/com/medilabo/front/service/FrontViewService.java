package com.medilabo.front.service;

import com.medilabo.front.client.AssessmentGatewayClient;
import com.medilabo.front.client.HistoryGatewayClient;
import com.medilabo.front.client.PatientGatewayClient;
import com.medilabo.front.dto.AssessmentDto;
import com.medilabo.front.dto.NoteDto;
import com.medilabo.front.dto.PatientDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import feign.FeignException;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class FrontViewService {

    private final PatientGatewayClient patients;
    private final HistoryGatewayClient history;
    private final AssessmentGatewayClient assessment;

    public FrontViewService(PatientGatewayClient patients, HistoryGatewayClient history, AssessmentGatewayClient assessment) {
        this.patients = patients;
        this.history = history;
        this.assessment = assessment;
    }

    public List<PatientDto> listPatients() {
        try {
            return patients.list();
        } catch (FeignException | IllegalStateException e) {
            log.error("Echec d'appel au service patients (list): {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    public PatientDto getPatient(Long id) {
        try {
            return patients.get(id);
        } catch (FeignException.NotFound e) {
            log.warn("Patient {} introuvable: {}", id, e.getMessage());
            return null;
        } catch (FeignException | IllegalStateException e) {
            log.error("Echec d'appel au service patients (get {}): {}", id, e.getMessage(), e);
            return null;
        }
    }

    public List<NoteDto> getNotesForPatient(Long patientId) {
        try {
            return history.findByPatient(patientId);
        } catch (FeignException.NotFound e) {
            log.warn("Notes pour patient {} introuvables: {}", patientId, e.getMessage());
            return Collections.emptyList();
        } catch (FeignException | IllegalStateException e) {
            log.error("Echec d'appel au service history (findByPatient {}): {}", patientId, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    public AssessmentDto getAssessment(Long patientId) {
        try {
            return assessment.getByPatient(patientId);
        } catch (FeignException.NotFound e) {
            log.warn("Assessment pour patient {} introuvable: {}", patientId, e.getMessage());
            return null;
        } catch (FeignException | IllegalStateException e) {
            log.error("Echec d'appel au service assessment (patient {}): {}", patientId, e.getMessage(), e);
            return null;
        }
    }


    public NoteDto addNote(NoteDto payload) {
        try {
            return history.create(payload);
        } catch (FeignException | IllegalStateException e) {
            log.error("Echec d'appel au service history (create): {}", e.getMessage(), e);
            throw new IllegalStateException("Impossible d'ajouter la note pour le moment, réessayez plus tard.");
        }
    }

}