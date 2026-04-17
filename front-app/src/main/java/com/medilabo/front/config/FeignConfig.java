package com.medilabo.front.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

@Configuration
@EnableFeignClients(basePackages = "com.medilabo.front.client")
public class FeignConfig {

    // Cette version utilise OAuth2AuthorizedClientManager qui :
    // - Récupère automatiquement le client autorisé pour l'utilisateur courant
    // - Rafraîchit le jeton d'accès si nécessaire (via refresh_token) avant l'appel Feign
    // - Évite d'avoir à manipuler manuellement la session HTTP ou l'OAuth2AuthorizedClientService
    // Ainsi, les appels Feign disposent toujours d'un Bearer valide sans code supplémentaire.
    @Bean
    RequestInterceptor oauth2TokenRelayInterceptor(OAuth2AuthorizedClientManager authorizedClientManager) {
        return (RequestTemplate template) -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (!(auth instanceof OAuth2AuthenticationToken oat)) {
                return;
            }

            OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                    .withClientRegistrationId(oat.getAuthorizedClientRegistrationId())
                    .principal(oat)
                    .build();

            OAuth2AuthorizedClient client = authorizedClientManager.authorize(authorizeRequest);
            // Si le jeton a expiré, 'authorize' déclenchera un refresh (si possible) avant de retourner le client
            if (client != null && client.getAccessToken() != null) {
                template.header("Authorization", "Bearer " + client.getAccessToken().getTokenValue());
            }
        };
    }
}