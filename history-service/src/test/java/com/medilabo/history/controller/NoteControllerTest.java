package com.medilabo.history.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medilabo.history.domain.Note;
import com.medilabo.history.service.NoteService;
import com.medilabo.history.controller.NoteController;
import com.medilabo.history.dto.NoteDto;
import com.medilabo.history.mapper.NoteMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests Web MVC pour le contrôleur {@link NoteController}.
 * <p>
 * Cette classe utilise:
 * - {@link WebMvcTest} pour charger uniquement la couche web,
 * - {@link MockMvc} pour simuler des requêtes HTTP,
 * - des {@link MockBean} pour isoler le contrôleur de ses dépendances ({@link NoteService} et {@link NoteMapper}).
 * <p>
 * Les tests vérifient:
 * - la récupération des notes d'un patient (200 + tableau JSON),
 * - la création d'une note (201 + corps JSON),
 * - la validation des entrées invalides (400).
 */
@WebMvcTest(controllers = NoteController.class)
@AutoConfigureMockMvc(addFilters = false)
class NoteControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper json;

    @MockBean
    NoteService service;

    @MockBean
    NoteMapper mapper;

    @Test
    void listByPatient_returns200_andList() throws Exception {
        var dto1 = new NoteDto(); dto1.setPatientId(42L); dto1.setContent("A");
        var dto2 = new NoteDto(); dto2.setPatientId(42L); dto2.setContent("B");

        when(service.findByPatientIdDesc(42L)).thenReturn(List.of(new Note(), new Note()));
        when(mapper.toDtos(anyList())).thenReturn(List.of(dto1, dto2));

        mvc.perform(get("/api/notes/patient/{patientId}", 42L))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void create_returns201_andBody() throws Exception {
        var payload = new NoteDto(); payload.setPatientId(1L); payload.setContent("Suivi");
        var entity = new Note(); var saved = new Note();
        var response = new NoteDto(); response.setPatientId(1L); response.setContent("Suivi");

        when(mapper.toEntity(any(NoteDto.class))).thenReturn(entity);
        when(service.create(entity)).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(response);

        mvc.perform(post("/api/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.patientId").value(1L))
                .andExpect(jsonPath("$.content").value("Suivi"));
    }

    /**
     * Vérifie que l'endpoint POST /api/notes rejette un payload invalide (JSON vide "{}")
     * et renvoie un statut HTTP 400 (Bad Request) à cause des contraintes de validation
     * applicables au {@code NoteDto} (@NotNull patientId, @NotBlank content).
     *
     * @throws Exception en cas d'erreur d'exécution de la requête simulée
     */
    @Test
    void create_invalidPayload_returns400() throws Exception {
       
        mvc.perform(post("/api/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}