package com.medilabo.front.client;

import com.medilabo.front.dto.PatientDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
    name = "patient-gateway",
    url = "${medilabo.gateway.base-url}"
)
public interface PatientGatewayClient {

    @GetMapping("/api/patients")
    List<PatientDto> list();

    @GetMapping("/api/patients/{id}")
    PatientDto get(@PathVariable("id") Long id);

    @PostMapping("/api/patients")
    PatientDto create(@RequestBody PatientDto payload);

    @PutMapping("/api/patients/{id}")
    PatientDto update(@PathVariable("id") Long id, @RequestBody PatientDto payload);

    @DeleteMapping("/api/patients/{id}")
    void delete(@PathVariable("id") Long id);
}