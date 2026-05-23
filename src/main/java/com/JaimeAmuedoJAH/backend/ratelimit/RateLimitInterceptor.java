package com.JaimeAmuedoJAH.backend.ratelimit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimitService rateLimitService;

    /**
     * Set of trusted proxy IPs (e.g. your load balancer or reverse proxy).
     * Only requests coming from these IPs will have their X-Forwarded-For header trusted.
     * Configure in application.properties: rate-limit.trusted-proxies=10.0.0.1
     */
    private final Set<String> trustedProxies;

    public RateLimitInterceptor(
            RateLimitService rateLimitService,
            @Value("${rate-limit.trusted-proxies:}") String trustedProxiesConfig
    ) {
        this.rateLimitService = rateLimitService;
        this.trustedProxies = parseTrustedProxies(trustedProxiesConfig);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        RateLimit rateLimit = handlerMethod.getMethodAnnotation(RateLimit.class);
        if (rateLimit == null) {
            return true; // No rate limiting for this endpoint
        }

        String clientId  = getClientIdentifier(request);
        String endpoint  = request.getRequestURI();  
 
        int remaining = rateLimitService.allowRequest(
                clientId,
                endpoint,
                rateLimit.maxAttempts(),
                rateLimit.windowSizeSeconds()
        );

        // Standard rate limit response headers so clients know their quota
        response.setHeader("X-RateLimit-Limit",     String.valueOf(rateLimit.maxAttempts()));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(remaining));

        log.debug("Rate limit check passed for client: {} — {} attempts remaining", clientId, remaining);
        return true;
    }

    /**
     * Returns the real client IP.
     * X-Forwarded-For is only trusted when the direct caller is a known proxy,
     * preventing clients from spoofing their IP via that header.
     */
    private String getClientIdentifier(HttpServletRequest request) {
        // Intentar obtener el usuario autenticado del SecurityContext
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return "user:" + auth.getName();
        }
 
        // Endpoint público → usar IP (con protección frente a X-Forwarded-For spoofing)
        return "ip:" + resolveIp(request);
    }
 
    private String resolveIp(HttpServletRequest request) {
        String remoteAddr = request.getRemoteAddr();
        if (trustedProxies.contains(remoteAddr)) {
            String xff = request.getHeader("X-Forwarded-For");
            if (xff != null && !xff.isBlank()) {
                return xff.split(",")[0].trim();
            }
        }
        return remoteAddr;
    }

    private Set<String> parseTrustedProxies(String config) {
        if (config == null || config.isBlank()) {
            return new HashSet<>();
        }
        return new HashSet<>(Arrays.asList(config.split(",")));
    }
}