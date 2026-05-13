package com.JaimeAmuedoJAH.backend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidExpirationDateValidator.class)
@Documented
public @interface ValidExpirationDate {
    String message() default "Fecha de expiración inválida (formato: MM/AA, debe ser futura)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
