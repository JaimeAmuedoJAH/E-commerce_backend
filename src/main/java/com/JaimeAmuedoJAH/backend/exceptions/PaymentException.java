package com.JaimeAmuedoJAH.backend.exceptions;

import org.springframework.http.HttpStatus;

public class PaymentException extends ApiException {

    public PaymentException(String message) {
        super(message, HttpStatus.PAYMENT_REQUIRED);
    }
}
