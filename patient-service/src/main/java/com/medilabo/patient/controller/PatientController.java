package com.medilabo.patient.controller;

import com.medilabo.patient.dto.PatientDto;
import com.medilabo.patient.mapper.PatientMapper;
import com.medilabo.patient.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.data.domain.PageRequest;
import com.medilabo.libs.commons.paging.PageResponse;
import com.medilabo.libs.commons.paging.Pages;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService service;
    private final PatientMapper mapper;

    @GetMapping
    public PageResponse<PatientDto> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        var pg = service.findAll(PageRequest.of(page, size));
        List<PatientDto> content = mapper.toDtos(pg.getContent());
        return Pages.of(content, pg.getTotalElements(), page, size);
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

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}