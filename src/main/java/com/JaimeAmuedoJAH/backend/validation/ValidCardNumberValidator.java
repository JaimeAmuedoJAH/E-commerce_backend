package com.JaimeAmuedoJAH.backend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidCardNumberValidator implements ConstraintValidator<ValidCardNumber, String> {

    @Override
    public void initialize(ValidCardNumber constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Null values are handled by @NotNull
        }

        // Remove spaces and hyphens
        String cleaned = value.replaceAll("\\s|-", "");

        // Check if it's 16 digits
        if (!cleaned.matches("\\d{16}")) {
            return false;
        }

        // Luhn Algorithm validation
        return isValidLuhn(cleaned);
    }

    private boolean isValidLuhn(String cardNumber) {
        int sum = 0;
        boolean isSecondDigit = false;

        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(cardNumber.charAt(i));

            if (isSecondDigit) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }

            sum += digit;
            isSecondDigit = !isSecondDigit;
        }

        return sum % 10 == 0;
    }
}
