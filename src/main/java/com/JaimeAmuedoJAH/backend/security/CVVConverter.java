package com.JaimeAmuedoJAH.backend.security;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * JPA AttributeConverter to automatically hash CVV codes
 * CVV should NEVER be decrypted - it should be hashed and compared only
 */
@Component
@Converter
@RequiredArgsConstructor
public class CVVConverter implements AttributeConverter<String, String> {

    private final PasswordEncoder passwordEncoder;

    @Override
    public String convertToDatabaseColumn(String cvv) {
        if (cvv == null || cvv.isEmpty()) {
            return cvv;
        }
        // Si ya es un hash BCrypt no lo volvemos a hashear
        if (cvv.startsWith("$2a$") || cvv.startsWith("$2b$") || cvv.startsWith("$2y$")) {
            return cvv;
        }
        return passwordEncoder.encode(cvv);
    }

    @Override
    public String convertToEntityAttribute(String hashedCvv) {
        return hashedCvv;
    }
}