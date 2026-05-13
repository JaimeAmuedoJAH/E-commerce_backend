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

    /**
     * Convert CVV to hashed database value
     * CVV is one-way hashed - it cannot be decrypted
     */
    @Override
    public String convertToDatabaseColumn(String cvv) {
        if (cvv == null || cvv.isEmpty()) {
            return cvv;
        }
        return passwordEncoder.encode(cvv);
    }

    /**
     * Note: Cannot convert back from hashed CVV to plaintext
     * This is intentional for security - CVV should only be used during payment validation
     */
    @Override
    public String convertToEntityAttribute(String hashedCvv) {
        // Return null because we cannot decrypt a hashed CVV
        // This ensures CVV is never accidentally exposed after storage
        return null;
    }
}
