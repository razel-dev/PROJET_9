package com.medilabo.front.controller;

import com.medilabo.front.dto.PatientDto;
import com.medilabo.front.service.FrontViewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitaires (MockMvc, standaloneSetup) pour PatientsController.
 * Objectifs:
 * - vérifier les vues retournées pour GET /patients et GET /patients/new,
 * - vérifier la redirection après POST /patients avec payload valide,
 * - vérifier la réaffectation de la vue "create" avec message d'erreur pour payload invalide,
 * - vérifier le comportement d'ajout de note (appel service si contenu non vide).
 */
class PatientsControllerTest {

    private FrontViewService viewService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        viewService = mock(FrontViewService.class);
        PatientsController controller = new PatientsController(viewService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    /**
     * GET /patients
     * Doit:
     * - retourner HTTP 200,
     * - rendre la vue "patients/list",
     * - poser l'attribut de modèle "patients",
     * - appeler FrontViewService#listPatients().
     */
    @Test
    void getPatients_returnsListView() throws Exception {
        when(viewService.listPatients()).thenReturn(List.of());

        mockMvc.perform(get("/patients"))
                .andExpect(status().isOk())
                .andExpect(view().name("patients/list"))
                .andExpect(model().attributeExists("patients"));

        verify(viewService).listPatients();
    }

    /**
     * GET /patients/new
     * Doit:
     * - retourner HTTP 200,
     * - rendre la vue "patients/create".
     */
    @Test
    void getNewPatient_returnsCreateView() throws Exception {
        mockMvc.perform(get("/patients/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("patients/create"));
    }

    /**
     * POST /patients avec un payload valide
     * Doit:
     * - appeler FrontViewService#createPatient(...),
     * - rediriger vers "/patients/{id}" (HTTP 302).
     */
    @Test
    void postPatients_validPayload_redirectsToDetails() throws Exception {
        var created = new PatientDto(
                123L, "John", "Doe", LocalDate.parse("1990-01-01"), "M", null, null
        );
        when(viewService.createPatient(any())).thenReturn(created);

        mockMvc.perform(post("/patients")
                        .param("prenom", "John")
                        .param("nom", "Doe")
                        .param("dateDeNaissance", "1990-01-01")
                        .param("genre", "M")
                        .param("adressePostale", "")
                        .param("numeroTelephone", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/patients/123"));

        verify(viewService).createPatient(ArgumentMatchers.any());
    }

    /**
     * POST /patients avec un payload invalide (nom vide)
     * Doit:
     * - ne pas appeler FrontViewService#createPatient(...),
     * - retourner HTTP 200,
     * - rendre la vue "patients/create",
     * - poser "errorMessage" dans le modèle.
     */
    @Test
    void postPatients_invalidPayload_returnsCreateViewWithError() throws Exception {
        // nom vide -> erreur de validation attendue
        mockMvc.perform(post("/patients")
                        .param("prenom", "John")
                        .param("nom", "")
                        .param("dateDeNaissance", "1990-01-01")
                        .param("genre", "M"))
                .andExpect(status().isOk())
                .andExpect(view().name("patients/create"))
                .andExpect(model().attributeExists("errorMessage"));

        verify(viewService, never()).createPatient(any());
    }

    /**
     * POST /patients/{id}/notes avec un contenu vide (après trim)
     * Doit:
     * - ne pas appeler FrontViewService#addNote(...),
     * - rediriger vers "/patients/{id}".
     */
    @Test
    void postNote_blankContent_doesNotCallService() throws Exception {
        mockMvc.perform(post("/patients/5/notes")
                        .param("content", "   "))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/patients/5"));

        verify(viewService, never()).addNote(any());
    }

    /**
     * POST /patients/{id}/notes avec un contenu non vide
     * Doit:
     * - appeler FrontViewService#addNote(...),
     * - rediriger vers "/patients/{id}".
     */
    @Test
    void postNote_validContent_redirectsToDetails() throws Exception {
        mockMvc.perform(post("/patients/7/notes")
                        .param("content", "Nouvelle note"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/patients/7"));

        verify(viewService).addNote(any());
    }
}