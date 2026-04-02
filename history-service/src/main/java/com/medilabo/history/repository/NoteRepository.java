package com.medilabo.history.repository;

import com.medilabo.history.domain.Note;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NoteRepository extends MongoRepository<Note, String> {
    List<Note> findByPatientIdOrderByCreatedAtDesc(Long patientId);
}