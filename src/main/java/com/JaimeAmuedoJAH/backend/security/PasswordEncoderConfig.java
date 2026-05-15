package com.JaimeAmuedoJAH.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuración independiente del PasswordEncoder para evitar dependencias circulares.
 * Al estar separado de SecurityConfig, los converters JPA pueden inyectarlo
 * sin crear un ciclo con la cadena de seguridad.
 */
@Configuration
public class PasswordEncoderConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}