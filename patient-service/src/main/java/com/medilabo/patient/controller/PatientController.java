package com.medilabo.patient.controller;

import com.medilabo.patient.dto.PatientDto;
import com.medilabo.patient.mapper.PatientMapper;
import com.medilabo.patient.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService service;
    private final PatientMapper mapper;

    @GetMapping
    public List<PatientDto> list() {

        return mapper.toDtos(service.findAll());
    }

    @GetMapping("/{id}")
    public PatientDto get(@PathVariable Long id) {
        return mapper.toDto(service.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PatientDto create(@Valid @RequestBody PatientDto payload) {
        return mapper.toDto(service.create(mapper.toEntity(payload)));
    }

    @PutMapping("/{id}")
    public PatientDto update(@PathVariable Long id, @Valid @RequestBody PatientDto payload) {
        return mapper.toDto(service.update(id, mapper.toEntity(payload)));
    }

    // PATCH: mise à jour partielle — n’écrase pas les autres champs
    @PatchMapping("/{id}")
    public PatientDto patch(@PathVariable Long id, @RequestBody PatientDto payload) {
        // Mise à jour partielle: seuls les champs non nuls du DTO sont appliqués
        return mapper.toDto(service.updatePartial(id, payload));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}