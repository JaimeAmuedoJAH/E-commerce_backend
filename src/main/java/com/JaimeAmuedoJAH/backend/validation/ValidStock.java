package com.JaimeAmuedoJAH.backend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidStockValidator.class)
@Documented
public @interface ValidStock {
    String message() default "El stock no puede ser negativo";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
