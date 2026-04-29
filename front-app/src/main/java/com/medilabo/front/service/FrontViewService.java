package com.medilabo.front.service;

import com.medilabo.front.client.AssessmentGatewayClient;
import com.medilabo.front.client.HistoryGatewayClient;
import com.medilabo.front.client.PatientGatewayClient;
import com.medilabo.front.dto.AssessmentDto;
import com.medilabo.front.dto.NoteDto;
import com.medilabo.front.dto.PatientDto;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Service d'orchestration côté front.
 * <p>
 * Rôle:
 * - centraliser les appels aux services Patients, History et Assessment,
 * - appliquer une gestion d'erreurs homogène (logs, valeurs de repli, exceptions métier),
 * - exposer au contrôleur des méthodes simples, prêtes à afficher.
 *
 * Tolérance aux pannes:
 * - list/get patient: retourne liste vide ou null en cas d'échec/404,
 * - note: retourne liste vide en cas d'échec/404,
 * - assessment: retourne null en cas d'échec/404,
 * - create patient / add note: lève IllegalStateException avec message utilisateur.
 */
@Service
@Slf4j
public class FrontViewService {

    /** Client HTTP pour le service Patients. */
    private final PatientGatewayClient patients;
    /** Client HTTP pour le service History (notes). */
    private final HistoryGatewayClient history;
    /** Client HTTP pour le service Assessment (évaluations). */
    private final AssessmentGatewayClient assessment;

    /**
     * Construit le service d'orchestration.
     *
     * @param patients   client du service Patients
     * @param history    client du service History (notes)
     * @param assessment client du service Assessment
     */
    public FrontViewService(PatientGatewayClient patients,
                            HistoryGatewayClient history,
                            AssessmentGatewayClient assessment) {
        this.patients = patients;
        this.history = history;
        this.assessment = assessment;
    }

    /**
     * Récupère la liste des patients.
     *
     * Comportement en erreur: log l'exception et renvoie une liste vide.
     *
     * @return liste de patients (éventuellement vide)
     */
    public List<PatientDto> listPatients() {
        try {
            return patients.list();
        } catch (FeignException | IllegalStateException e) {
            log.error("Echec d'appel au service patients (list): {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * Récupère un patient par identifiant.
     *
     * Comportement:
     * - 404: log en avertissement et renvoie null,
     * - autre erreur: log et renvoie null.
     *
     * @param id identifiant du patient
     * @return le patient ou null si introuvable/erreur
     */
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

    /**
     * Récupère les notes d'un patient.
     *
     * Comportement:
     * - 404: log en avertissement et renvoie une liste vide,
     * - autre erreur: log et renvoie une liste vide.
     *
     * @param patientId identifiant du patient
     * @return liste des notes (éventuellement vide)
     */
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

    /**
     * Récupère l'évaluation d'un patient.
     *
     * Comportement:
     * - 404: log en avertissement et renvoie null,
     * - autre erreur: log et renvoie null.
     *
     * @param patientId identifiant du patient
     * @return évaluation ou null si indisponible
     */
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

    /**
     * Crée un patient.
     *
     * Comportement en erreur:
     * - 400: lève IllegalStateException avec message utilisateur (validation),
     * - 500: lève IllegalStateException avec message utilisateur (erreur serveur),
     * - autres: lève IllegalStateException générique.
     *
     * @param payload données du patient à créer
     * @return patient créé
     * @throws IllegalStateException si la création échoue
     */
    public PatientDto createPatient(PatientDto payload) {
        try {
            return patients.create(payload);
        } catch (FeignException.BadRequest e) {
            log.warn("Creation patient refusee: status={}, body={}", e.status(), e.contentUTF8(), e);
            throw new IllegalStateException("Les informations du patient sont invalides.");
        } catch (FeignException.InternalServerError e) {
            log.error("Creation patient en erreur: status={}, body={}", e.status(), e.contentUTF8(), e);
            throw new IllegalStateException("Le service patient a refuse la creation. Verifiez le formulaire.");
        } catch (FeignException | IllegalStateException e) {
            log.error("Echec d'appel au service patients (create): {}", e.getMessage(), e);
            throw new IllegalStateException("Impossible de creer le patient pour le moment, reessayez plus tard.");
        }
    }

    /**
     * Ajoute une note.
     *
     * Comportement en erreur:
     * - toute erreur réseau/serveur: lève IllegalStateException avec message utilisateur.
     *
     * @param payload note à créer
     * @return note créée
     * @throws IllegalStateException si la création échoue
     */
    public NoteDto addNote(NoteDto payload) {
        try {
            return history.create(payload);
        } catch (FeignException | IllegalStateException e) {
            log.error("Echec d'appel au service history (create): {}", e.getMessage(), e);
            throw new IllegalStateException("Impossible d'ajouter la note pour le moment, reessayez plus tard.");
        }
    }
}
