package com.JaimeAmuedoJAH.backend.ratelimit;

import com.JaimeAmuedoJAH.backend.exceptions.TooManyRequestsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RateLimitService {

    private final ConcurrentHashMap<String, RequestCounter> counters = new ConcurrentHashMap<>();

    /**
     * Check if the request should be allowed based on rate limiting rules
     * @param key Unique identifier (usually IP address or user email)
     * @param maxAttempts Maximum attempts allowed
     * @param windowSizeSeconds Time window in seconds
     * @return true if request is allowed, throws TooManyRequestsException if not
     */
    public boolean allowRequest(String key, int maxAttempts, int windowSizeSeconds) {
        long currentTime = System.currentTimeMillis();
        long windowStart = currentTime - TimeUnit.SECONDS.toMillis(windowSizeSeconds);

        counters.compute(key, (k, counter) -> {
            if (counter == null) {
                counter = new RequestCounter(currentTime, 1);
            } else if (counter.windowStart < windowStart) {
                // New window, reset counter
                counter = new RequestCounter(currentTime, 1);
            } else {
                // Same window, increment counter
                counter.attempts++;
                counter.lastRequestTime = currentTime;
            }
            return counter;
        });

        RequestCounter counter = counters.get(key);
        
        if (counter.attempts > maxAttempts) {
            log.warn("Rate limit exceeded for key: {} with {} attempts", key, counter.attempts);
            throw new TooManyRequestsException(
                    "Too many requests. Maximum " + maxAttempts + " attempts per " + windowSizeSeconds + " seconds."
            );
        }

        return true;
    }

    /**
     * Get the current attempt count for a key
     */
    public int getAttemptCount(String key) {
        RequestCounter counter = counters.get(key);
        return counter != null ? counter.attempts : 0;
    }

    /**
     * Reset the counter for a specific key
     */
    public void resetCounter(String key) {
        counters.remove(key);
        log.info("Rate limit counter reset for key: {}", key);
    }

    /**
     * Clean up old entries to prevent memory leaks
     * Should be called periodically
     */
    public void cleanup() {
        long currentTime = System.currentTimeMillis();
        long maxAge = TimeUnit.HOURS.toMillis(1); // Remove entries older than 1 hour
        
        counters.entrySet().removeIf(entry -> 
            (currentTime - entry.getValue().lastRequestTime) > maxAge
        );
        
        log.debug("Rate limit counters cleanup completed");
    }

    /**
     * Inner class to track request attempts
     */
    private static class RequestCounter {
        long windowStart;
        int attempts;
        long lastRequestTime;

        RequestCounter(long windowStart, int attempts) {
            this.windowStart = windowStart;
            this.attempts = attempts;
            this.lastRequestTime = windowStart;
        }
    }
}
