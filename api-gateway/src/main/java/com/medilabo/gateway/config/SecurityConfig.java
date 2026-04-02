package com.medilabo.gateway.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.util.StringUtils;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public ReactiveJwtDecoder jwtDecoder(Environment env) {
        // Lit d'abord l'issuer, sinon le JWK Set, sinon échoue avec un message explicite
        String issuer = env.getProperty("spring.security.oauth2.resourceserver.jwt.issuer-uri");
        String jwkSetUri = env.getProperty("spring.security.oauth2.resourceserver.jwt.jwk-set-uri");

        if (StringUtils.hasText(issuer)) {
            return NimbusReactiveJwtDecoder.withIssuerLocation(issuer).build();
        }
        if (StringUtils.hasText(jwkSetUri)) {
            return NimbusReactiveJwtDecoder.withJwkSetUri(jwkSetUri).build();
        }

        throw new IllegalStateException(
            "Configuration JWT manquante: définissez " +
            "'spring.security.oauth2.resourceserver.jwt.issuer-uri' ou " +
            "'spring.security.oauth2.resourceserver.jwt.jwk-set-uri' dans le profil actif."
        );
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers("/actuator/**").permitAll()
                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }
}