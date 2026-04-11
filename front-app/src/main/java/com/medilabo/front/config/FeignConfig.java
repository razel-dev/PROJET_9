package com.medilabo.front.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

@Configuration
@EnableFeignClients(basePackages = "com.medilabo.front.client")
public class FeignConfig {

    // Relais du jeton d'accès de l'utilisateur connecté vers le Gateway
    @Bean
    RequestInterceptor oauth2TokenRelayInterceptor(OAuth2AuthorizedClientService clientService) {
        return (RequestTemplate template) -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth instanceof OAuth2AuthenticationToken oat) {
                OAuth2AuthorizedClient client =
                        clientService.loadAuthorizedClient(oat.getAuthorizedClientRegistrationId(), oat.getName());
                if (client != null && client.getAccessToken() != null) {
                    template.header("Authorization", "Bearer " + client.getAccessToken().getTokenValue());
                }
            }
        };
    }
}