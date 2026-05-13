package com.JaimeAmuedoJAH.backend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

public class ValidPriceValidator implements ConstraintValidator<ValidPrice, Number> {

    @Override
    public void initialize(ValidPrice constraintAnnotation) {
    }

    @Override
    public boolean isValid(Number value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Null values are handled by @NotNull
        }

        // Convert to BigDecimal for precision
        BigDecimal decimal = new BigDecimal(value.toString());
        
        // Price must be greater than 0
        return decimal.compareTo(BigDecimal.ZERO) > 0;
    }
}
