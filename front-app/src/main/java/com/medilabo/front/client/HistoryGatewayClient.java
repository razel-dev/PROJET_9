package com.medilabo.front.client;

import com.medilabo.front.dto.NoteDto;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "history-gateway", url = "${medilabo.gateway.base-url}")
public interface HistoryGatewayClient {

  @GetMapping("/api/notes/patient/{patientId}")
  List<NoteDto> findByPatient(@PathVariable("patientId") Long patientId);

  @PostMapping("/api/notes")
  NoteDto create(@RequestBody NoteDto payload);
}