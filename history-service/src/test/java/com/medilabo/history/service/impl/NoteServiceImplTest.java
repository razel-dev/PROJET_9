package com.medilabo.history.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.medilabo.history.domain.Note;
import com.medilabo.history.repository.NoteRepository;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class NoteServiceImplTest {

    @Mock
    private NoteRepository repository;

    @InjectMocks
    private NoteServiceImpl service;

    @BeforeEach
    void setup() {
        // Ce stub n'est pas utilisé par tous les tests => rendre lenient pour éviter UnnecessaryStubbingException
        lenient().when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void findByPatientIdDesc_returnsNotesOrderedByCreatedAtDesc() {
        Long patientId = 42L;
        Instant t1 = Instant.parse("2024-01-01T10:00:00Z");
        Instant t2 = Instant.parse("2024-02-01T10:00:00Z");
        Instant t3 = Instant.parse("2024-03-01T10:00:00Z");

        Note n1 = new Note(); n1.setPatientId(patientId); n1.setCreatedAt(t3);
        Note n2 = new Note(); n2.setPatientId(patientId); n2.setCreatedAt(t2);
        Note n3 = new Note(); n3.setPatientId(patientId); n3.setCreatedAt(t1);

        List<Note> expected = List.of(n1, n2, n3);
        when(repository.findByPatientIdOrderByCreatedAtDesc(patientId)).thenReturn(expected);

        List<Note> result = service.findByPatientIdDesc(patientId);

        assertEquals(expected, result);
    }

    @Test
    void create_throwsException_whenNoteIsNull() {
        assertThrows(IllegalArgumentException.class, () -> service.create(null));
    }

    @Test
    void create_resetsId_toAvoidUpdate() {
        Note toCreate = new Note();
        toCreate.setId("999");
        toCreate.setPatientId(1L);
        toCreate.setContent("test");

        Note saved = service.create(toCreate);

        assertNull(saved.getId());
    }

    @Test
    void create_trimsContentBeforeSave() {
        Note toCreate = new Note();
        toCreate.setPatientId(1L);
        toCreate.setContent("   contenu avec espaces   ");

        Note saved = service.create(toCreate);

        assertEquals("contenu avec espaces", saved.getContent());
    }

    @Test
    void create_trimsAuthorAndSetsNullIfBlank() {
        Note withAuthor = new Note();
        withAuthor.setPatientId(1L);
        withAuthor.setContent("c");
        withAuthor.setAuthor("  Docteur X  ");

        Note saved1 = service.create(withAuthor);
        assertEquals("Docteur X", saved1.getAuthor());

        Note blankAuthor = new Note();
        blankAuthor.setPatientId(1L);
        blankAuthor.setContent("c");
        blankAuthor.setAuthor("    ");

        Note saved2 = service.create(blankAuthor);
        assertNull(saved2.getAuthor());
    }

    @Test
    void create_setsCreatedAt_whenMissing() {
        Note toCreate = new Note();
        toCreate.setPatientId(1L);
        toCreate.setContent("c");

        Instant before = Instant.now();
        Note saved = service.create(toCreate);
        Instant after = Instant.now();

        assertNotNull(saved.getCreatedAt());
        assertFalse(saved.getCreatedAt().isBefore(before) || saved.getCreatedAt().isAfter(after));
    }

    @Test
    void create_preservesCreatedAt_whenProvided() {
        Note toCreate = new Note();
        toCreate.setPatientId(1L);
        toCreate.setContent("c");
        Instant provided = Instant.parse("2020-05-20T12:34:56Z");
        toCreate.setCreatedAt(provided);

        Note saved = service.create(toCreate);

        assertEquals(provided, saved.getCreatedAt());
    }
}