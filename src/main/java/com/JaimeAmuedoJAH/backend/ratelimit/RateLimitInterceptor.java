package com.JaimeAmuedoJAH.backend.ratelimit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimitService rateLimitService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RateLimit rateLimit = handlerMethod.getMethodAnnotation(RateLimit.class);

        if (rateLimit == null) {
            return true; // No rate limiting for this endpoint
        }

        // Get client identifier (IP address or user email)
        String clientId = getClientIdentifier(request);
        
        // Check rate limit
        rateLimitService.allowRequest(
                clientId,
                rateLimit.maxAttempts(),
                rateLimit.windowSizeSeconds()
        );

        log.debug("Rate limit check passed for client: {} (attempts: {})",
                clientId, rateLimitService.getAttemptCount(clientId));

        return true;
    }

    /**
     * Get client identifier (IP address is preferred, fallback to user email)
     */
    private String getClientIdentifier(HttpServletRequest request) {
        // Try to get IP from X-Forwarded-For header (for proxies)
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        // Get direct client IP
        return request.getRemoteAddr();
    }
}
