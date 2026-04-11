package com.medilabo.front.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class FrontSecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/",
                    "/login/**",
                    "/actuator/**",
                    "/webjars/**",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/fonts/**",
                    "/webfonts/**",
                    "/assets/**",
                    "/favicon.ico",
                    "/error"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth -> oauth
                .defaultSuccessUrl("/", true)
            );


        return http.build();
    }
}