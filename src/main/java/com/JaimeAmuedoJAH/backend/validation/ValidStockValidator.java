package com.JaimeAmuedoJAH.backend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidStockValidator implements ConstraintValidator<ValidStock, Integer> {

    @Override
    public void initialize(ValidStock constraintAnnotation) {
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Null values are handled by @NotNull
        }

        // Stock must be non-negative
        return value >= 0;
    }
}
