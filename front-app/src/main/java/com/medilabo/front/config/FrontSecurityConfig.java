package com.medilabo.front.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;




@Configuration
public class FrontSecurityConfig {

    @Value("${app.keycloak.logout-uri:http://localhost:8080/realms/medilabo/protocol/openid-connect/logout}")
    private String keycloakLogoutUri;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/",
                    "/login/**",
                    "/oauth2/**",
                    "/actuator/**",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/favicon.ico",
                    "/error"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth -> oauth
                .defaultSuccessUrl("/", true)
            )
            .logout(logout -> logout
                .logoutSuccessHandler(oidcLogoutSuccessHandler())
            );

        return http.build();
    }

    @Bean
    LogoutSuccessHandler oidcLogoutSuccessHandler() {
        return (HttpServletRequest request, HttpServletResponse response, Authentication authentication) -> {
            String postLogoutRedirectUri = request.getScheme() + "://" + request.getServerName()
                    + ":" + request.getServerPort() + "/login";

            if (authentication != null && authentication.getPrincipal() instanceof OidcUser oidcUser) {
                String idToken = oidcUser.getIdToken().getTokenValue();
                String redirectUrl = keycloakLogoutUri
                        + "?id_token_hint=" + urlEncode(idToken)
                        + "&post_logout_redirect_uri=" + urlEncode(postLogoutRedirectUri);
                response.sendRedirect(redirectUrl);
                return;
            }

            response.sendRedirect(postLogoutRedirectUri);
        };
    }

    private String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
