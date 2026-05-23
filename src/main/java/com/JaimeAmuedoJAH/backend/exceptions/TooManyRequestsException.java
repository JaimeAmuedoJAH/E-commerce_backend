package com.JaimeAmuedoJAH.backend.exceptions;

/**
 * FIX 5 — La excepción ahora lleva retryAfterSeconds para que el
 * GlobalExceptionHandler pueda incluir el header Retry-After en la respuesta.
 */
public class TooManyRequestsException extends RuntimeException {

    private final int retryAfterSeconds;

    public TooManyRequestsException(String message, int retryAfterSeconds) {
        super(message);
        this.retryAfterSeconds = retryAfterSeconds;
    }

    /** Compatibilidad con el constructor original sin retryAfter */
    public TooManyRequestsException(String message) {
        this(message, 60);
    }

    public int getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}