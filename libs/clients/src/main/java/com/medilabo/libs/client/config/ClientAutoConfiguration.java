package com.medilabo.libs.client.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientAutoConfiguration {

    @Bean(name = "patientWebClient")
    @ConditionalOnProperty(prefix = "medilabo.gateway", name = "base-url")
    @ConditionalOnMissingBean(name = "patientWebClient")
    public WebClient patientWebClient(@Value("${medilabo.gateway.base-url}") String baseUrl) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}