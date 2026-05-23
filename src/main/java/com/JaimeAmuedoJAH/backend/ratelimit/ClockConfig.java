package com.JaimeAmuedoJAH.backend.ratelimit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * Configuración separada para el bean Clock.
 * Necesario para evitar dependencia circular:
 * RateLimitConfig → RateLimitService → Clock → RateLimitConfig
 */
@Configuration
public class ClockConfig {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}