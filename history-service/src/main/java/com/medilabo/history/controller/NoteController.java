package com.medilabo.history.controller;

import com.medilabo.history.dto.NoteDto;
import com.medilabo.history.mapper.NoteMapper;
import com.medilabo.history.service.NoteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService service;
    private final NoteMapper mapper;



    @GetMapping("/patient/{patientId}")
    public List<NoteDto> listByPatient(@PathVariable Long patientId) {
        return mapper.toDtos(service.findByPatientIdDesc(patientId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NoteDto create(@Valid @RequestBody NoteDto payload) {
        return mapper.toDto(service.create(mapper.toEntity(payload)));
    }
}