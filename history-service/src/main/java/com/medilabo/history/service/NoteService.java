package com.medilabo.history.service;

import com.medilabo.history.domain.Note;

import java.util.List;

public interface NoteService {
    List<Note> findByPatientIdDesc(Long patientId);
    Note create(Note toCreate);
}