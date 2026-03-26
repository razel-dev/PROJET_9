package com.medilabo.libs.client.patient;

import com.medilabo.libs.client.patient.dto.PatientDto;
import com.medilabo.libs.commons.web.ErrorResponse;
import com.medilabo.libs.commons.paging.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PatientApiClient {

    private final WebClient webClient;

    @Autowired
    public PatientApiClient(@Qualifier("patientWebClient") WebClient webClient,
                            @Value("${medilabo.patient.base-path:/api/patients}") String basePath) {
        this.webClient = webClient;
        this.basePath = basePath;
    }

    @Value("${medilabo.patient.base-path:/api/patients}")
    private String basePath;

    private <T> Mono<T> mapErrors(WebClient.ResponseSpec spec, Class<T> type) {
        return spec.onStatus(
                        HttpStatusCode::isError,
                        resp -> resp.bodyToMono(ErrorResponse.class)
                                .defaultIfEmpty(new ErrorResponse("INTERNAL_ERROR", "Erreur inconnue"))
                                .flatMap(err -> Mono.error(new RuntimeException(err.message())))
                )
                .bodyToMono(type)
                .timeout(Duration.ofSeconds(5));
    }

    private <T> Mono<T> mapErrors(WebClient.ResponseSpec spec, ParameterizedTypeReference<T> typeRef) {
        return spec.onStatus(
                        HttpStatusCode::isError,
                        resp -> resp.bodyToMono(ErrorResponse.class)
                                .defaultIfEmpty(new ErrorResponse("INTERNAL_ERROR", "Erreur inconnue"))
                                .flatMap(err -> Mono.error(new RuntimeException(err.message())))
                )
                .bodyToMono(typeRef)
                .timeout(Duration.ofSeconds(5));
    }

    public List<PatientDto> list() {

        return mapErrors(
                webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path(basePath)
                                .build())
                        .retrieve(),
                new ParameterizedTypeReference<List<PatientDto>>() {}
        ).block();
    }

    public PatientDto get(Long id) {
        return mapErrors(
                webClient.get()
                        .uri(builder -> builder.path(basePath + "/{id}").build(id))
                        .retrieve(),
                PatientDto.class
        ).block();
    }

    public void create(PatientDto payload) {
        mapErrors(
                webClient.post()
                        .uri(builder -> builder.path(basePath).build())
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(payload)
                        .retrieve(),
                Void.class
        ).block();
    }

    public void update(Long id, PatientDto payload) {
        mapErrors(
                webClient.put()
                        .uri(builder -> builder.path(basePath + "/{id}").build(id))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(payload)
                        .retrieve(),
                Void.class
        ).block();
    }

    public void patch(Long id, Map<String, Object> fields) {
        // Mise à jour partielle: envoie uniquement les champs à modifier
        mapErrors(
                webClient.patch()
                        .uri(builder -> builder.path(basePath + "/{id}").build(id))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(fields)
                        .retrieve(),
                Void.class
        ).block();
    }
}