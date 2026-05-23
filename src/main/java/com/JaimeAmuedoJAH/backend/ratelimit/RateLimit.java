package com.JaimeAmuedoJAH.backend.ratelimit;

import java.lang.annotation.*;

/**
 * Annotation to enable rate limiting on controller methods.
 * Usage: @RateLimit(maxAttempts = 5, windowSizeSeconds = 60)
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

    /**
     * Maximum number of requests allowed within the time window
     */
    int maxAttempts() default 10;

    /**
     * Time window in seconds
     */
    int windowSizeSeconds() default 60;
}