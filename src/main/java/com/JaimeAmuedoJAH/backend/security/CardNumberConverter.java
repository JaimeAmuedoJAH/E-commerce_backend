package com.JaimeAmuedoJAH.backend.security;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * JPA AttributeConverter to automatically encrypt/decrypt card numbers
 * Applied to any field annotated with @Convert(converter = CardNumberConverter.class)
 */
@Component
@Converter
@RequiredArgsConstructor
public class CardNumberConverter implements AttributeConverter<String, String> {

    private final EncryptionUtil encryptionUtil;

    /**
     * Convert card number to encrypted database value
     */
    @Override
    public String convertToDatabaseColumn(String cardNumber) {
        if (cardNumber == null || cardNumber.isEmpty()) {
            return cardNumber;
        }
        return encryptionUtil.encrypt(cardNumber);
    }

    /**
     * Convert encrypted database value to card number
     */
    @Override
    public String convertToEntityAttribute(String encryptedCardNumber) {
        if (encryptedCardNumber == null || encryptedCardNumber.isEmpty()) {
            return encryptedCardNumber;
        }
        return encryptionUtil.decrypt(encryptedCardNumber);
    }
}
