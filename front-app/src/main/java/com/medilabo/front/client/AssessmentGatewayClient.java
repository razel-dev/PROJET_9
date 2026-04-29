package com.medilabo.front.client;

import com.medilabo.front.dto.AssessmentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "assessment-gateway", url = "${medilabo.gateway.base-url}")
public interface AssessmentGatewayClient {

    @GetMapping("/api/assessments/patient/{patientId}")
    AssessmentDto getByPatient(@PathVariable("patientId") Long patientId);
}