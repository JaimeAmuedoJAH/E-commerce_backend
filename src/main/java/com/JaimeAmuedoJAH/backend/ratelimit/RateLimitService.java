package com.JaimeAmuedoJAH.backend.ratelimit;

import com.JaimeAmuedoJAH.backend.exceptions.TooManyRequestsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Slf4j
public class RateLimitService {

    private final Clock clock;

    static final int MAX_ENTRIES = 100_000;

    private final ConcurrentHashMap<String, RequestCounter> counters = new ConcurrentHashMap<>();

    public RateLimitService(Clock clock) {
        this.clock = clock;
    }

    /**
     * Comprueba si la petición debe ser permitida.
     *
     * La clave combina clientId + endpoint para que el contador de
     * /login no interfiera con el de /search, y viceversa.
     *
     * @param clientId          IP o userId del cliente
     * @param endpoint          URI del endpoint (e.g. "/api/auth/login")
     * @param maxAttempts       Máximo de intentos permitidos en la ventana
     * @param windowSizeSeconds Tamaño de la ventana en segundos
     * @return Intentos restantes (>= 0)
     * @throws TooManyRequestsException si se supera el límite
     */
    public int allowRequest(String clientId, String endpoint, int maxAttempts, int windowSizeSeconds) {
        // rechazar antes de insertar si el mapa está lleno
        if (counters.size() >= MAX_ENTRIES) {
            log.error("Rate limit map full ({} entries) — rejecting request from {}", MAX_ENTRIES, clientId);
            throw new TooManyRequestsException(
                    "Service temporarily unavailable. Please try again later.",
                    windowSizeSeconds
            );
        }

        long now        = clock.millis();
        long windowSize = TimeUnit.SECONDS.toMillis(windowSizeSeconds);

        // clave por cliente + endpoint
        String bucketKey = clientId + "|" + endpoint;

        // compute() atómico, resultado usado directamente (sin get() separado)
        RequestCounter counter = counters.compute(bucketKey, (k, existing) -> {
            if (existing == null || (now - existing.windowStart.get()) >= windowSize) {
                return new RequestCounter(now, 1);
            }
            existing.attempts.incrementAndGet();
            existing.lastRequestTime.set(now);
            return existing;
        });

        int attempts = counter.attempts.get();

        if (attempts > maxAttempts) {
            long windowStart   = counter.windowStart.get();
            long resetAt       = windowStart + windowSize;
            int retryAfterSec  = (int) Math.max(1, TimeUnit.MILLISECONDS.toSeconds(resetAt - now));

            log.warn("Rate limit exceeded — client: {}, endpoint: {}, attempts: {}/{}",
                    clientId, endpoint, attempts, maxAttempts);

            throw new TooManyRequestsException(
                    "Too many requests. Maximum " + maxAttempts +
                    " attempts per " + windowSizeSeconds + " seconds.",
                    retryAfterSec   // tiempo exacto hasta que se libera la ventana
            );
        }

        return maxAttempts - attempts;
    }

    public int getAttemptCount(String clientId, String endpoint) {
        RequestCounter counter = counters.get(clientId + "|" + endpoint);
        return counter != null ? counter.attempts.get() : 0;
    }

    public void resetCounter(String clientId, String endpoint) {
        String key = clientId + "|" + endpoint;
        counters.remove(key);
        log.info("Rate limit counter reset — key: {}", key);
    }

    /**
     * Elimina entradas sin actividad en la última hora.
     * Llamado por el @Scheduled en RateLimitConfig.
     */
    public void cleanup() {
        long now    = clock.millis();
        long maxAge = TimeUnit.HOURS.toMillis(1);

        int before = counters.size();
        counters.entrySet().removeIf(e ->
                (now - e.getValue().lastRequestTime.get()) > maxAge
        );
        int removed = before - counters.size();

        if (removed > 0) {
            log.debug("Rate limit cleanup: {} entradas eliminadas ({} restantes)", removed, counters.size());
        }
    }

    // -------------------------------------------------------------------------

    private static final class RequestCounter {
        final AtomicLong    windowStart;
        final AtomicInteger attempts;
        final AtomicLong    lastRequestTime;

        RequestCounter(long windowStart, int initialAttempts) {
            this.windowStart     = new AtomicLong(windowStart);
            this.attempts        = new AtomicInteger(initialAttempts);
            this.lastRequestTime = new AtomicLong(windowStart);
        }
    }
}