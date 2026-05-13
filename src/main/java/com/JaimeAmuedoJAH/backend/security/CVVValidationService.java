package com.JaimeAmuedoJAH.backend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service for validating CVV without ever exposing it
 * Compares provided CVV with stored hash
 */
@Service
@RequiredArgsConstructor
public class CVVValidationService {

    private final PasswordEncoder passwordEncoder;

    /**
     * Validate a provided CVV against a stored hashed CVV
     * Does NOT decrypt anything - uses hash comparison
     *
     * @param providedCVV The CVV provided by user
     * @param hashedCVV The hashed CVV stored in database
     * @return true if CVV matches, false otherwise
     */
    public boolean validateCVV(String providedCVV, String hashedCVV) {
        if (providedCVV == null || hashedCVV == null) {
            return false;
        }
        return passwordEncoder.matches(providedCVV, hashedCVV);
    }
}
