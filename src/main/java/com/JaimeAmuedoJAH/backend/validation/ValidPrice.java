package com.JaimeAmuedoJAH.backend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidPriceValidator.class)
@Documented
public @interface ValidPrice {
    String message() default "El precio debe ser mayor que 0";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
