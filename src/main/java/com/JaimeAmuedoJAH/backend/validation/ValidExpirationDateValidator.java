package com.JaimeAmuedoJAH.backend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ValidExpirationDateValidator implements ConstraintValidator<ValidExpirationDate, String> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");

    @Override
    public void initialize(ValidExpirationDate constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Null values are handled by @NotNull
        }

        try {
            // Parse the date in MM/yy format
            YearMonth expiry = YearMonth.parse(value, formatter);
            
            // Check if the expiration date is in the future (or current month)
            YearMonth now = YearMonth.now();
            
            return !expiry.isBefore(now);
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
