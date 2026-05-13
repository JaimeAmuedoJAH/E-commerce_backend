package com.JaimeAmuedoJAH.backend.ratelimit;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
@EnableScheduling
public class RateLimitConfig implements WebMvcConfigurer {

    private final RateLimitInterceptor rateLimitInterceptor;
    private final RateLimitService rateLimitService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/swagger-ui/**", "/v3/api-docs/**");
    }

    /**
     * Scheduled task to clean up old rate limit entries
     * Runs every hour to prevent memory leaks
     */
    @Scheduled(fixedDelay = 3600000) // 1 hour
    public void cleanupRateLimitCounters() {
        rateLimitService.cleanup();
    }
}
