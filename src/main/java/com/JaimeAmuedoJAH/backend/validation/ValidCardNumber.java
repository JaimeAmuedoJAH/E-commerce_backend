package com.JaimeAmuedoJAH.backend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidCardNumberValidator.class)
@Documented
public @interface ValidCardNumber {
    String message() default "Número de tarjeta inválido (debe tener 16 dígitos)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
