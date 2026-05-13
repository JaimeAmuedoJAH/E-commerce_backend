package com.JaimeAmuedoJAH.backend.exceptions;

import org.springframework.http.HttpStatus;

public class OutOfStockException extends ApiException {

    public OutOfStockException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
