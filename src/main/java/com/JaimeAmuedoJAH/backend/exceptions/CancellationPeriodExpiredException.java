package com.JaimeAmuedoJAH.backend.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Excepción lanzada cuando se intenta cancelar una orden fuera del período permitido
 */
public class CancellationPeriodExpiredException extends ApiException {

    public CancellationPeriodExpiredException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public CancellationPeriodExpiredException(String message, int daysAllowed) {
        super(message + ". El período de cancelación es de " + daysAllowed + " días desde la creación de la orden.", HttpStatus.BAD_REQUEST);
    }
}
