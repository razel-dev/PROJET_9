package com.medilabo.assessment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "history-service", url = "${services.history.base-url}")
public interface HistoryClient {
    @GetMapping("/api/notes/patient/{patientId}")
    List<NoteDto> findByPatient(@PathVariable("patientId") Long patientId);
}
